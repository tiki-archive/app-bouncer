/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import com.mytiki.common.exception.ApiExceptionHandlerDefault;
import com.mytiki.common.reply.ApiReplyHandlerDefault;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Import({
        ConfigProperties.class,
        ApiExceptionHandlerDefault.class,
        ApiReplyHandlerDefault.class,
        ConfigUtilities.class,
        ConfigFeatures.class
})
public class ConfigBouncerApp {

    @PostConstruct
    void starter(){
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
