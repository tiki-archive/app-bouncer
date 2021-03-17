/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import java.time.ZonedDateTime;

public class OtpAOIssueRsp {

    private String salt;
    private ZonedDateTime issued;
    private ZonedDateTime expires;

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public ZonedDateTime getIssued() {
        return issued;
    }

    public void setIssued(ZonedDateTime issued) {
        this.issued = issued;
    }

    public ZonedDateTime getExpires() {
        return expires;
    }

    public void setExpires(ZonedDateTime expires) {
        this.expires = expires;
    }
}
