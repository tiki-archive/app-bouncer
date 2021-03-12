/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.OneTimeToken;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;

public class OneTimeTokenService {

    private final OneTimeTokenRepository tokenRepository;
    private static final Long EXPIRY_DURATION_MINUTES = 3L;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public OneTimeTokenService(OneTimeTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void issue(OneTimeTokenAOEmail tokenAO){
        OneTimeTokenDO tokenDO = new OneTimeTokenDO();
        byte[] tokenByteArray = new byte[32];

        try {
            SecureRandom.getInstanceStrong().nextBytes(tokenByteArray);
        }catch (NoSuchAlgorithmException e) {
            throw ApiExceptionFactory.exception(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Failed to generate a secure token");
        }

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        tokenDO.setToken(encoder.encodeToString(tokenByteArray));

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        tokenDO.setIssued(now);
        tokenDO.setExpires(now.plusMinutes(EXPIRY_DURATION_MINUTES));

        tokenRepository.save(tokenDO);
    }
}
