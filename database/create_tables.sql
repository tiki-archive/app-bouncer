/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

-- -----------------------------------------------------------------------
-- ONE TIME TOKEN
-- -----------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS one_time_token(
    id BIGSERIAL NOT NULL,
    link_token TEXT NOT NULL,
    device_token TEXT NOT NULL,
    issued_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_utc TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY(id)
);
