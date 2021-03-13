/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import com.mytiki.bouncer.features.latest.otp.OtpConfig;
import org.springframework.context.annotation.Import;

@Import({
        OtpConfig.class
})
public class ConfigFeatures {
}
