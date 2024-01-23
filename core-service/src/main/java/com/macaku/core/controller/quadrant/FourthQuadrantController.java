package com.macaku.core.controller.quadrant;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.service.quadrant.FourthQuadrantService;
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
 * Time: 13:22
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/fourthquadrant")
@Api(tags = "第四象限")
public class FourthQuadrantController {


    private final FourthQuadrantService fourthQuadrantService;

    @PostMapping("/search/{coreId}")
    @ApiOperation("查看第四象限")
    public SystemJsonResponse<FourthQuadrantVO> searchFourthQuadrant(@PathVariable("coreId") @NonNull @ApiParam("内核 ID") Long coreId) {
        FourthQuadrantVO fourthQuadrantVO = fourthQuadrantService.searchFourthQuadrant(coreId);
        return SystemJsonResponse.SYSTEM_SUCCESS(fourthQuadrantVO);
    }


}
