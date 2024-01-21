package com.macaku.core.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.domain.po.inner.dto.StatusFlagDTO;
import com.macaku.core.service.inner.StatusFlagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/add")
    @ApiOperation("增加一条状态指标")
    public SystemJsonResponse addStatusFlag(StatusFlagDTO statusFlagDTO) {
        // 检查
        statusFlagDTO.validate();
        StatusFlag statusFlag = BeanUtil.copyProperties(statusFlagDTO, StatusFlag.class);
        // 插入
        statusFlagService.addStatusFlag(statusFlag);
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }




}
