/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mytiki.bouncer.config.ConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public class UtilitiesConfig {

    @Bean
    public SendgridHelper sendgridHelper(@Autowired ConfigProperties properties){
        return new SendgridHelper(properties.getSendgridApiKey());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(@Autowired ObjectMapper objectMapper){
        return new AccessDeniedHandlerImpl(objectMapper);
    }

    @Bean(name = "authEntryPointImplException")
    public AuthenticationEntryPoint authEntryPointImplException(@Autowired ObjectMapper objectMapper){
        return new AuthEntryPointImplException(objectMapper);
    }
}
