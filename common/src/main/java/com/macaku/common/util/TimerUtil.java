package com.macaku.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 22:05
 */
@Slf4j
public class TimerUtil {

    public static String getDateFormat(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static void schedule(TimerTask timerTask, long delay, TimeUnit timeUnit) {
        Timer timer = new Timer();
        long deadline = timeUnit.toMillis(delay) + System.currentTimeMillis();
        log.warn("计时开始，将于 “ {} ” {} 后执行，即于 {} 执行！", delay, timeUnit.name(),
                getDateFormat(new Date(deadline)));
        timer.schedule(timerTask, timeUnit.toMillis(delay));
    }

    public static void cycleScheduleTask(Date deadline, Integer cycle) {
        final long deadTimestamp = deadline.getTime();
        final long nowTimestamp = System.currentTimeMillis();
        final long nextDeadTimestamp = deadTimestamp + TimeUnit.SECONDS.toMillis(cycle);
        final long delay;
        if(nowTimestamp == deadTimestamp) {
            delay = TimeUnit.SECONDS.toMillis(cycle);
        }else {
            delay = deadTimestamp - nowTimestamp;
        }
        Date nextDeadline = new Date(nextDeadTimestamp);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                log.warn("好耶！");
                cycleScheduleTask(nextDeadline, cycle);
            }
        }, TimeUnit.MILLISECONDS.toSeconds(delay), TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        cycleScheduleTask(new Date(), 5);
    }

}
