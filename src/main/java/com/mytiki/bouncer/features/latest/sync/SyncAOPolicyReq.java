/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SyncAOPolicyReq {
    private String address;
    private String stringToSign;
    private String signature;

    @JsonCreator
    public SyncAOPolicyReq(
            @JsonProperty(required = true) String address,
            @JsonProperty(required = true) String stringToSign,
            @JsonProperty(required = true) String signature) {
        this.address = address;
        this.stringToSign = stringToSign;
        this.signature = signature;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStringToSign() {
        return stringToSign;
    }

    public void setStringToSign(String stringToSign) {
        this.stringToSign = stringToSign;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
