/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.scode;

import com.mytiki.common.exception.ApiExceptionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.lang.Character.MAX_RADIX;

public class ScodeService {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ScodeRepository scodeRepository;

    public ScodeService(ScodeRepository scodeRepository) {
        this.scodeRepository = scodeRepository;
    }

    @Transactional
    public ScodeAORsp get(String address) {
        ScodeAORsp rsp = new ScodeAORsp();
        Optional<ScodeDO> scodeDOOptional = scodeRepository.findByAddress(address);
        scodeDOOptional.ifPresentOrElse(
                scodeDO -> rsp.setCode(scodeDO.getShortCode()),
                () -> {
                    ScodeDO scodeDO = new ScodeDO();
                    scodeDO.setAddress(address);
                    scodeDO.setCreated(ZonedDateTime.now());
                    ScodeDO saved = scodeRepository.save(scodeDO);
                    saved.setShortCode("$" + toAlphaNumeric(saved.getId()));
                    saved = scodeRepository.save(saved);
                    rsp.setCode(saved.getShortCode());
                });
        return rsp;
    }

    public ScodeAORsp claim(ScodeAOClaim aoClaim) {
        ScodeAORsp rsp = new ScodeAORsp();
        Optional<ScodeDO> scodeDOOptional = scodeRepository.findByShortCode(aoClaim.getCode());
        scodeDOOptional.ifPresentOrElse(scodeDO -> {
            if(scodeDO.getAddress() != null){
                throw ApiExceptionFactory.exception(HttpStatus.BAD_REQUEST, "code already claimed");
            }else{
                scodeDO.setAddress(aoClaim.getAddress());
                scodeRepository.save(scodeDO);
                rsp.setCode(scodeDO.getShortCode());
            }
        }, () -> {
            throw ApiExceptionFactory.exception(HttpStatus.BAD_REQUEST, "code doesn't exist");
        });
        return rsp;
    }

    private String toAlphaNumeric(Long id){
        String val = Long.toString(id, MAX_RADIX).toUpperCase();
        StringBuilder res = new StringBuilder();
        int numZeros = 5 - val.length();
        res.append("0".repeat(Math.max(0, numZeros)));
        res.append(val);
        return res.toString();
    }
}
