/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.springframework.http.HttpStatus;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;

public class OtpService {

    private final OtpRepository otpRepository;
    private static final Long EXPIRY_DURATION_MINUTES = 3L;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public OtpAOIssueRsp issue(OtpAOIssueEmail otpAOIssueEmail){
        //TODO send email (Sendgrid)
        return issue();
    }

    public OtpAOIssueRsp issue(OtpAOIssuePush otpAOIssuePush){
        //TODO send push (APNS, Firebase)
        return issue();
    }

    public OtpAOAuthenticateRsp authenticate(OtpAOAuthenticate otpAOAuthenticate){
        String invalidMsg = "Invalid One-time Password (OTP).";
        String expiredMsg = "One-time Password (OTP) expired. Re-issue and try again.";

        //TODO hash tokens

        Optional<OtpDO> otpDO = otpRepository.findByDeviceToken(otpAOAuthenticate.getDeviceToken());
        if(otpDO.isEmpty() || !otpDO.get().getLinkToken().equals(otpAOAuthenticate.getLinkToken()))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, invalidMsg);

        otpRepository.delete(otpDO.get());

        if(ZonedDateTime.now(ZoneOffset.UTC).isAfter(otpDO.get().getExpires()))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, expiredMsg);

        OtpAOAuthenticateRsp rsp = new OtpAOAuthenticateRsp();
        rsp.setJwt("hello");
        return rsp;
    }

    private OtpAOIssueRsp issue(){
        OtpDO otpDO = new OtpDO();
        byte[] deviceTokenByteArray = new byte[32];
        byte[] linkTokenByteArray = new byte[32];

        try {
            SecureRandom.getInstanceStrong().nextBytes(deviceTokenByteArray);
            SecureRandom.getInstanceStrong().nextBytes(linkTokenByteArray);
        }catch (NoSuchAlgorithmException e) {
            throw ApiExceptionFactory.exception(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Failed to generate a secure token");
        }

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        otpDO.setDeviceToken(encoder.encodeToString(deviceTokenByteArray));
        otpDO.setLinkToken(encoder.encodeToString(linkTokenByteArray));

        //TODO hash tokens before sticking in DB.

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        otpDO.setIssued(now);
        otpDO.setExpires(now.plusMinutes(EXPIRY_DURATION_MINUTES));
        OtpDO savedDO = otpRepository.save(otpDO);

        OtpAOIssueRsp otpAOIssueRsp = new OtpAOIssueRsp();
        otpAOIssueRsp.setDeviceToken(savedDO.getDeviceToken());
        otpAOIssueRsp.setExpires(savedDO.getExpires());
        otpAOIssueRsp.setIssued(savedDO.getIssued());
        return otpAOIssueRsp;
    }
}
