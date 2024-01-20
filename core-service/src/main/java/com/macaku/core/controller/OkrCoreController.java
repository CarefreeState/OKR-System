package com.macaku.core.controller;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.service.OkrCoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-19
 * Time: 23:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/core")
@Api(tags = "OKR 内核")
public class OkrCoreController {


    private final OkrCoreService okrCoreService;

    @GetMapping("/exception")
    @ApiOperation("test")
    public SystemJsonResponse test() {
        okrCoreService.test();
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/create")
    @ApiOperation("创建一个core")
    public SystemJsonResponse createOkr() {
        Long coreID = okrCoreService.createOkrCore()
                .orElseThrow(() -> new GlobalServiceException("创建core失败"));
        return SystemJsonResponse.SYSTEM_SUCCESS(coreID);
    }

}
