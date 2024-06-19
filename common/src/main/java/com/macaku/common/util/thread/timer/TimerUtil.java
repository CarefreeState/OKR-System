package com.macaku.common.util.thread.timer;

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

    private final static Timer TIMER = new Timer();

    public static String getDateFormat(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    public static String getOnlyDateFormat(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static void log(long delay, TimeUnit timeUnit) {
        long deadline = timeUnit.toMillis(delay) + System.currentTimeMillis();
        log.warn("计时开始，将于 “ {} ” {} 后执行，即于 {} 执行！", delay, timeUnit.name(),
                getDateFormat(new Date(deadline)));
    }

    public static void schedule(TimerTask timerTask, long delay, TimeUnit timeUnit) {
        long deadline = timeUnit.toMillis(delay) + System.currentTimeMillis();
        log.warn("计时开始，将于 “ {} ” {} 后执行，即于 {} 执行！", delay, timeUnit.name(),
                getDateFormat(new Date(deadline)));
        TIMER.schedule(timerTask, timeUnit.toMillis(delay));
    }

    public static void schedule(Runnable task, long delay, TimeUnit timeUnit) {
        schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay, timeUnit);
    }

    public static void main(String[] args) {
        schedule(() -> {
            System.out.println("666");
        }, 1000, TimeUnit.MILLISECONDS);

        schedule(() -> {
            System.out.println("666");
        }, 2500, TimeUnit.MILLISECONDS);
    }

}
