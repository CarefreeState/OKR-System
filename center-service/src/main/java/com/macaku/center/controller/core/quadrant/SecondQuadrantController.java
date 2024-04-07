package com.macaku.center.controller.core.quadrant;

import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.quadrant.OkrInitQuadrantDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.medal.domain.entry.StayTrueBeginning;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.medal.service.UserMedalService;
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

import java.util.Objects;

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

    @Value("${medal.stay-true-beginning.id}")
    private Long medalId;

    private final SecondQuadrantService secondQuadrantService;

    private final OkrServiceSelector okrServiceSelector;

    private final UserMedalService userMedalService;

    private final MedalHandlerChain medalHandlerChain;

    @PostMapping("/init")
    @ApiOperation("初始化第二象限")
    public SystemJsonResponse initSecondQuadrant(@RequestBody OkrInitQuadrantDTO okrInitQuadrantDTO) {
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
        User user = UserRecordUtil.getUserRecord();
        Long quadrantId = initQuadrantDTO.getId();
        OkrOperateService okrOperateService = okrServiceSelector.select(okrInitQuadrantDTO.getScene());
        // 检测身份
        Long coreId = secondQuadrantService.getSecondQuadrantCoreId(quadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            secondQuadrantService.initSecondQuadrant(initQuadrantDTO);
            log.info("第二象限初始化成功：{}", initQuadrantDTO);
            // 启动一个异步线程
            IOThreadPool.submit(() -> {
                UserMedal dbUserMedal = userMedalService.getDbUserMedal(userId, medalId);
                if(Objects.isNull(dbUserMedal)) {
                    StayTrueBeginning stayTrueBeginning = StayTrueBeginning.builder().userId(userId).coreId(coreId).build();
                    medalHandlerChain.handle(stayTrueBeginning);
                }
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        // 成功
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }


}
