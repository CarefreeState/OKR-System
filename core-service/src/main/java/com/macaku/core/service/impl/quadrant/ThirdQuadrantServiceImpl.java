package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.TimerUtil;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import com.macaku.core.init.util.QuadrantDeadlineUtil;
import com.macaku.core.mapper.quadrant.ThirdQuadrantMapper;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:20
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ThirdQuadrantServiceImpl extends ServiceImpl<ThirdQuadrantMapper, ThirdQuadrant>
    implements ThirdQuadrantService {

    private final ThirdQuadrantMapper thirdQuadrantMapper;

    private void scheduledUpdate(Long coreId, Long id, Date deadline, Integer quadrantCycle) {
        final long deadTimestamp = deadline.getTime();
        final long nextDeadTimestamp = deadTimestamp + TimeUnit.SECONDS.toMillis(quadrantCycle);
        final long delay = TimeUnit.MILLISECONDS.toSeconds(deadTimestamp - System.currentTimeMillis());
        Date nextDeadline = new Date(nextDeadTimestamp);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = Db.lambdaQuery(OkrCore.class)
                        .eq(OkrCore::getId, coreId)
                        .select(OkrCore::getIsOver)
                        .one()
                        .getIsOver();
                if(isOver) {
                    log.info("OKR 已结束");
                }else {
                    // 设置新的截止时间
                    ThirdQuadrant updateQuadrant = new ThirdQuadrant();
                    updateQuadrant.setId(id);
                    updateQuadrant.setDeadline(nextDeadline);
                    Db.updateById(updateQuadrant);
                    // 发起下一个事件
                    scheduledUpdate(coreId, id, nextDeadline, quadrantCycle);
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    @Override
    public void initThirdQuadrant(InitQuadrantDTO initQuadrantDTO) {
        Long id = initQuadrantDTO.getId();
        Date deadline = this.lambdaQuery()
                .eq(ThirdQuadrant::getId, id)
                .one()
                .getDeadline();
        if(Objects.nonNull(deadline)) {
            throw new GlobalServiceException("第三象限无法再次初始化！",
                    GlobalServiceStatusCode.THIRD_QUADRANT_UPDATE_ERROR);
        }
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        deadline = initQuadrantDTO.getDeadline();
        // 查询内核 ID
        Long coreId = this.lambdaQuery()
                .eq(ThirdQuadrant::getId, id)
                .select(ThirdQuadrant::getCoreId)
                .one()
                .getCoreId();
        Boolean isOver = Db.lambdaQuery(OkrCore.class)
                .eq(OkrCore::getId, coreId)
                .select(OkrCore::getIsOver)
                .one()
                .getIsOver();
        if(isOver) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        // 为 core 设置周期
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(coreId);
        updateOkrCore.setSecondQuadrantCycle(quadrantCycle);
        Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
        // 设置象限的截止时间
        ThirdQuadrant updateQuadrant = new ThirdQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(deadline);
        this.lambdaUpdate().eq(ThirdQuadrant::getId, id).update(updateQuadrant);
        // 发起一个定时任务
        QuadrantDeadlineUtil.scheduledUpdate(coreId, id, deadline, quadrantCycle, ThirdQuadrant.class);
    }

    @Override
    public ThirdQuadrantVO searchThirdQuadrant(Long coreId) {
        return thirdQuadrantMapper.searchThirdQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.THIRD_QUADRANT_NOT_EXISTS));
    }
}




