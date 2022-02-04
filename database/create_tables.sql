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

-- -----------------------------------------------------------------------
-- ANONYMOUS KEY BACKUP
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bkup(
    id BIGSERIAL NOT NULL,
    proof_hashed TEXT NOT NULL UNIQUE,
    ciphertext BYTEA NOT NULL,
    created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    last_accessed_utc TIMESTAMP WITH TIME ZONE,
    access_count INTEGER DEFAULT 0,
    lock_code TEXT,
    PRIMARY KEY(id)
);

-- -----------------------------------------------------------------------
-- SHORT CODE
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS short_code(
       id BIGSERIAL NOT NULL,
       short_code TEXT,
       address TEXT,
       created_utc TIMESTAMP WITH TIME ZONE NOT NULL,
       PRIMARY KEY(id)
);