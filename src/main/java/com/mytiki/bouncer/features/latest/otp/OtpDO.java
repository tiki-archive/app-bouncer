/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "otp")
public class OtpDO implements Serializable {

    private Long id;
    private String otpHashed;
    private ZonedDateTime issued;
    private ZonedDateTime expires;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "otp_hashed")
    public String getOtpHashed() {
        return otpHashed;
    }

    public void setOtpHashed(String otpHashed) {
        this.otpHashed = otpHashed;
    }

    @Column(name = "issued_utc")
    public ZonedDateTime getIssued() {
        return issued;
    }

    public void setIssued(ZonedDateTime issued) {
        this.issued = issued;
    }

    @Column(name = "expires_utc")
    public ZonedDateTime getExpires() {
        return expires;
    }

    public void setExpires(ZonedDateTime expires) {
        this.expires = expires;
    }
}
