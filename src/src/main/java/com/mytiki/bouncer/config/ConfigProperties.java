/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import org.springframework.beans.factory.annotation.Value;

public class ConfigProperties {

    @Value("${spring.profiles.active:}")
    private String springProfilesActive;

    @Value("${com.mytiki.bouncer.sendgrid.apikey:}")
    private String sendgridApiKey;

    public String getSpringProfilesActive() {
        return springProfilesActive;
    }

    public void setSpringProfilesActive(String springProfilesActive) {
        this.springProfilesActive = springProfilesActive;
    }

    public String getSendgridApiKey() {
        return sendgridApiKey;
    }

    public void setSendgridApiKey(String sendgridApiKey) {
        this.sendgridApiKey = sendgridApiKey;
    }
}
