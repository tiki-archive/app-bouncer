/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.Otp;

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
@RequestMapping(value = OneTimeTokenController.PATH_CONTROLLER)
public class OneTimeTokenController {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "otp";
    public static final String PATH_ISSUE = "/issue";
    public static final String PATH_VARIANT_EMAIL = "/email";
    public static final String PATH_VARIANT_PUSH = "/push";

    private final OneTimeTokenService tokenService;

    public OneTimeTokenController(OneTimeTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_CONTROLLER + PATH_VARIANT_EMAIL)
    public ApiReplyAO<OneTimeTokenAORsp> issueEmail(@RequestBody OtpAOIssueEmail body){
        logger.trace("POST " + OneTimeTokenController.PATH_CONTROLLER);
        return ApiReplyAOFactory.ok(tokenService.issue(body));
    }
}
