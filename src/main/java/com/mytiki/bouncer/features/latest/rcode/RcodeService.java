/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.rcode;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;

public class RcodeService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final RcodeRepository rcodeRepository;

    public RcodeService(RcodeRepository rcodeRepository) {
        this.rcodeRepository = rcodeRepository;
    }

    public void add(RcodeAOReq req) {
        RcodeDO rcodeDO = new RcodeDO();
        rcodeDO.setCode(req.getCode());
        rcodeDO.setReferred(encode(req.getEmail()));
        rcodeDO.setCreated(ZonedDateTime.now());
        try{
            rcodeRepository.save(rcodeDO);
        }catch (Exception exception){
            throw ApiExceptionFactory.exception(HttpStatus.BAD_REQUEST, "Failed save referral code");
        }
    }

    public RcodeAORsp count(String code){
        List<RcodeDO> codes = rcodeRepository.findByCode(code);
        RcodeAORsp rsp = new RcodeAORsp();
        rsp.setCount(codes.size());
        return rsp;
    }

    private byte[] encode(String email){
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA3-512");
            return digest.digest(email.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw ApiExceptionFactory.exception(HttpStatus.EXPECTATION_FAILED, "Failed to hash email");
        }
    }
}
