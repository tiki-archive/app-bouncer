/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.mytiki.common.exception.ApiExceptionBuilder;
import com.mytiki.common.exception.ApiExceptionFactory;
import com.mytiki.common.reply.ApiReplyAOMessageBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class BkupService {
    private static final int ACCESS_LOCK_LIMIT = 5;
    private static final int PBKDF2_ITERATIONS = 200000;
    private static final int PBKDF2_WIDTH = 256;

    private final BkupRepository bkupRepository;

    public BkupService(BkupRepository bkupRepository) {
        this.bkupRepository = bkupRepository;
    }

    public void upsert(BkupAOReqUpsert req){
        String proof = generateProof(req.getEmail(), req.getPin());
        BkupDO save;
        Optional<BkupDO> optionalBkupDO = bkupRepository.findByProof(proof);
        if(optionalBkupDO.isPresent()){
            save = optionalBkupDO.get();
            save.setAccessCount(0);
            save.setCreated(ZonedDateTime.now());
            save.setLastAccessed(null);
            save.setLockCode(null);
            save.setCiphertext(req.getCiphertext().getBytes(StandardCharsets.UTF_8));
        }else{
            save = new BkupDO();
            save.setProof(proof);
            save.setCreated(ZonedDateTime.now());
            save.setCiphertext(req.getCiphertext().getBytes(StandardCharsets.UTF_8));
        }
        bkupRepository.save(save);
    }

    public BkupAORspFind find(BkupAOReqFind req){
        String proof = generateProof(req.getEmail(), req.getPin());
        Optional<BkupDO> optionalBkupDO = bkupRepository.findByProof(proof);
        BkupAORspFind find = new BkupAORspFind();
        optionalBkupDO.ifPresent((bkupDO) -> {
            if(bkupDO.getAccessCount() < ACCESS_LOCK_LIMIT)
                find.setCiphertext(
                        new String(bkupDO.getCiphertext(), StandardCharsets.UTF_8));
            else{
                if(bkupDO.getLockCode() == null)
                    bkupDO.setLockCode(UUID.randomUUID().toString());
                throw new ApiExceptionBuilder()
                        .httpStatus(HttpStatus.FORBIDDEN)
                        .messages(new ApiReplyAOMessageBuilder()
                                .message("Backup locked, too many attempts. Contact support")
                                .properties("LockCode", bkupDO.getLockCode())
                                .build())
                        .build();
            }
            bkupDO.setAccessCount(bkupDO.getAccessCount() + 1);
            bkupDO.setLastAccessed(ZonedDateTime.now());
            bkupRepository.save(bkupDO);
        });
        return find;
    }

    private String generateProof(String email, String pin){
        if(email == null || email.isEmpty() || pin == null || pin.isEmpty())
            throw ApiExceptionFactory.exception(
                    HttpStatus.BAD_REQUEST, "invalid email or pin");
        try {
            PBEKeySpec spec = new PBEKeySpec(
                    email.toCharArray(),
                    Utf8.encode(pin),
                    PBKDF2_ITERATIONS,
                    PBKDF2_WIDTH);

            SecretKeyFactory skf = SecretKeyFactory.getInstance(
                    Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1.name());

            return Base64.getEncoder().encodeToString(skf.generateSecret(spec).getEncoded());
        }catch(NoSuchAlgorithmException | InvalidKeySpecException exception){
            throw ApiExceptionFactory.exception(
                    HttpStatus.EXPECTATION_FAILED, "failed to generate proof");
        }
    }
}
