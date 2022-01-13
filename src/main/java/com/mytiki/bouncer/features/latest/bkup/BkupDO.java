/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;


import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bkup")
public class BkupDO implements Serializable {

    private Long id;
    private String proof;
    private byte[] ciphertext;
    private ZonedDateTime created;
    private ZonedDateTime lastAccessed;
    private int accessCount;
    private String lockCode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "proof_hashed")
    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    @Column(name = "ciphertext")
    public byte[] getCiphertext() {
        return ciphertext;
    }

    public void setCiphertext(byte[] ciphertext) {
        this.ciphertext = ciphertext;
    }

    @Column(name = "created_utc")
    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    @Column(name = "last_accessed_utc")
    public ZonedDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(ZonedDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    @Column(name = "access_count")
    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    @Column(name = "lock_code")
    public String getLockCode() {
        return lockCode;
    }

    public void setLockCode(String lockCode) {
        this.lockCode = lockCode;
    }
}
