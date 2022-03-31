/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.sync;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = SyncController.PATH_CONTROLLER)
public class SyncController {
    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "sync-chain";

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/policy")
    public ApiReplyAO<SyncAOPolicyRsp> policy(@RequestBody SyncAOPolicyReq body){
        return ApiReplyAOFactory.ok(syncService.policy(body));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/register")
    public ApiReplyAO<?> register(){
        return ApiReplyAOFactory.ok();
    }
}
