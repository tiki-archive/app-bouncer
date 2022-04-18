/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SyncRepository extends JpaRepository<SyncDO, Long> {
    Optional<SyncDO> findByAddress(String address);
}
