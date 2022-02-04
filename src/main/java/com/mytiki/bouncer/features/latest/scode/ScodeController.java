/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.scode;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ScodeController.PATH_CONTROLLER)
public class ScodeController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "short-code";

    private final ScodeService scodeService;

    public ScodeController(ScodeService scodeService) {
        this.scodeService = scodeService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{address}")
    public ApiReplyAO<ScodeAORsp> get(@PathVariable String address){
        return ApiReplyAOFactory.ok(scodeService.get(address));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ApiReplyAO<ScodeAORsp> postIssueEmail(@RequestBody ScodeAOClaim body){
        return ApiReplyAOFactory.ok(scodeService.claim(body));
    }
}
