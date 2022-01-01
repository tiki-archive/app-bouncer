/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BkupAOReqAdd {

    private String email;
    private String pin;
    private String ciphertext;

    @JsonCreator
    public BkupAOReqAdd(
            @JsonProperty(required = true) String email,
            @JsonProperty(required = true) String pin,
            @JsonProperty(required = true) String ciphertext)
    {
        this.email = email;
        this.pin = pin;
        this.ciphertext = ciphertext;
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

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}
