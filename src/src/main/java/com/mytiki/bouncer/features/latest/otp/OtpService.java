/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.mytiki.bouncer.utilities.SendgridHelper;
import com.mytiki.common.exception.ApiExceptionFactory;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OtpRepository otpRepository;
    private final SendgridHelper sendgridHelper;
    private final OtpTemplateEmail otpTemplateEmail;
    private static final Long EXPIRY_DURATION_MINUTES = 3L;
    private static final String KEY_OTP = "otp";
    private static final String KEY_SALT = "salt";

    String failedAuthenticateMsg = "Failed to authenticate";
    String failedIssueMsg = "Failed to issue a secure One-time Password (OTP)";
    String failedEmailMsg = "Failed to send email";
    String invalidOtpMsg = "Invalid One-time Password (OTP).";
    String expiredOtpMsg = "One-time Password (OTP) expired. Re-issue and try again.";

    public OtpService(
            OtpRepository otpRepository,
            SendgridHelper sendgridHelper,
            OtpTemplateEmail otpTemplateEmail
    ) {
        this.otpRepository = otpRepository;
        this.sendgridHelper = sendgridHelper;
        this.otpTemplateEmail = otpTemplateEmail;
    }

    public OtpAOIssueRsp issue(OtpAOIssueEmail otpAOIssueEmail){
        Map<String, String> newOtpMap = generateNewOtp();

        HashMap<String, String> templateDataMap = new HashMap<>(1);
        templateDataMap.put("link", "https://mytiki.com/" + newOtpMap.get(KEY_OTP));

        boolean emailSuccess = sendgridHelper.send(
                otpAOIssueEmail.getEmail(),
                otpTemplateEmail.renderSubject(null),
                otpTemplateEmail.renderBodyHtml(templateDataMap),
                otpTemplateEmail.renderBodyText(templateDataMap));

        if(emailSuccess) return issue(newOtpMap.get(KEY_OTP), newOtpMap.get(KEY_SALT));
        else throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, failedEmailMsg);
    }

    public OtpAOIssueRsp issue(OtpAOIssuePush otpAOIssuePush){
        Map<String, String> newOtpMap = generateNewOtp();
        //TODO send push (APNS, Firebase)
        return issue(newOtpMap.get(KEY_OTP), newOtpMap.get(KEY_SALT));
    }

    public OtpAOAuthenticateRsp authenticate(OtpAOAuthenticate otpAOAuthenticate){
        String hashedOtp;
        try{
            hashedOtp = sha512(otpAOAuthenticate.getOtp(), otpAOAuthenticate.getSalt());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to execute SHA-512", e);
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, failedAuthenticateMsg);
        }

        Optional<OtpDO> otpDO = otpRepository.findByOtpHashed(hashedOtp);
        if(otpDO.isEmpty())
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, invalidOtpMsg);

        otpRepository.delete(otpDO.get());

        if(ZonedDateTime.now(ZoneOffset.UTC).isAfter(otpDO.get().getExpires()))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, expiredOtpMsg);

        OtpAOAuthenticateRsp rsp = new OtpAOAuthenticateRsp();
        rsp.setJwt("hello");
        return rsp;
    }

    private Map<String, String> generateNewOtp(){
        byte[] otpByteArray = new byte[32];
        byte[] saltByteArray = new byte[32];
        HashMap<String, String> response = new HashMap<>(2);

        try {
            SecureRandom.getInstanceStrong().nextBytes(otpByteArray);
            SecureRandom.getInstanceStrong().nextBytes(saltByteArray);
        }catch (NoSuchAlgorithmException e) {
            logger.error("Unable to generate secure random", e);
            throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, failedIssueMsg);
        }

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        response.put(KEY_OTP, encoder.encodeToString(otpByteArray));
        response.put(KEY_SALT, encoder.encodeToString(saltByteArray));
        return response;
    }

    private OtpAOIssueRsp issue(String otp, String salt){
        OtpDO otpDO = new OtpDO();

        try {
            otpDO.setOtpHashed(sha512(otp, salt));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to execute SHA-512", e);
            throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, failedIssueMsg);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        otpDO.setIssued(now);
        otpDO.setExpires(now.plusMinutes(EXPIRY_DURATION_MINUTES));
        OtpDO savedDO = otpRepository.save(otpDO);

        OtpAOIssueRsp otpAOIssueRsp = new OtpAOIssueRsp();
        otpAOIssueRsp.setSalt(salt);
        otpAOIssueRsp.setExpires(savedDO.getExpires());
        otpAOIssueRsp.setIssued(savedDO.getIssued());
        return otpAOIssueRsp;
    }

    private String sha512(String raw, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encodeHex(bytes));
    }
}
