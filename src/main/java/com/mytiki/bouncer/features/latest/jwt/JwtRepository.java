/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface JwtRepository extends JpaRepository<JwtDO, Long> {

    Optional<JwtDO> findByRefreshJwt(String refreshJwt);
    List<JwtDO> findAllByExpiresBefore(ZonedDateTime before);
}
