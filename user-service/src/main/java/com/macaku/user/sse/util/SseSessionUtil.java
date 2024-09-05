package com.macaku.user.sse.util;

import com.macaku.common.util.thread.pool.CPUThreadPool;
import com.macaku.common.util.thread.timer.TimerUtil;
import com.macaku.user.sse.session.SseSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-26
 * Time: 13:59
 */
@Slf4j
public class SseSessionUtil {

    private final static String DEFAULT_MESSAGE = "OK";

    public static void initSseEmitter(SseEmitter sseEmitter, String sessionKey) {
        // 注册回调
        sseEmitter.onCompletion(completionCallBack(sessionKey));
        sseEmitter.onTimeout(timeOutCallBack(sessionKey));
        sseEmitter.onError(errorCallBack(sessionKey));
        SseSessionMapper.put(sessionKey, sseEmitter);
    }

    public static void replyMessage(String sessionKey, Supplier<String> messageSupplier) {
        SseMessageSender.sendMessage(sessionKey, DEFAULT_MESSAGE);
        if(Objects.nonNull(messageSupplier)) {
            CPUThreadPool.submit(() -> {
                String message = messageSupplier.get();
                SseMessageSender.sendMessage(sessionKey, message);
            });
        }
    }

    public static SseEmitter createConnect(String sessionKey, Supplier<String> messageSupplier) {
        // 设置连接超时时间。0 表示不过期，默认是 30 秒，超过时间未完成会抛出异常（我比较习惯不设置时间代表不限时）
        SseEmitter sseEmitter = new SseEmitter(0L);
        initSseEmitter(sseEmitter, sessionKey);
        replyMessage(sessionKey, messageSupplier);
        return sseEmitter;
    }

    public static SseEmitter createConnect(long timeout, String sessionKey, Supplier<String> messageSupplier) {
        // 超时时间设置为 timeout ms
        SseEmitter sseEmitter = new SseEmitter(timeout);
        log.warn("{} 成功建立连接，将于 {} ms 后断开连接，即 {}", sessionKey, timeout,
                TimerUtil.getDateFormat(new Date(System.currentTimeMillis() + timeout)));
        initSseEmitter(sseEmitter, sessionKey);
        replyMessage(sessionKey, messageSupplier);
        return sseEmitter;
    }

    /**
     * 获取当前连接总数
     *
     * @return
     */
    public static int getConnectTotal(String prefix) {
        return SseSessionMapper.size(prefix);
    }

    public static List<String> getSessionKeys(String prefix) {
        return new ArrayList<>(SseSessionMapper.getKeys(prefix));
    }

    private static Runnable completionCallBack(String sessionKey) {
        return () -> {
            log.info("{} 结束 SSE 连接", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

    private static Runnable timeOutCallBack(String sessionKey) {
        return () -> {
            log.warn("{} 连接 SSE 超时", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

    private static Consumer<Throwable> errorCallBack(String sessionKey) {
        return throwable -> {
            log.error("{} 连接 SSE 异常", sessionKey);
            SseSessionMapper.remove(sessionKey);
        };
    }

}
