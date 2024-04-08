package com.macaku.center.controller.core.inner;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.inner.OkrStatusFlagDTO;
import com.macaku.center.domain.dto.unify.inner.OkrStatusFlagRemoveDTO;
import com.macaku.center.domain.dto.unify.inner.OkrStatusFlagUpdateDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.domain.po.inner.dto.StatusFlagDTO;
import com.macaku.core.domain.po.inner.dto.StatusFlagUpdateDTO;
import com.macaku.core.service.inner.StatusFlagService;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.medal.domain.config.StatusFlagConfig;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 2:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/flag")
@Api(tags = "状态指标")
public class StatusFlagController {

    private final StatusFlagService statusFlagService;

    private final OkrServiceSelector okrServiceSelector;

    private final FourthQuadrantService fourthQuadrantService;

    private final StatusFlagConfig statusFlagConfig;

    @PostMapping("/add")
    @ApiOperation("增加一条状态指标")
    public SystemJsonResponse addStatusFlag(@RequestBody OkrStatusFlagDTO okrStatusFlagDTO) {
        // 检查
        okrStatusFlagDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        StatusFlagDTO statusFlagDTO = okrStatusFlagDTO.getStatusFlagDTO();
        statusFlagDTO.validate();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrStatusFlagDTO.getScene());
        StatusFlag statusFlag = BeanUtil.copyProperties(statusFlagDTO, StatusFlag.class);
        // 检测身份
        Long fourthQuadrantId = statusFlagDTO.getFourthQuadrantId();
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(fourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            // 插入
            id = statusFlagService.addStatusFlag(statusFlag);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/remove")
    @ApiOperation("删除一条指标")
    public SystemJsonResponse remove(@RequestBody OkrStatusFlagRemoveDTO okrStatusFlagRemoveDTO) {
        okrStatusFlagRemoveDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        Long statusFlagId = okrStatusFlagRemoveDTO.getId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrStatusFlagRemoveDTO.getScene());
        // 检测身份
        Long fourthQuadrantId = statusFlagService.getFlagFourthQuadrantId(statusFlagId);
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(fourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            statusFlagService.removeStatusFlag(statusFlagId);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/update")
    @ApiOperation("更新一条指标")
    public SystemJsonResponse update(@RequestBody OkrStatusFlagUpdateDTO okrStatusFlagUpdateDTO) {
        // 检查
        okrStatusFlagUpdateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        StatusFlagUpdateDTO statusFlagUpdateDTO = okrStatusFlagUpdateDTO.getStatusFlagUpdateDTO();
        statusFlagUpdateDTO.validate();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrStatusFlagUpdateDTO.getScene());
        StatusFlag statusFlag = BeanUtil.copyProperties(statusFlagUpdateDTO, StatusFlag.class);
        Long statusFlagId = statusFlagUpdateDTO.getId();
        // 检测身份
        Long flagFourthQuadrantId = statusFlagService.getFlagFourthQuadrantId(statusFlagId);
        Long coreId = fourthQuadrantService.getFourthQuadrantCoreId(flagFourthQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            statusFlagService.updateStatusFlag(statusFlag);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/check")
    @ApiOperation("检查当前用户的状态指标")
    public SystemJsonResponse<Boolean> updateKeyResult() {
        // 校验
        Long userId = UserRecordUtil.getUserRecord().getId();
        double average = statusFlagConfig.calculateStatusFlag(userId);
        boolean isTouch = statusFlagConfig.isTouch(average);
        log.info("检查用户 {} 状态指标 {}", userId, isTouch);
        return SystemJsonResponse.SYSTEM_SUCCESS(isTouch);
    }

}
