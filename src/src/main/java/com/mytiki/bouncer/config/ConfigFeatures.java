/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import com.mytiki.bouncer.features.latest.OneTimeToken.OneTimeTokenConfig;
import org.springframework.context.annotation.Import;

@Import({
        OneTimeTokenConfig.class
})
public class ConfigFeatures {
}
