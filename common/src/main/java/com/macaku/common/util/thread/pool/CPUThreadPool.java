package com.macaku.common.util.thread.pool;

import com.macaku.common.exception.GlobalServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-31
 * Time: 18:39
 */
@Slf4j
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

    private static final int AWAIT_TIME = 5;

    private static final TimeUnit AWAIT_TIMEUNIT = TimeUnit.SECONDS;

    private static final int DEFAULT_TASK_NUMBER = 30;

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

    public static <T> void operateBatch(List<T> dataList, Consumer<T> consumer) {
        if(Objects.isNull(dataList)) {
            return;
        }
        int size = dataList.size();
        if(size == 0) {
            return;
        }
        // 计算多少个线程，每个线程多少个任务
        int taskNumber = DEFAULT_TASK_NUMBER;
        int threadNumber = size / taskNumber;
        while (taskNumber * threadNumber < size) {
            threadNumber++;
        }
        if(threadNumber > CORE_POOL_SIZE) {
            threadNumber = CORE_POOL_SIZE;
            taskNumber = size / threadNumber;
        }
        while (taskNumber * threadNumber < size) {
            taskNumber++;
        }
        log.info("启动 {} 个线程，每个线程处理 {} 个任务", threadNumber, taskNumber);
        CountDownLatch latch = new CountDownLatch(threadNumber);
        for (int i = 0; i < size; i += taskNumber) {
            final int from = i;
            final int to = Math.min(i + taskNumber, size);
            submit(() -> {
                log.info("分段操作 [{}, {})", from, to);
                dataList.subList(from, to).forEach(consumer);
                latch.countDown();
            });
        }
        try {
            latch.await();
            log.info("分段批量操作执行完毕");
        } catch (InterruptedException e) {
            throw new GlobalServiceException(e.getMessage());
        }
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
