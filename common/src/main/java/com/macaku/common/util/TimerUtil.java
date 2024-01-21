package com.macaku.common.util;

import lombok.extern.slf4j.Slf4j;

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

    public static void schedule(TimerTask timerTask, long delay, TimeUnit timeUnit) {
        Timer timer = new Timer();
        log.warn("计时开始，将于 “ {} ” {} 后执行！", delay, timeUnit.name());
        timer.schedule(timerTask, timeUnit.toMillis(delay));
    }
}
