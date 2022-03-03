/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.rcode;

import com.mytiki.bouncer.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(com.mytiki.bouncer.features.latest.rcode.RcodeConfig.PACKAGE_PATH)
@EntityScan(com.mytiki.bouncer.features.latest.rcode.RcodeConfig.PACKAGE_PATH)
public class RcodeConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".rcode";

    @Bean
    public RcodeService rcodeService(@Autowired RcodeRepository rcodeRepository){
        return new RcodeService(rcodeRepository);
    }

    @Bean
    public RcodeController rcodeController(@Autowired RcodeService rcodeService){
        return new RcodeController(rcodeService);
    }
}
