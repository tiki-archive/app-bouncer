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
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.invoke.MethodHandles;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final OtpRepository otpRepository;
    private final SendgridHelper sendgridHelper;
    private final OtpTemplate otpTemplate;

    private static final Long EXPIRY_DURATION_MINUTES = 60L;
    private static final String KEY_OTP = "otp";
    private static final String KEY_SALT = "salt";
    private static final String FAILED_AUTHENTICATE_MSG = "Failed to redeem One-time Password (OTP)";
    private static final String FAILED_ISSUE_MSG = "Failed to issue a secure One-time Password (OTP)";
    private static final String FAILED_EMAIL_MSG = "Failed to send email";
    private static final String INVALID_OTP_MSG = "Invalid One-time Password (OTP).";
    private static final String EXPIRED_OTP_MSG = "One-time Password (OTP) expired. Re-issue and try again.";

    public OtpService(
            OtpRepository otpRepository,
            SendgridHelper sendgridHelper,
            OtpTemplate otpTemplate
    ) {
        this.otpRepository = otpRepository;
        this.sendgridHelper = sendgridHelper;
        this.otpTemplate = otpTemplate;
    }

    public OtpAORsp issue(OtpAOIssueEmail otpAOIssueEmail){
        Map<String, String> newOtpMap = generateNewOtp();
        String path = "https://mytiki.com/app/bouncer?otp=" + newOtpMap.get(KEY_OTP);

        HashMap<String, String> templateDataMap = new HashMap<>(1);
        templateDataMap.put("dynamic-link",
                "https://mytiki.app/?link=" +  URLEncoder.encode(path, StandardCharsets.UTF_8) +
                        "&apn=com.mytiki.app" +
                        //"&isi=1560250866" + disabled while using TF with ifl
                        "&ibi=com.mytiki.app" +
                        "&ifl=" + URLEncoder.encode("https://testflight.apple.com/join/pUcjaGK8",StandardCharsets.UTF_8) +
                        "&afl=" + URLEncoder.encode("https://play.google.com/store/apps/details?id=com.mytiki.app",StandardCharsets.UTF_8) +
                        "&ofl=" + URLEncoder.encode("https://mytiki.com/app",StandardCharsets.UTF_8));

        boolean emailSuccess = sendgridHelper.send(
                otpAOIssueEmail.getEmail(),
                otpTemplate.renderEmailSubject(null),
                otpTemplate.renderEmailBodyHtml(templateDataMap),
                otpTemplate.renderEmailBodyText(templateDataMap));

        if(emailSuccess) return issue(newOtpMap.get(KEY_OTP), newOtpMap.get(KEY_SALT));
        else throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, FAILED_EMAIL_MSG);
    }

    public void redeem(String otp, String salt){
        String hashedOtp;
        try{
            hashedOtp = sha512(otp, salt);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to execute SHA-512", e);
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, FAILED_AUTHENTICATE_MSG);
        }

        Optional<OtpDO> otpDO = otpRepository.findByOtpHashed(hashedOtp);
        if(otpDO.isEmpty())
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, INVALID_OTP_MSG);

        otpRepository.delete(otpDO.get());

        if(ZonedDateTime.now(ZoneOffset.UTC).isAfter(otpDO.get().getExpires()))
            throw ApiExceptionFactory.exception(HttpStatus.UNAUTHORIZED, EXPIRED_OTP_MSG);
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
            throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, FAILED_ISSUE_MSG);
        }

        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        response.put(KEY_OTP, encoder.encodeToString(otpByteArray));
        response.put(KEY_SALT, encoder.encodeToString(saltByteArray));
        return response;
    }

    private OtpAORsp issue(String otp, String salt){
        OtpDO otpDO = new OtpDO();

        try {
            otpDO.setOtpHashed(sha512(otp, salt));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Unable to execute SHA-512", e);
            throw ApiExceptionFactory.exception(HttpStatus.UNPROCESSABLE_ENTITY, FAILED_ISSUE_MSG);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        otpDO.setIssued(now);
        otpDO.setExpires(now.plusMinutes(EXPIRY_DURATION_MINUTES));
        OtpDO savedDO = otpRepository.save(otpDO);

        OtpAORsp otpAORsp = new OtpAORsp();
        otpAORsp.setSalt(salt);
        otpAORsp.setExpires(savedDO.getExpires());
        otpAORsp.setIssued(savedDO.getIssued());
        return otpAORsp;
    }

    private String sha512(String raw, String salt) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(salt.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
        return new String(Hex.encodeHex(bytes));
    }

    @Scheduled(fixedDelay = 1000*60*60*24) //24hrs
    private void prune(){
        List<OtpDO> expired = otpRepository.findAllByExpiresBefore(ZonedDateTime.now(ZoneOffset.UTC));
        otpRepository.deleteInBatch(expired);
    }
}
