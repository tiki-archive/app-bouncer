/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.jwt;

import com.mytiki.bouncer.features.latest.otp.OtpService;
import com.mytiki.bouncer.utilities.Constants;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@EnableJpaRepositories(JwtConfig.PACKAGE_PATH)
@EntityScan(JwtConfig.PACKAGE_PATH)
public class JwtConfig {

    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".jwt";

    @Bean
    public JwtController jwtController(@Autowired JwtService jwtService){
        return new JwtController(jwtService);
    }

    @Bean
    public JwtService jwtService(
            @Value("${com.mytiki.bouncer.jwt.private_key}") String jwtPrivateKey,
            @Autowired JwtRepository jwtRepository,
            @Autowired OtpService otpService
    ) throws JOSEException, InvalidKeySpecException, NoSuchAlgorithmException {
        return new JwtService(jwtPrivateKey, jwtRepository, otpService);
    }
}
