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

    @Value("${com.mytiki.bouncer.jwt.private_key}")
    private String jwtPrivateKey;

    @Value("${com.mytiki.bouncer.jwt.public_key}")
    private String jwtPublicKey;

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

    public String getJwtPrivateKey() {
        return jwtPrivateKey;
    }

    public void setJwtPrivateKey(String jwtPrivateKey) {
        this.jwtPrivateKey = jwtPrivateKey;
    }

    public String getJwtPublicKey() {
        return jwtPublicKey;
    }

    public void setJwtPublicKey(String jwtPublicKey) {
        this.jwtPublicKey = jwtPublicKey;
    }

    public String getFirebaseKeyPath() {
        return firebaseKeyPath;
    }

    public void setFirebaseKeyPath(String firebaseKeyPath) {
        this.firebaseKeyPath = firebaseKeyPath;
    }
}
