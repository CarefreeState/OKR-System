package com.macaku.common.util.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 14:04
 */
public class IOThreadPool {

    private static final AtomicLong THEAD_ID = new AtomicLong(1);

    private static final int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = SYSTEM_CORE_SIZE * 2 + 1;

    private static final int MAXIMUM_POOL_SIZE = SYSTEM_CORE_SIZE * 3;

    private static final int KEEP_ALIVE_TIME = 3;

    private static final TimeUnit KEEP_ALIVE_TIMEUNIT = TimeUnit.SECONDS;

    private static final BlockingDeque<Runnable> BLOCKING_DEQUE = new LinkedBlockingDeque<>(MAXIMUM_POOL_SIZE);

    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, "OKR-System-Thread-IO" + THEAD_ID.getAndIncrement());

    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ExecutorService THREAD_POOL;

    private static final int AWAIT_TIME = 5;

    private static final TimeUnit AWAIT_TIMEUNIT = TimeUnit.SECONDS;

    static {
        THREAD_POOL = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIMEUNIT,
                BLOCKING_DEQUE,
                THREAD_FACTORY,
                REJECTED_EXECUTION_HANDLER
        );
    }

    public static void submit(Runnable... tasks) {
        // 提交任务
        for (int i = 0; i < tasks.length; i++) {
            THREAD_POOL.submit(tasks[i]);
        }
    }

    public static void submit(Runnable runnable) {
        THREAD_POOL.submit(runnable);
    }

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
