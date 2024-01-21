package com.macaku.core.controller;

import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/search/{id}")
    @ApiOperation("查看一个OKR内核")
    public SystemJsonResponse searchOkrCore(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id) {
        OkrCoreVO coreVO = okrCoreService.searchOkrCore(id)
                .orElseThrow(() -> new GlobalServiceException("查询失败"));
        return SystemJsonResponse.SYSTEM_SUCCESS(coreVO);
    }

}
