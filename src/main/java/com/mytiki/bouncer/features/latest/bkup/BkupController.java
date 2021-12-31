/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.bouncer.features.latest.bkup;

import com.mytiki.common.ApiConstants;
import com.mytiki.common.reply.ApiReplyAO;
import com.mytiki.common.reply.ApiReplyAOFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = BkupController.PATH_CONTROLLER)
public class BkupController {

    public static final String PATH_CONTROLLER = ApiConstants.API_LATEST_ROUTE + "backup";
    public static final String PATH_UPSERT = "/upsert";
    public static final String PATH_FIND = "/find";

    private final BkupService bkupService;

    public BkupController(BkupService bkupService) {
        this.bkupService = bkupService;
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_UPSERT)
    public ApiReplyAO<?> upsert(@RequestBody BkupAOReqUpsert body){
        bkupService.upsert(body);
        return ApiReplyAOFactory.ok();
    }

    @RequestMapping(method = RequestMethod.POST, path = PATH_FIND)
    public ApiReplyAO<BkupAORspFind> find(@RequestBody BkupAOReqFind body){
        return ApiReplyAOFactory.ok(bkupService.find(body));
    }
}
