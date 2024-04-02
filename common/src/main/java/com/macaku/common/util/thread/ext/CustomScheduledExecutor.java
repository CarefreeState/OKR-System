package com.macaku.common.util.thread.ext;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-02
 * Time: 22:29
 */
public class CustomScheduledExecutor extends ScheduledThreadPoolExecutor {

    private final BlockingQueue<Runnable> customQueue;

    public CustomScheduledExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue,
                                   ThreadFactory threadFactory,
                                   RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
        this.setMaximumPoolSize(maximumPoolSize);
        this.setKeepAliveTime(keepAliveTime, unit);
        this.customQueue = Objects.isNull(workQueue) ? super.getQueue() : workQueue;
    }

    @Override
    public BlockingQueue<Runnable> getQueue() {
        return customQueue;
    }
}
