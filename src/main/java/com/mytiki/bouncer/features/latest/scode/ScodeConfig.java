/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.scode;

import com.mytiki.bouncer.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(ScodeConfig.PACKAGE_PATH)
@EntityScan(ScodeConfig.PACKAGE_PATH)
public class ScodeConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".scode";

    @Bean
    public ScodeService scodeService(@Autowired ScodeRepository scodeRepository){
        return new ScodeService(scodeRepository);
    }

    @Bean
    public ScodeController scodeController(@Autowired ScodeService scodeService){
        return new ScodeController(scodeService);
    }
}
