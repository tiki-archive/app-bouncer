/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.main;

import com.mytiki.bouncer.config.ConfigBouncerApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({
        ConfigBouncerApp.class
})
@SpringBootApplication
public class BouncerApp {

    public static void main(final String... args) {
        SpringApplication.run(BouncerApp.class, args);
    }
}