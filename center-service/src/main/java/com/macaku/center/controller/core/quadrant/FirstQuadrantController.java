package com.macaku.center.controller.core.quadrant;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.dto.FirstQuadrantDTO;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 22:29
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/firstquadrant")
@Api(tags = "第一象限")
public class FirstQuadrantController {

    private final FirstQuadrantService firstQuadrantService;


    @PostMapping("/init")
    @ApiOperation("初始化第一项象限")
    public SystemJsonResponse initFirstQuadrant(FirstQuadrantDTO firstQuadrantDTO) {
        // 校验
        firstQuadrantDTO.validate();
        // 初始化
        FirstQuadrant firstQuadrant = BeanUtil.copyProperties(firstQuadrantDTO, FirstQuadrant.class);
        firstQuadrantService.initFirstQuadrant(firstQuadrant);
        // 成功
        log.info("第一象限初始化成功：{}", firstQuadrantDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/search/{coreId}")
    @ApiOperation("查看第一象限")
    public SystemJsonResponse<FirstQuadrantVO> searchFirstQuadrant(@PathVariable("coreId") @NonNull @ApiParam("内核 ID") Long coreId) {
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        return SystemJsonResponse.SYSTEM_SUCCESS(firstQuadrantVO);
    }





}
