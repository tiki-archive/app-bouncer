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
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Date;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class JwtService {
    private final JWSSigner signer;
    private final JwtRepository jwtRepository;
    private final OtpService otpService;
    private static final Duration BEARER_DURATION = Duration.ofMinutes(60);
    private static final Duration REFRESH_DURATION = Duration.ofDays(30);

    private static final String FAILED_GRANT_MSG = "Failed to grant JWT";
    private static final String INVALID_REFRESH_MSG = "Invalid Refresh Token";
    private static final String EXPIRED_REFRESH_MSG = "Refresh Token expired. Re-login";

    public JwtService(String privateKey, JwtRepository jwtRepository, OtpService otpService)
            throws JOSEException, NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        this.signer = new ECDSASigner(keyFactory.generatePrivate(encodedKeySpec), Curve.P_256);
        this.jwtRepository = jwtRepository;
        this.otpService = otpService;
    }

    public JwtAORsp refreshGrant(JwtAORefresh jwtAORefresh){
        Optional<JwtDO> jwtDO = jwtRepository.findByRefreshJwt(jwtAORefresh.getRefreshToken());
        if(jwtDO.isEmpty())
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, INVALID_REFRESH_MSG);

        jwtRepository.delete(jwtDO.get());

        if(jwtDO.get().getExpires().isBefore(ZonedDateTime.now(ZoneOffset.UTC)))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, EXPIRED_REFRESH_MSG);

        return grantNewJwt();
    }

    public JwtAORsp otpGrant(JwtAOOtp jwtAOOtp){
        otpService.redeem(jwtAOOtp.getOtp(), jwtAOOtp.getSalt());
        return grantNewJwt();
    }

    private JwtAORsp grantNewJwt(){
        JwtAORsp jwtAORsp = new JwtAORsp();

        ZonedDateTime iat = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime expBearer = iat.plus(BEARER_DURATION);
        ZonedDateTime expRefresh = iat.plus(REFRESH_DURATION);

        try {
            jwtAORsp.setAccessToken(generateJwt(iat, expBearer));
            jwtAORsp.setRefreshToken(generateJwt(iat, expRefresh));
        }catch (JOSEException e) {
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, FAILED_GRANT_MSG);
        }

        jwtAORsp.setTokenType(Constants.OAUTH_TOKEN_TYPE_BEARER);
        jwtAORsp.setExpiresIn(BEARER_DURATION.getSeconds());

        JwtDO jwtDO = new JwtDO();
        jwtDO.setRefreshJwt(jwtAORsp.getRefreshToken());
        jwtDO.setIssued(iat);
        jwtDO.setExpires(expRefresh);
        jwtRepository.save(jwtDO);
        return jwtAORsp;
    }

    private String generateJwt(ZonedDateTime iat, ZonedDateTime exp) throws JOSEException {
        JWSObject jwsObject = new JWSObject(
                new JWSHeader
                        .Builder(JWSAlgorithm.ES256)
                        .type(JOSEObjectType.JWT)
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

    @Scheduled(fixedDelay = 1000*60*60*24) //24hrs
    private void prune(){
        List<JwtDO> expired = jwtRepository.findAllByExpiresBefore(ZonedDateTime.now(ZoneOffset.UTC));
        jwtRepository.deleteInBatch(expired);
    }
}
