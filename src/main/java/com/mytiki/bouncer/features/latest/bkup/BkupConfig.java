/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.mytiki.bouncer.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(BkupConfig.PACKAGE_PATH)
@EntityScan(BkupConfig.PACKAGE_PATH)
public class BkupConfig {

    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".bkup";

    @Bean
    public BkupController bkupController(@Autowired BkupService bkupService){
        return new BkupController(bkupService);
    }

    @Bean
    public BkupService bkupService(@Autowired BkupRepository bkupRepository) {
        return new BkupService(bkupRepository);
    }
}
