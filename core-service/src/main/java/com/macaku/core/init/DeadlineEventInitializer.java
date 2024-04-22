package com.macaku.core.init;

import com.macaku.common.util.thread.pool.CPUThreadPool;
import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.handler.chain.DeadlineEventHandlerChain;
import com.macaku.core.util.QuadrantDeadlineUtil;
import com.macaku.core.mapper.OkrCoreMapper;
import com.macaku.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeadlineEventInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 0;

    private final static String CRON = "59 23 23 ? * 1 *";

    private final OkrCoreMapper okrCoreMapper;

    private final DeadlineEventHandlerChain deadlineEventHandlerChain;

    private void handleEvent(DeadlineEvent deadlineEvent) {
        final long nowTimestamp = System.currentTimeMillis();// 当前时间
        deadlineEventHandlerChain.handle(deadlineEvent, nowTimestamp);
    }

    private void action() {
        QuadrantDeadlineUtil.clear();
        // 获取定时任务
        List<DeadlineEvent> deadlineEvents = okrCoreMapper.getDeadlineEvents();
        // 处理定时任务
        CPUThreadPool.operateBatch(deadlineEvents, this::handleEvent);
    }

    @XxlJob(value = "initDeadlineJob")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】刷新截止时间的任务")
    public void initDeadlineJob() {
        action();
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始恢复定时任务 --> --> -->");
        action();
        log.warn("<-- <-- <-- <-- <-- 定时任务恢复成功 <-- <-- <-- <-- <--");
    }
}
