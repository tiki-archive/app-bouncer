/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OtpAOAuthenticate {

    private String otp;
    private String salt;

    @JsonCreator
    public OtpAOAuthenticate(
            @JsonProperty(value = "otp", required = true) String otp,
            @JsonProperty(value = "salt", required = true) String salt
    ) {
        this.otp = otp;
        this.salt = salt;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
