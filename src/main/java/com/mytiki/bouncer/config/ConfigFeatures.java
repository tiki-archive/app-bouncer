/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import com.mytiki.bouncer.features.latest.bkup.BkupConfig;
import com.mytiki.bouncer.features.latest.jwt.JwtConfig;
import com.mytiki.bouncer.features.latest.otp.OtpConfig;
import org.springframework.context.annotation.Import;

@Import({
        OtpConfig.class,
        JwtConfig.class,
        BkupConfig.class
})
public class ConfigFeatures {}
