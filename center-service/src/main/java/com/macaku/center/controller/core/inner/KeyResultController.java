package com.macaku.center.controller.core.inner;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.component.OkrOperateServiceFactory;
import com.macaku.center.domain.dto.unify.inner.OkrKeyResultDTO;
import com.macaku.center.domain.dto.unify.inner.OkrKeyResultUpdateDTO;
import com.macaku.center.service.OkrOperateService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.inner.dto.KeyResultDTO;
import com.macaku.core.domain.po.inner.dto.KeyResultUpdateDTO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.service.inner.KeyResultService;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import com.macaku.corerecord.domain.entry.KeyResultUpdate;
import com.macaku.corerecord.handler.chain.RecordEventHandlerChain;
import com.macaku.medal.domain.entry.VictoryWithinGrasp;
import com.macaku.medal.handler.chain.MedalHandlerChain;
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
 * Date: 2024-01-21
 * Time: 2:21
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/keyresult")
@Api(tags = "关键结果")
public class KeyResultController {

    private final OkrCoreService okrCoreService;

    private final KeyResultService keyResultService;

    private final OkrOperateServiceFactory okrOperateServiceFactory;

    private final FirstQuadrantService firstQuadrantService;

    private final MedalHandlerChain medalHandlerChain;

    private final RecordEventHandlerChain recordEventHandlerChain;

    @PostMapping("/add")
    @ApiOperation("添加关键结果")
    public SystemJsonResponse<Long> addKeyResult(@RequestBody OkrKeyResultDTO okrKeyResultDTO) {
        // 校验
        okrKeyResultDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        KeyResultDTO keyResultDTO = okrKeyResultDTO.getKeyResultDTO();
        keyResultDTO.validate();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultDTO.getScene());
        KeyResult keyResult = BeanUtil.copyProperties(keyResultDTO, KeyResult.class);
        // 检测身份
        Long firstQuadrantId = keyResultDTO.getFirstQuadrantId();
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        Long id = null;
        if(user.getId().equals(userId)) {
            // 添加
            id = keyResultService.addResultService(keyResult);
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        Integer probability = keyResult.getProbability();
        IOThreadPool.submit(() -> {
            okrCoreService.checkOverThrows(coreId);
            VictoryWithinGrasp victoryWithinGrasp = VictoryWithinGrasp.builder()
                    .userId(userId)
                    .probability(probability)
                    .oldProbability(0)
                    .build();
            medalHandlerChain.handle(victoryWithinGrasp);
            KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder().coreId(coreId).build();
            recordEventHandlerChain.handle(keyResultUpdate);
        });
        return SystemJsonResponse.SYSTEM_SUCCESS(id);
    }

    @PostMapping("/update")
    @ApiOperation("更新完成概率")
    public SystemJsonResponse updateKeyResult(@RequestBody OkrKeyResultUpdateDTO okrKeyResultUpdateDTO) {
        // 校验
        okrKeyResultUpdateDTO.validate();
        User user = UserRecordUtil.getUserRecord();
        KeyResultUpdateDTO keyResultUpdateDTO = okrKeyResultUpdateDTO.getKeyResultUpdateDTO();
        keyResultUpdateDTO.validate();
        OkrOperateService okrOperateService = okrOperateServiceFactory.getService(okrKeyResultUpdateDTO.getScene());
        KeyResult keyResult = BeanUtil.copyProperties(keyResultUpdateDTO, KeyResult.class);
        Long keyResultId = keyResult.getId();
        // 校验身份
        Long firstQuadrantId = keyResultService.getFirstQuadrantId(keyResultId);
        Long coreId = firstQuadrantService.getFirstQuadrantCoreId(firstQuadrantId);
        Long userId = okrOperateService.getCoreUser(coreId);
        if(user.getId().equals(userId)) {
            // 更新
            KeyResult oldKeyResult = keyResultService.updateProbability(keyResult);
            log.info("提交更新：{}", keyResultUpdateDTO);
            Integer probability = keyResult.getProbability();
            Integer oldProbability = oldKeyResult.getProbability();
            IOThreadPool.submit(() -> {
                okrCoreService.checkOverThrows(coreId);
                VictoryWithinGrasp victoryWithinGrasp = VictoryWithinGrasp.builder()
                        .userId(userId)
                        .probability(probability)
                        .oldProbability(oldProbability)
                        .build();
                medalHandlerChain.handle(victoryWithinGrasp);
                KeyResultUpdate keyResultUpdate = KeyResultUpdate.builder().coreId(coreId).build();
                recordEventHandlerChain.handle(keyResultUpdate);
            });
        }else {
            throw new GlobalServiceException(GlobalServiceStatusCode.USER_NOT_CORE_MANAGER);
        }
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
