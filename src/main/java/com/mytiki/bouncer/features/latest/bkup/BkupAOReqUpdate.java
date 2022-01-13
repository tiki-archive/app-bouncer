/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BkupAOReqUpdate {

    private String email;
    private String oldPin;
    private String newPin;
    private String ciphertext;

    @JsonCreator
    public BkupAOReqUpdate(
            @JsonProperty(required = true) String email,
            @JsonProperty(required = true) String oldPin,
            @JsonProperty(required = true) String newPin,
            @JsonProperty(required = true) String ciphertext)
    {
        this.email = email;
        this.oldPin = oldPin;
        this.newPin = newPin;
        this.ciphertext = ciphertext;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOldPin() {
        return oldPin;
    }

    public void setOldPin(String oldPin) {
        this.oldPin = oldPin;
    }

    public String getNewPin() {
        return newPin;
    }

    public void setNewPin(String newPin) {
        this.newPin = newPin;
    }

    public String getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(String ciphertext) {
        this.ciphertext = ciphertext;
    }
}
