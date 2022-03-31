/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import com.mytiki.bouncer.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class SyncConfig {
    public static final String PACKAGE_PATH = Constants.PACKAGE_FEATURES_LATEST_DOT_PATH + ".sync";

    @Bean
    public SyncController syncController(@Autowired SyncService syncService){
        return new SyncController(syncService);
    }

    @Bean
    public SyncService syncService(
            @Value("${com.mytiki.bouncer.sync-chain.accountKey}") String accountKey,
            @Value("${com.mytiki.bouncer.sync-chain.secretKey}") String secretKey){
        return new SyncService(accountKey,secretKey);
    }
}
