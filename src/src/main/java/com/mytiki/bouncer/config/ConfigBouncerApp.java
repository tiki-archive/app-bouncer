/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import({
        ConfigProperties.class,
        ConfigFeatures.class
})
public class ConfigBouncerApp {

    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
