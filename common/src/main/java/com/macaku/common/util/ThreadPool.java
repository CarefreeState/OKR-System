package com.macaku.common.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 14:04
 */
public class ThreadPool {

    public static final int THREAD_NUMBER = 10;

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(THREAD_NUMBER);

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
