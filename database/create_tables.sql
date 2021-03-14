/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- ONE-TIME PASSWORD
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS otp(
    id BIGSERIAL NOT NULL,
    hashed_otp TEXT NOT NULL UNIQUE,
    issued_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);
