package com.macaku.center.controller.core.quadrant;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.quadrant.OkrInitQuadrantDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.thread.local.ThreadLocalUtil;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private final OkrServiceSelector okrServiceSelector;

    private final OkrCoreService okrCoreService;

    @PostMapping("/init")
    @ApiOperation("初始化第三象限")
    public SystemJsonResponse initThirdQuadrant(@RequestBody OkrInitQuadrantDTO okrInitQuadrantDTO) {
        // 校验
        okrInitQuadrantDTO.validate();
        // 初始化
        InitQuadrantDTO initQuadrantDTO = okrInitQuadrantDTO.getInitQuadrantDTO();
        initQuadrantDTO.validate();
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        Long quadrantId = initQuadrantDTO.getId();
        Long coreId = thirdQuadrantService.getThirdQuadrantCoreId(quadrantId);
        User user = UserRecordUtil.getUserRecord();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            // 判断时长是否合理
            okrCoreService.checkThirdCycle(coreId, quadrantCycle);
            thirdQuadrantService.initThirdQuadrant(initQuadrantDTO);
            log.info("第三象限初始化成功：{}", initQuadrantDTO);
            ThreadLocalUtil.set(coreId.toString());
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
