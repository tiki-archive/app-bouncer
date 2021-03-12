/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.OneTimeToken;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping(value = ApiConstants.API_LATEST_ROUTE + "one-time-token")
public class OneTimeTokenController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final OneTimeTokenService tokenService;

    public OneTimeTokenController(OneTimeTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ApiReplyAO<OneTimeTokenAORsp> post(@RequestBody OneTimeTokenAOEmail body){
        logger.trace("POST " + ApiConstants.API_LATEST_ROUTE + "one-time-token");
        tokenService.issue(body);
        return ApiReplyAOFactory.ok();
    }
}
