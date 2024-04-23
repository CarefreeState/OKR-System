package com.macaku.common.util.thread.local;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-14
 * Time: 0:34
 */
@Slf4j
public class ThreadLocalUtil {

    private final static ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    public static void set(final String value) {
        log.info("{} 设置本地线程资源 {}", Thread.currentThread().getName(), value);
        THREAD_LOCAL.set(value);
    }

    public static String get() {
        String value = THREAD_LOCAL.get();
        log.info("{} 获取本地线程资源 {}", Thread.currentThread().getName(), value);
        return value;
    }

    public static <T> T get(Function<String, T> function) {
        String value = get();
        return Objects.isNull(value) ? null : function.apply(value);
    }

    public static void remove() {
        log.info("{} 移除本地线程资源", Thread.currentThread().getName());
        THREAD_LOCAL.remove();
    }

}
