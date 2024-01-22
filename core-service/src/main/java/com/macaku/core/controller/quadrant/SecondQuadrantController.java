package com.macaku.core.controller.quadrant;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.service.quadrant.SecondQuadrantService;
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
 * Date: 2024-01-22
 * Time: 12:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/secondquadrant")
@Api(tags = "第二象限")
public class SecondQuadrantController {

    private final SecondQuadrantService secondQuadrantService;

    @PostMapping("/search/{coreId}")
    @ApiOperation("查看第二象限")
    public SystemJsonResponse searchFirstQuadrant(@PathVariable("coreId") @NonNull @ApiParam("内核 ID") Long coreId) {
        SecondQuadrantVO secondQuadrantVO = secondQuadrantService.searchSecondQuadrant(coreId);
        return SystemJsonResponse.SYSTEM_SUCCESS(secondQuadrantVO);
    }


}