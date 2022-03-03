/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.rcode;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = RcodeController.PATH_CONTROLLER)
public class RcodeController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "refer";

    private final RcodeService rcodeService;

    public RcodeController(RcodeService rcodeService) {
        this.rcodeService = rcodeService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ApiReplyAO<RcodeAORsp> get(@RequestParam String code){
        return ApiReplyAOFactory.ok(rcodeService.count(code));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ApiReplyAO<?> post(@RequestBody RcodeAOReq body){
        rcodeService.add(body);
        return ApiReplyAOFactory.ok();
    }
}
