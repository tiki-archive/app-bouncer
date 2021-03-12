/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.OneTimeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(OneTimeTokenConfig.PACKAGE_PATH)
@EntityScan(OneTimeTokenConfig.PACKAGE_PATH)
public class OneTimeTokenConfig {

    public static final String PACKAGE_PATH = "com.mytiki.bouncer.features.latest.OneTimeToken";

    @Bean
    public OneTimeTokenService oneTimeTokenService(@Autowired OneTimeTokenRepository tokenRepository){
        return new OneTimeTokenService(tokenRepository);
    }

    @Bean
    public OneTimeTokenController oneTimeTokenController(@Autowired OneTimeTokenService tokenService){
        return new OneTimeTokenController(tokenService);
    }
}
