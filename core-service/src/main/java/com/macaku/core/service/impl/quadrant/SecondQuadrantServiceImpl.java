package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.TimerUtil;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.mapper.quadrant.SecondQuadrantMapper;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class SecondQuadrantServiceImpl extends ServiceImpl<SecondQuadrantMapper, SecondQuadrant>
    implements SecondQuadrantService{

    private final SecondQuadrantMapper secondQuadrantMapper;

    @Override
    public void initSecondQuadrant(InitQuadrantDTO initQuadrantDTO) {
        Long id = initQuadrantDTO.getId();
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        Date deadline = initQuadrantDTO.getDeadline();
        // 查询内核 ID
        Long coreId = this.lambdaQuery().eq(SecondQuadrant::getId, id)
                .select(SecondQuadrant::getCoreId)
                .one()
                .getCoreId();
        // 为 core 设置周期
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(coreId);
        updateOkrCore.setSecondQuadrantCycle(quadrantCycle);
        Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
        // 设置象限的截止时间
        SecondQuadrant updateQuadrant = new SecondQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(deadline);
        this.lambdaUpdate().eq(SecondQuadrant::getId, id).update(updateQuadrant);
        // 发起一个定时任务
        final long deadTimestamp = deadline.getTime();
        final long nextDeadTimestamp = deadTimestamp + quadrantCycle * 1000L;
        Date nextDeadline = new Date(nextDeadTimestamp);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = Db.lambdaQuery(OkrCore.class)
                        .eq(OkrCore::getId, coreId)
                        .one()
                        .getIsOver();
                if(isOver) {
                    log.info("OKR 已结束");
                }else {
                    // 设置新的截止时间
                    SecondQuadrant updateQuadrant = new SecondQuadrant();
                    updateQuadrant.setId(id);
                    updateQuadrant.setDeadline(nextDeadline);
                    Db.lambdaUpdate(SecondQuadrant.class).eq(SecondQuadrant::getId, id).update(updateQuadrant);
                    // 发起新的
                }
            }
        }, deadTimestamp, TimeUnit.MILLISECONDS);





    }

    @Override
    public SecondQuadrantVO searchSecondQuadrant(Long coreId) {
        return secondQuadrantMapper.searchSecondQuadrant(coreId).orElseThrow(() ->
            new GlobalServiceException(GlobalServiceStatusCode.SECOND_QUADRANT_NOT_EXISTS)
        );
    }
}




