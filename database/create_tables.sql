/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- ONE-TIME PASSWORD
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS otp(
    id BIGSERIAL NOT NULL,
    otp_hashed TEXT NOT NULL UNIQUE,
    issued_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);

-- -----------------------------------------------------------------------
-- JWT REFRESH
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jwt(
    id BIGSERIAL NOT NULL,
    refresh_jwt TEXT NOT NULL UNIQUE,
    issued_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);