/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.otp;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = OtpController.PATH_CONTROLLER)
public class OtpController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "otp";
    public static final String PATH_EMAIL = "/email";
    public static final String PATH_PUSH = "/push";

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_EMAIL)
    public ApiReplyAO<OtpAORsp> postIssueEmail(@RequestBody OtpAOIssueEmail body){
        return ApiReplyAOFactory.ok(otpService.issue(body));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_PUSH)
    public ApiReplyAO<OtpAORsp> postIssuePush(@RequestBody OtpAOIssuePush body){
        return ApiReplyAOFactory.ok(otpService.issue(body));
    }
}
