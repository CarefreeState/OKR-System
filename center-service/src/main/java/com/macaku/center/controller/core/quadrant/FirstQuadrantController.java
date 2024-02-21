package com.macaku.center.controller.core.quadrant;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.quadrant.OkrFirstQuadrantDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.dto.FirstQuadrantDTO;
import com.macaku.core.service.quadrant.FirstQuadrantService;
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

import javax.servlet.http.HttpServletRequest;

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

    private final OkrServiceSelector okrServiceSelector;

    @PostMapping("/init")
    @ApiOperation("初始化第一项象限")
    public SystemJsonResponse initFirstQuadrant(HttpServletRequest request,
                                                @RequestBody OkrFirstQuadrantDTO okrFirstQuadrantDTO) {
        // 校验
        okrFirstQuadrantDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        FirstQuadrantDTO firstQuadrantDTO = okrFirstQuadrantDTO.getFirstQuadrantDTO();
        firstQuadrantDTO.validate();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrFirstQuadrantDTO.getScene());
        FirstQuadrant firstQuadrant = BeanUtil.copyProperties(firstQuadrantDTO, FirstQuadrant.class);
        Long firstQuadrantId = firstQuadrant.getId();
        // 检测身份
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            firstQuadrantService.initFirstQuadrant(firstQuadrant);
            log.info("第一象限初始化成功：{}", firstQuadrantDTO);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
