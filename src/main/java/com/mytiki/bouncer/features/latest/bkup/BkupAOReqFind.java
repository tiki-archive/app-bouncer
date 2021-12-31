/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BkupAOReqFind {

    private String email;
    private String pin;

    @JsonCreator
    public BkupAOReqFind(
            @JsonProperty(required = true) String email,
            @JsonProperty(required = true) String pin)
    {
        this.email = email;
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
