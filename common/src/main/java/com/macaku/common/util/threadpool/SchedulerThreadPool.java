package com.macaku.common.util.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
public class SchedulerThreadPool {

    private static final AtomicLong THEAD_ID = new AtomicLong(1);

    private static final int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = SYSTEM_CORE_SIZE * 2 + 1;

    private static final int MAXIMUM_POOL_SIZE = SYSTEM_CORE_SIZE * 3;

    private static final int KEEP_ALIVE_TIME = 3;

    private static final TimeUnit KEEP_ALIVE_TIMEUNIT = TimeUnit.SECONDS;

    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, "OKR-System-Thread-Scheduler-IO" + THEAD_ID.getAndIncrement());

    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ScheduledExecutorService THREAD_POOL;

    private static final int AWAIT_TIME = 5;

    private static final TimeUnit AWAIT_TIMEUNIT = TimeUnit.SECONDS;

    static {
        THREAD_POOL = new ScheduledThreadPoolExecutor (
                CORE_POOL_SIZE,
                THREAD_FACTORY,
                REJECTED_EXECUTION_HANDLER
        );
        ((ScheduledThreadPoolExecutor) THREAD_POOL).setMaximumPoolSize(MAXIMUM_POOL_SIZE);
        ((ScheduledThreadPoolExecutor) THREAD_POOL).setKeepAliveTime(KEEP_ALIVE_TIME, KEEP_ALIVE_TIMEUNIT);
    }

    // 添加普通定时任务
    public static void schedule(Runnable task, long delay, TimeUnit unit) {
        THREAD_POOL.schedule(task, delay, unit);
    }

    // 添加周期定时任务
    public static void scheduleCircle(Runnable task, long initialDelay, long period, TimeUnit unit) {
        THREAD_POOL.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    // 添加下个周期运行的定时任务
    public static void scheduleCircle(Runnable task, long delay, TimeUnit unit) {
        THREAD_POOL.scheduleAtFixedRate(task, delay, delay, unit);
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
}
