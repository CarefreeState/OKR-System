package com.macaku.core.controller.quadrant;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
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
 * Time: 13:20
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/thirdquadrant")
@Api(tags = "第三象限")
public class ThirdQuadrantController {


    private final ThirdQuadrantService thirdQuadrantService;

    @PostMapping("/search/{coreId}")
    @ApiOperation("查看第三象限")
    public SystemJsonResponse searchFirstQuadrant(@PathVariable("coreId") @NonNull @ApiParam("内核 ID") Long coreId) {
        ThirdQuadrantVO secondQuadrantVO = thirdQuadrantService.searchThirdQuadrant(coreId);
        return SystemJsonResponse.SYSTEM_SUCCESS(secondQuadrantVO);
    }



}
