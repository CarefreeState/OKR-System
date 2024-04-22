package com.macaku.core.handler.ext;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.handler.DeadlineEventHandler;
import com.macaku.core.util.QuadrantDeadlineUtil;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:50
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdQuadrantDeadlineEventHandler extends DeadlineEventHandler {

    private final ThirdQuadrantService thirdQuadrantService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        ThirdQuadrantEvent thirdQuadrantEvent = deadlineEvent.getThirdQuadrantEvent();
        Long id = thirdQuadrantEvent.getCoreId();
        Long thirdQuadrantId = thirdQuadrantEvent.getId();
        Date thirdQuadrantDeadline = thirdQuadrantEvent.getDeadline();
        Integer thirdQuadrantCycle = thirdQuadrantEvent.getCycle();
        log.info("处理事件：内核 ID {}，第三象限 ID {}，第三象限截止时间 {}，第三象限周期 {}",
                id, thirdQuadrantId, thirdQuadrantDeadline, thirdQuadrantCycle);
        // 4. 是否设置了第三象限截止时间和周期
        if(Objects.nonNull(thirdQuadrantDeadline) && Objects.nonNull(thirdQuadrantCycle)) {
            // 4.1 获取一个正确的截止点
            long deadTimestamp = thirdQuadrantDeadline.getTime();
            long nextDeadTimestamp = deadTimestamp;
            final long cycle = TimeUnit.SECONDS.toMillis(thirdQuadrantCycle);
            while(nextDeadTimestamp <= nowTimestamp) {
                nextDeadTimestamp += cycle;
            }
            Date nextDeadline = new Date(nextDeadTimestamp);
            // 4.2 更新截止时间
            if(nextDeadTimestamp != deadTimestamp) {
                ThirdQuadrant updateQuadrant = new ThirdQuadrant();
                updateQuadrant.setId(thirdQuadrantId);
                updateQuadrant.setDeadline(nextDeadline);
                thirdQuadrantService.lambdaUpdate()
                        .eq(ThirdQuadrant::getId, thirdQuadrantId)
                        .update(updateQuadrant);
            }
            // 4.3 发起定时任务
            thirdQuadrantEvent.setDeadline(nextDeadline);
            QuadrantDeadlineUtil.scheduledUpdateThirdQuadrant(thirdQuadrantEvent);
        }
        super.doNextHandler(deadlineEvent, nowTimestamp);//执行下一个责任处理器
    }
}
