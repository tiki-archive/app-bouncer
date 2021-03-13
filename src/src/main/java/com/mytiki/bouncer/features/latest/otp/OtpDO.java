/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.Otp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "one_time_token")
public class OneTimeTokenDO implements Serializable {

    private Long id;
    private String deviceToken;
    private String linkToken;
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

    @Column(name = "device_token")
    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @Column(name = "link_token")
    public String getLinkToken() {
        return linkToken;
    }

    public void setLinkToken(String linkToken) {
        this.linkToken = linkToken;
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
