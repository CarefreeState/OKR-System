package com.macaku.core.controller;

import com.macaku.common.code.GlobalServiceStatusCode;
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
import org.springframework.util.StringUtils;
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
        OkrCoreVO coreVO = okrCoreService.searchOkrCore(id);
        return SystemJsonResponse.SYSTEM_SUCCESS(coreVO);
    }

    @PostMapping("/celebrate/{id}")
    @ApiOperation("确定庆祝日")
    public SystemJsonResponse confirmCelebrateDay(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id,
                                                  @RequestParam("celebrateDay") @NonNull @ApiParam("庆祝日（星期）") Integer celebrateDay) {
        if(celebrateDay.compareTo(1) < 0 || celebrateDay.compareTo(7) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.INVALID_CELEBRATE_DAY);
        }
        okrCoreService.confirmCelebrateDate(id, celebrateDay);
        log.info("成功为 OKR {} 确定庆祝日 星期{}", id, celebrateDay);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }
    @PostMapping("/summary/{id}")
    @ApiOperation("总结 OKR")
    public SystemJsonResponse summaryOKR(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id,
                                                  @RequestParam("summary") @NonNull @ApiParam("总结") String summary) {
        if(!StringUtils.hasText(summary)) {
            throw new GlobalServiceException("总结内容为空", GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
        okrCoreService.summaryOKR(id, summary);
        log.info("成功为 OKR {} 总结 {}", id, summary);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }
    @PostMapping("/complete/{id}")
    @ApiOperation("结束 OKR")
    public SystemJsonResponse complete(@PathVariable("id") @NonNull @ApiParam("OKR 内核 ID") Long id) {
        okrCoreService.complete(id);
        log.info("成功结束 OKR {}", id);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
