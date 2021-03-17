/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.config;

import org.springframework.beans.factory.annotation.Value;

public class ConfigProperties {

    @Value("${spring.profiles.active:}")
    private String springProfilesActive;

    @Value("${com.mytiki.bouncer.sendgrid.apikey}")
    private String sendgridApiKey;

    @Value("${com.mytiki.bouncer.jwt.key.path}")
    private String jwtKeyPath;

    @Value("${com.mytiki.bouncer.jwt.key.id}")
    private String jwtKeyId;

    @Value("${com.mytiki.bouncer.firebase.key.path}")
    private String firebaseKeyPath;

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

    public String getJwtKeyPath() {
        return jwtKeyPath;
    }

    public void setJwtKeyPath(String jwtKeyPath) {
        this.jwtKeyPath = jwtKeyPath;
    }

    public String getJwtKeyId() {
        return jwtKeyId;
    }

    public void setJwtKeyId(String jwtKeyId) {
        this.jwtKeyId = jwtKeyId;
    }

    public String getFirebaseKeyPath() {
        return firebaseKeyPath;
    }

    public void setFirebaseKeyPath(String firebaseKeyPath) {
        this.firebaseKeyPath = firebaseKeyPath;
    }
}
