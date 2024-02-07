package com.macaku.center.controller.core.quadrant;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.quadrant.OkrInitQuadrantDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @Value("${limit.time.second}")
    private Integer secondQuadrantCycle;

    private final SecondQuadrantService secondQuadrantService;

    private final OkrServiceSelector okrServiceSelector;

    @PostMapping("/init")
    @ApiOperation("初始化第二象限")
    public SystemJsonResponse initSecondQuadrant(HttpServletRequest request,
                                                 @RequestBody OkrInitQuadrantDTO okrInitQuadrantDTO) {
        // 校验
        okrInitQuadrantDTO.validate();
        // 初始化
        InitQuadrantDTO initQuadrantDTO = okrInitQuadrantDTO.getInitQuadrantDTO();
        initQuadrantDTO.validate();
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        // 判断周期长度合理性
        if(secondQuadrantCycle.compareTo(quadrantCycle) > 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.SECOND_CYCLE_TOO_SHORT);
        }
        User user = UserRecordUtil.getUserRecord(request);
        Long quadrantId = initQuadrantDTO.getId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long coreId = secondQuadrantService.getSecondQuadrantCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            secondQuadrantService.initSecondQuadrant(initQuadrantDTO);
            log.info("第二象限初始化成功：{}", initQuadrantDTO);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
