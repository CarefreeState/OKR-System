package com.macaku.core.controller.inner;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.inner.pto.KeyResultDTO;
import com.macaku.core.service.inner.KeyResultService;
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
 * Date: 2024-01-21
 * Time: 2:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/keyresult")
@Api(tags = "关键结果")
public class KeyResultController {

    private final KeyResultService keyResultService;

    @PostMapping("/add")
    @ApiOperation("添加关键结果")
    public SystemJsonResponse addKeyResult(KeyResultDTO keyResultDTO) {
        // 校验
        keyResultDTO.validate();
        // 构造
        KeyResult keyResult = BeanUtil.copyProperties(keyResultDTO, KeyResult.class);
        // 添加
        keyResultService.addResultService(keyResult);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
