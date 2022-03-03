/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.rcode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RcodeRepository extends JpaRepository<RcodeDO, Long> {
    Optional<RcodeDO> findByReferred(byte[] referred);
    List<RcodeDO> findByCode(String code);
}
