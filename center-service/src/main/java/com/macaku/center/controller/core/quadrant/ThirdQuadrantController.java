package com.macaku.center.controller.core.quadrant;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrInitQuadrantDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    private final OkrServiceSelector okrServiceSelector;

    @PostMapping("/init")
    @ApiOperation("初始化第三象限")
    public SystemJsonResponse initThirdQuadrant(HttpServletRequest request,
                                                OkrInitQuadrantDTO okrInitQuadrantDTO) {
        // 校验
        okrInitQuadrantDTO.validate();
        // 初始化
        User user = UserRecordUtil.getUserRecord(request);
        InitQuadrantDTO initQuadrantDTO = okrInitQuadrantDTO.getInitQuadrantDTO();
        Long quadrantId = initQuadrantDTO.getId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long coreId = thirdQuadrantService.getThirdQuadrantCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            thirdQuadrantService.initThirdQuadrant(initQuadrantDTO);
            log.info("第三象限初始化成功：{}", initQuadrantDTO);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
