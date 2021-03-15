/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.jwt;

import com.mytiki.bouncer.features.latest.otp.OtpService;
import com.mytiki.bouncer.utilities.Constants;
import com.mytiki.common.exception.ApiExceptionFactory;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

public class JwtService {

    private final JWSSigner signer;
    private final JwtRepository jwtRepository;
    private final OtpService otpService;
    private final String kid;
    private static final Duration BEARER_DURATION = Duration.ofMinutes(5);
    private static final Duration REFRESH_DURATION = Duration.ofDays(30);

    private static final String FAILED_GRANT_MSG = "Failed to grant JWT";
    private static final String INVALID_REFRESH_MSG = "Invalid Refresh Token";
    private static final String EXPIRED_REFRESH_MSG = "Refresh Token expired. Re-login";

    //TODO routine to periodically clean up refresh tokens
    //TODO monitor refresh tokens for abuse

    public JwtService(String path, String kid, JwtRepository jwtRepository, OtpService otpService)
            throws IOException, ParseException, JOSEException {
        JWKSet localKeys = JWKSet.load(new File(path));
        ECKey privateKey = localKeys.getKeyByKeyId(kid).toECKey();
        this.signer = new ECDSASigner(privateKey);
        this.kid = kid;
        this.jwtRepository = jwtRepository;
        this.otpService = otpService;
    }

    public JwtAO refreshGrant(JwtAORefresh jwtAORefresh){
        Optional<JwtDO> jwtDO = jwtRepository.findByRefreshJwt(jwtAORefresh.getRefreshToken());
        if(jwtDO.isEmpty())
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, INVALID_REFRESH_MSG);

        jwtRepository.delete(jwtDO.get());

        if(jwtDO.get().getExpires().isBefore(ZonedDateTime.now(ZoneOffset.UTC)))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, EXPIRED_REFRESH_MSG);

        return grantNewJwt();
    }

    public JwtAO otpGrant(JwtAOOtp jwtAOOtp){
        otpService.redeem(jwtAOOtp.getOtp(), jwtAOOtp.getSalt());
        return grantNewJwt();
    }


    private JwtAO grantNewJwt(){
        JwtAO jwtAO = new JwtAO();

        ZonedDateTime iat = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime expBearer = iat.plus(BEARER_DURATION);
        ZonedDateTime expRefresh = iat.plus(REFRESH_DURATION);

        try {
            jwtAO.setAccessToken(generateJwt(iat, expBearer));
            jwtAO.setRefreshToken(generateJwt(iat, expRefresh));
        }catch (JOSEException e) {
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, FAILED_GRANT_MSG);
        }

        jwtAO.setTokenType(Constants.OAUTH_TOKEN_TYPE_BEARER);
        jwtAO.setExpiresIn(BEARER_DURATION.getSeconds());

        JwtDO jwtDO = new JwtDO();
        jwtDO.setRefreshJwt(jwtAO.getRefreshToken());
        jwtDO.setIssued(iat);
        jwtDO.setExpires(expRefresh);
        jwtRepository.save(jwtDO);
        return jwtAO;
    }


    private String generateJwt(ZonedDateTime iat, ZonedDateTime exp) throws JOSEException {
        JWSObject jwsObject = new JWSObject(
                new JWSHeader
                        .Builder(JWSAlgorithm.ES256)
                        .type(JOSEObjectType.JWT)
                        .keyID(kid)
                        .build(),
                new Payload(
                        new JWTClaimsSet.Builder()
                                .issuer(Constants.MODULE_DOT_PATH)
                                .issueTime(Date.from(iat.toInstant()))
                                .expirationTime(Date.from(exp.toInstant()))
                                .build()
                                .toJSONObject()
                ));

        jwsObject.sign(signer);
        return jwsObject.serialize();
    }
}
