package com.macaku.common.util.thread.pool;

import com.macaku.common.util.thread.ext.CustomScheduledExecutor;
import com.macaku.common.util.thread.timer.TimerUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class SchedulerThreadPool {

    private static final AtomicLong THEAD_ID = new AtomicLong(1);

    private static final int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = SYSTEM_CORE_SIZE * 2 + 1;

    private static final int MAXIMUM_POOL_SIZE = SYSTEM_CORE_SIZE * 3;

    private static final int KEEP_ALIVE_TIME = 3;

    private static final TimeUnit KEEP_ALIVE_TIMEUNIT = TimeUnit.SECONDS;

    private static final BlockingDeque<Runnable> BLOCKING_DEQUE = null; // null 代表使用默认的阻塞队列

    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, "OKR-System-Thread-Scheduler-IO" + THEAD_ID.getAndIncrement());

    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ScheduledExecutorService THREAD_POOL;

    private static final int AWAIT_TIME = 5;

    private static final TimeUnit AWAIT_TIMEUNIT = TimeUnit.SECONDS;

    static {
        THREAD_POOL = new CustomScheduledExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIMEUNIT,
                BLOCKING_DEQUE,
                THREAD_FACTORY,
                REJECTED_EXECUTION_HANDLER
        );
    }

    // 添加普通定时任务
    public static void schedule(Runnable task, long delay, TimeUnit unit) {
        TimerUtil.log(delay, unit);
        THREAD_POOL.schedule(task, delay, unit);
    }

    // 添加周期定时任务
    public static void scheduleCircle(Runnable task, long initialDelay, long period, TimeUnit unit) {
        TimerUtil.log(initialDelay, unit);
        THREAD_POOL.scheduleAtFixedRate(() -> {
            task.run();
            TimerUtil.log(period, unit);
        }, initialDelay, period, unit);
    }

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Runnable task, long delay, TimeUnit unit) {
        TimerUtil.log(delay, unit);
        THREAD_POOL.scheduleAtFixedRate(() -> {
            task.run();
            TimerUtil.log(delay, unit);
        }, delay, delay, unit);
    }

    /**
     * 函数式接口： Runnable run、Consumer accept、Supplier get、Function apply
     */

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Consumer<Map<String, Object>> task, Map<String, Object> session, long delay, TimeUnit unit) {
        task.accept(session);
        schedule(() -> {
            task.accept(session);
            scheduleCircle(task, session, delay, unit);
        }, delay, unit);
    }

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Consumer<Map<String, Object>> task, Map<String, Object> session, long initialDelay, long period, TimeUnit unit) {
        schedule(() -> {
            scheduleCircle(task, session, period, unit);
        }, initialDelay, unit);
    }

    // 添加下个周期运行的定时任务
    public static <T> void scheduleCircle(Consumer<T> task, T object, long delay, TimeUnit unit) {
        task.accept(object);
        schedule(() -> {
            task.accept(object);
            scheduleCircle(task, object, delay, unit);
        }, delay, unit);
    }

    // 添加下个周期运行的定时任务
    public static <T> void scheduleCircle(Consumer<T> task, T object, long initialDelay, long period, TimeUnit unit) {
        schedule(() -> {
            scheduleCircle(task, object, period, unit);
        }, initialDelay, unit);
    }

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Supplier<Boolean> task, long delay, TimeUnit unit) {
        if(Boolean.TRUE.equals(task.get())) {
            schedule(() -> {
                if(Boolean.TRUE.equals(task.get())) {
                    scheduleCircle(task, delay, unit);
                }
            }, delay, unit);
        }
    }

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Supplier<Boolean> task, long initialDelay, long period, TimeUnit unit) {
        schedule(() -> {
            scheduleCircle(task, period, unit);
        }, initialDelay, unit);
    }

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        scheduleCircle(() -> {
            if(atomicInteger.get() < 5) {
                atomicInteger.incrementAndGet();
                return Boolean.TRUE;
            }else {
                return Boolean.FALSE;
            }
        }, 2, TimeUnit.SECONDS);
    }

    // 关闭线程池
    public static void shutdown() {
        THREAD_POOL.shutdown();
        try {
            if (!THREAD_POOL.awaitTermination(AWAIT_TIME, AWAIT_TIMEUNIT)) {
                THREAD_POOL.shutdownNow();
            }
        } catch (InterruptedException e) {
            THREAD_POOL.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void remove(Runnable task) {
        ((ScheduledThreadPoolExecutor) THREAD_POOL).remove(task);
    }
}
