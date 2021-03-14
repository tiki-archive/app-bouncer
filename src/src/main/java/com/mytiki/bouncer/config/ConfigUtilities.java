/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import com.mytiki.bouncer.utilities.SendgridHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

public class ConfigUtilities {

    @Bean
    public SendgridHelper sendgridHelper(@Autowired ConfigProperties properties){
        return new SendgridHelper(properties.getSendgridApiKey());
    }
}
