package com.macaku.core.init.handler.ext;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.init.handler.EventHandler;
import com.macaku.core.init.util.QuadrantDeadlineUtil;
import com.macaku.core.service.quadrant.SecondQuadrantService;
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
public class SecondQuadrantEventHandler extends EventHandler {

    private final SecondQuadrantService secondQuadrantService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        Long id = deadlineEvent.getId();
        Long secondQuadrantId = deadlineEvent.getSecondQuadrantId();
        Date secondQuadrantDeadline = deadlineEvent.getSecondQuadrantDeadline();
        Integer secondQuadrantCycle = deadlineEvent.getSecondQuadrantCycle();
        log.warn("处理事件：内核 ID {}，第二象限 ID {}，第二象限截止时间 {}，第二象限周期 {}",
                id, secondQuadrantId, secondQuadrantDeadline, secondQuadrantCycle);
        // 3. 是否设置了第二象限截止时间和周期
        if(Objects.nonNull(secondQuadrantDeadline) && Objects.nonNull(secondQuadrantCycle)) {
            // 3.1 获取一个正确的截止点
            long deadTimestamp = secondQuadrantDeadline.getTime();
            long nextDeadTimestamp = deadTimestamp;
            final long cycle = TimeUnit.SECONDS.toMillis(secondQuadrantCycle);
            while(nextDeadTimestamp <= nowTimestamp) {
                nextDeadTimestamp += cycle;
            }
            Date nextDeadline = new Date(nextDeadTimestamp);
            // 3.2 更新截止时间
            if(nextDeadTimestamp != deadTimestamp) {
                SecondQuadrant updateQuadrant = new SecondQuadrant();
                updateQuadrant.setId(secondQuadrantId);
                updateQuadrant.setDeadline(nextDeadline);
                secondQuadrantService.lambdaUpdate()
                        .eq(SecondQuadrant::getId, secondQuadrantId)
                        .update(updateQuadrant);
            }
            // 3.3 发起定时任务
            QuadrantDeadlineUtil.scheduledUpdateSecondQuadrant(id, secondQuadrantId, nextDeadline,
                    secondQuadrantCycle);
        }
        super.doNextHandler(deadlineEvent, nowTimestamp);//执行下一个责任处理器
    }
}
