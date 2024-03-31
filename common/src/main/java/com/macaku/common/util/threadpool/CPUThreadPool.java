package com.macaku.common.util.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-31
 * Time: 18:39
 */
public class CPUThreadPool {

    private static final AtomicLong THEAD_ID = new AtomicLong(1);

    private static final int SYSTEM_CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int CORE_POOL_SIZE = SYSTEM_CORE_SIZE + 1;

    private static final int MAXIMUM_POOL_SIZE = SYSTEM_CORE_SIZE * 2;

    private static final int KEEP_ALIVE_TIME = 2;

    private static final TimeUnit KEEP_ALIVE_TIMEUNIT = TimeUnit.SECONDS;

    private static final BlockingDeque<Runnable> BLOCKING_DEQUE = new LinkedBlockingDeque<>(MAXIMUM_POOL_SIZE);

    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, "OKR-System-Thread-CPU" + THEAD_ID.getAndIncrement());

    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ExecutorService THREAD_POOL;

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

}
