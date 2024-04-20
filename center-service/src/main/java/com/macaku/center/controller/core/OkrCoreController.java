package com.macaku.center.controller.core;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrCoreDTO;
import com.macaku.center.domain.dto.unify.OkrCoreSummaryDTO;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.corerecord.domain.po.DayRecord;
import com.macaku.corerecord.domain.vo.DayRecordVO;
import com.macaku.corerecord.service.DayRecordService;
import com.macaku.medal.domain.entry.OkrFinish;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    private final OkrServiceSelector okrServiceSelector;

    private final MedalHandlerChain medalHandlerChain;

    private final DayRecordService dayRecordService;

    @PostMapping("/create")
    @ApiOperation("创建一个 OKR")
    public SystemJsonResponse<Map<String, Object>> createOkr(@RequestBody OkrOperateDTO okrOperateDTO) {
        // 检测
        okrOperateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrOperateDTO.getScene());
        Map<String, Object> ret = okrOperateService.createOkrCore(user, okrOperateDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(ret);
    }

    @PostMapping("/search")
    @ApiOperation("查看一个 OKR")
    public SystemJsonResponse<OkrCoreVO> searchOkrCore(@RequestBody OkrCoreDTO okrCoreDTO) {
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreDTO.getScene());
        OkrCoreVO okrCoreVO = okrOperateService.selectAllOfCore(user, okrCoreDTO.getCoreId());
        return SystemJsonResponse.SYSTEM_SUCCESS(okrCoreVO);
    }

    @PostMapping("/search/dayrecord")
    @ApiOperation("查看一个 OKR 的日记录")
    public SystemJsonResponse<List<DayRecordVO>> searchOkrCoreDayRecord(@RequestBody OkrCoreDTO okrCoreDTO) {
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreDTO.getCoreId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreDTO.getScene());
        if(Boolean.TRUE.equals(okrOperateService.canVisit(user, coreId))) {
            List<DayRecord> dayRecords = dayRecordService.getDayRecords(coreId);
            return SystemJsonResponse.SYSTEM_SUCCESS(BeanUtil.copyToList(dayRecords, DayRecordVO.class));
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
    }

    @PostMapping("/celebrate/{day}")
    @ApiOperation("确定庆祝日")
    public SystemJsonResponse confirmCelebrateDay(@RequestBody OkrCoreDTO okrCoreDTO,
                                                  @PathVariable("day") @NonNull @ApiParam("庆祝日（星期）") Integer celebrateDay) {
        if(celebrateDay.compareTo(1) < 0 || celebrateDay.compareTo(7) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.INVALID_CELEBRATE_DAY);
        }
        okrCoreDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreDTO.getCoreId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)){
            okrCoreService.confirmCelebrateDate(coreId, celebrateDay);
            log.info("成功为 OKR {} 确定庆祝日 星期{}", coreId, celebrateDay);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/summary")
    @ApiOperation("总结 OKR")
    public SystemJsonResponse summaryOKR(@RequestBody OkrCoreSummaryDTO okrCoreSummaryDTO) {
        // 检测
        okrCoreSummaryDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long coreId = okrCoreSummaryDTO.getCoreId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreSummaryDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            String summary = okrCoreSummaryDTO.getSummary();
            Integer degree = okrCoreSummaryDTO.getDegree();
            Date endTime = okrCoreService.summaryOKR(coreId, summary, degree);
            log.info("成功为 OKR {} 总结 {} 完成度 {}%", coreId, summary, degree);
            // 开启一个异步线程
            IOThreadPool.submit(() -> {
                OkrFinish okrFinish = OkrFinish.builder()
                        .userId(userId)
                        .degree(degree)
                        .isAdvance(endTime.compareTo(new Date()) < 0)
                        .build();
                medalHandlerChain.handle(okrFinish);
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/complete")
    @ApiOperation("结束 OKR")
    public SystemJsonResponse complete(@RequestBody OkrCoreDTO okrCoreDTO) {
        // 检测
        okrCoreDTO.validate();
        Long coreId = okrCoreDTO.getCoreId();
        User user = UserRecordUtil.getUserRecord();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrCoreDTO.getScene());
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            okrCoreService.complete(coreId);
            log.info("成功结束 OKR {}", coreId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
