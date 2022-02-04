/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.scode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScodeRepository extends JpaRepository<ScodeDO, Long> {
    List<ScodeDO> findByAddress(String address);
    Optional<ScodeDO> findByShortCode(String shortCode);
}
