package com.macaku.core.init;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.init.util.QuadrantDeadlineUtil;
import com.macaku.core.mapper.OkrCoreMapper;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeadlineEventInitializer implements ApplicationListener<ApplicationStartedEvent> {

    public final static String QUADRANT_ID = "id";

    public final static String QUADRANT_DEADLINE = "deadline";

    private final OkrCoreMapper okrCoreMapper;

    private final OkrCoreService okrCoreService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;


    private void handleEvent(DeadlineEvent deadlineEvent) {
        Long id = deadlineEvent.getId();
        Date firstQuadrantDeadline = deadlineEvent.getFirstQuadrantDeadline();
        Date secondQuadrantDeadline = deadlineEvent.getSecondQuadrantDeadline();
        Date thirdQuadrantDeadline = deadlineEvent.getThirdQuadrantDeadline();
        Integer secondQuadrantCycle = deadlineEvent.getSecondQuadrantCycle();
        Integer thirdQuadrantCycle = deadlineEvent.getThirdQuadrantCycle();
        Long secondQuadrantId = deadlineEvent.getSecondQuadrantId();
        Long thirdQuadrantId = deadlineEvent.getThirdQuadrantId();
        final long nowTimestamp = System.currentTimeMillis();
        // 1. 判断是否截止
        if(Objects.nonNull(firstQuadrantDeadline) &&
                firstQuadrantDeadline.getTime() <= nowTimestamp) {
            okrCoreService.complete(id);
            return;
        }
        // 2. 是否设置了第一象限截止时间（这里一定代表未截止）
        if(Objects.nonNull(firstQuadrantDeadline)) {
            QuadrantDeadlineUtil.scheduledComplete(id, firstQuadrantDeadline);
        }
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
            QuadrantDeadlineUtil.scheduledUpdate(id, secondQuadrantId, nextDeadline,
                    secondQuadrantCycle, SecondQuadrant.class);
        }
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
            QuadrantDeadlineUtil.scheduledUpdate(id, thirdQuadrantId, nextDeadline,
                    thirdQuadrantCycle, ThirdQuadrant.class);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始恢复定时任务 --> --> -->");
        // 获取定时任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        // 处理定时任务
        deadlineEvents.stream()
                .parallel()
                .forEach(this::handleEvent);
        log.warn("<-- <-- <-- <-- <-- 定时任务恢复成功 <-- <-- <-- <-- <--");
    }
}
