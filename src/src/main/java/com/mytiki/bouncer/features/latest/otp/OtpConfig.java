/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.mytiki.bouncer.utilities.Constants;
import com.mytiki.bouncer.utilities.SendgridHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(OtpConfig.PACKAGE_PATH)
@EntityScan(OtpConfig.PACKAGE_PATH)
public class OtpConfig {

    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".otp";

    @Bean
    public OtpService otpService(
            @Autowired OtpRepository otpRepository,
            @Autowired SendgridHelper sendgridHelper,
            @Autowired OtpTemplateEmail otpTemplateEmail
    ){
        return new OtpService(otpRepository, sendgridHelper, otpTemplateEmail);
    }

    @Bean
    public OtpController otpController(@Autowired OtpService otpService){
        return new OtpController(otpService);
    }

    @Bean
    public OtpTemplateEmail otpTemplateEmail(){
        return new OtpTemplateEmail();
    }
}
