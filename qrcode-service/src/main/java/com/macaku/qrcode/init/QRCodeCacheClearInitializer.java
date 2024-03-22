package com.macaku.qrcode.init;

import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.TimerUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.qrcode.config.QRCodeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.TimerTask;

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
public class QRCodeCacheClearInitializer implements ApplicationListener<ApplicationStartedEvent> {

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
        log.warn("本轮清除任务结束，启动下一次清除任务...");
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
        log.warn("本轮清除任务结束，启动下一次清除任务...");
    }

    private void clearQRCodeCacheCycle(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        clearQRCodeCache(directory);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                clearQRCodeCacheCycle(directory);
            }
        }, QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
    }

    private void clearLoginQRCodeCacheCycle(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        clearLoginQRCodeCache(directory);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                clearLoginQRCodeCacheCycle(directory);
            }
        }, QRCodeConfig.WX_LOGIN_QR_CODE_TTL, QRCodeConfig.WX_LOGIN_QR_CODE_UNIT);
    }

    @Override
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

