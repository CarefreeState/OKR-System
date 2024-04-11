package com.macaku.medal.init;

import cn.hutool.core.date.DateUtil;
import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import com.macaku.common.util.thread.timer.TimerUtil;
import com.macaku.medal.domain.entry.GreatState;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.UserService;
import com.macaku.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 16:19
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MedalEventInitializer {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static String MEDAL_CHECK_CRON = "59 23 23 ? * 1 *";

    private final MedalHandlerChain medalHandlerChain;

    private final UserService userService;

    private final static Integer WEEK_MOMENT = 7; // 星期日的最后一刻结算

    public static long getNextWeekTimestamp() {
        int weekMoment = WEEK_MOMENT;
        weekMoment = weekMoment <= 0 || weekMoment > 7 ? 7 : weekMoment;
        Date today = new Date();
        int weekToday = DateUtil.dayOfWeek(today) - 1;
        weekToday = weekToday == 0 ? 7 : weekToday;
        int gapDays = weekMoment - weekToday;
        gapDays = gapDays < 0 ? gapDays + 7 : gapDays;
        Date endOfToday = DateUtil.endOfDay(today);
        return DateUtil.offsetDay(endOfToday, gapDays).getTime();
    }

    @XxlJob(value = "issueGreatStateMedal")
    @XxlRegister(cron = MEDAL_CHECK_CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR,  triggerStatus = 1, jobDesc = "每周一次的勋章检查")
    public void issueGreatStateMedal() {
        userService.lambdaQuery()
                .select(User::getId)
                .list()
                .stream()
                .parallel()
                .map(User::getId).forEach(userId -> {
            GreatState greatState = GreatState.builder().userId(userId).build();
            medalHandlerChain.handle(greatState);
        });
        log.info("本周定时颁布勋章任务执行完毕！下次执行将与一周后");
    }

//    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始恢复定时颁布勋章任务 --> --> -->");
        long nextWeekTimestamp = getNextWeekTimestamp();
        long initialDelay = nextWeekTimestamp - System.currentTimeMillis();
        log.info("最近一次的颁布勋章任务执行时间：{}", TimerUtil.getDateFormat(new Date(nextWeekTimestamp)));
        long period = TimeUnit.DAYS.toMillis(7);
        SchedulerThreadPool.scheduleCircle(this::issueGreatStateMedal, initialDelay, period, TimeUnit.MILLISECONDS);
        log.warn("<-- <-- <-- <-- <-- 任务恢复成功 <-- <-- <-- <-- <--");
    }

}
