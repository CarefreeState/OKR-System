package com.macaku.qrcode.init;

import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import com.macaku.qrcode.config.QRCodeConfig;
import com.macaku.redis.repository.RedisCache;
import com.macaku.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-11
 * Time: 20:57
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class QRCodeCacheClearInitializer  {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static String LOGIN_CLEAR_CRON = "0 0/1 * * * ? *";

    private final static String BINDING_CLEAR_CRON = "0 0/5 * * * ? *";

    private final RedisCache redisCache;

    private void clearQRCodeCache(File directory) {
        File[] files = directory.listFiles();
        // 如果文件没有缓存在
        Arrays.stream(files).parallel().forEach(file -> {
            String fileName = file.getName();
            redisCache.getCacheObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + fileName).orElseGet(() -> {
                log.warn("文件 {} 逻辑失效，删除！", fileName);
                return file.delete();
            });
        });
        log.warn("clearQRCodeCache 本轮清除任务结束，启动下一次清除任务...");
    }

    private void clearLoginQRCodeCache(File directory) {
        File[] files = directory.listFiles();
        // 如果文件没有缓存在
        Arrays.stream(files).parallel().forEach(file -> {
            String fileName = file.getName();
            redisCache.getCacheObject(QRCodeConfig.WX_LOGIN_QR_CODE_CACHE + fileName).orElseGet(() -> {
                log.warn("文件 {} 逻辑失效，删除！", fileName);
                return file.delete();
            });
        });
        log.warn("clearLoginQRCodeCache 本轮清除任务结束，启动下一次清除任务...");
    }

    private void clearQRCodeCacheCycle(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        SchedulerThreadPool.scheduleCircle(() -> {
            clearQRCodeCache(directory);
        }, 0, QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
    }

    private void clearLoginQRCodeCacheCycle(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        SchedulerThreadPool.scheduleCircle(() -> {
            clearLoginQRCodeCache(directory);
        }, 0, QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
    }

    @XxlJob(value = "clearLoginQRCodeCache")
    @XxlRegister(cron = LOGIN_CLEAR_CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = 1, jobDesc = "清除登录码的缓存")
    private void clearLoginQRCodeCache() {
        // 查看 media/login/ 下的文件
        String path = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.LOGIN_PATH;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 循环检查是否清除缓存
        clearLoginQRCodeCache(directory);
    }

    @XxlJob(value = "clearQRCodeCache")
    @XxlRegister(cron = BINDING_CLEAR_CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = 1, jobDesc = "清除绑定码的缓存")
    private void clearQRCodeCache() {
        // 查看 media/binding/ 下的文件
        String path = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.BINDING_PATH;
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        clearQRCodeCache(directory);
    }

    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始清除微信小程序码的缓存 --> --> -->");
        // 查看 media/binding/ 下的文件
        String path = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.BINDING_PATH;
        File directory = new File(path);
        // 循环检查是否清除缓存
        clearQRCodeCacheCycle(directory);
        // 查看 media/login/ 下的文件
        path = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.LOGIN_PATH;
        directory = new File(path);
        // 循环检查是否清除缓存
        clearLoginQRCodeCacheCycle(directory);
        log.warn("<-- <-- <-- <-- <-- 清除微信小程序码的缓存的任务启动成功 <-- <-- <-- <-- <--");
    }

}

