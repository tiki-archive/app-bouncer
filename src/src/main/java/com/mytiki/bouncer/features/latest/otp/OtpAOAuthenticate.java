/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OtpAOAuthenticate {

    private String deviceToken;
    private String linkToken;

    @JsonCreator
    public OtpAOAuthenticate(
            @JsonProperty(value = "deviceToken", required = true) String deviceToken,
            @JsonProperty(value = "linkToken", required = true) String linkToken
    ) {
        this.deviceToken = deviceToken;
        this.linkToken = linkToken;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getLinkToken() {
        return linkToken;
    }

    public void setLinkToken(String linkToken) {
        this.linkToken = linkToken;
    }
}
