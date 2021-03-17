/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.jwt;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = JwtController.PATH_CONTROLLER)
public class JwtController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "jwt";
    public static final String PATH_REFRESH = "/refresh";
    public static final String PATH_OTP = "/otp";

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_REFRESH)
    public ApiReplyAO<JwtAO> postRefresh(@RequestBody JwtAORefresh body){
        return ApiReplyAOFactory.ok(jwtService.refreshGrant(body));
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_OTP)
    public ApiReplyAO<JwtAO> postRefresh(@RequestBody JwtAOOtp body){
        return ApiReplyAOFactory.ok(jwtService.otpGrant(body));
    }
}
