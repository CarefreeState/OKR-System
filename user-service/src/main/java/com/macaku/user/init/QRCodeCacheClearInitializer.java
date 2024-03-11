package com.macaku.user.init;

import com.macaku.common.redis.RedisCache;
import com.macaku.common.util.TimerUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import com.macaku.user.qrcode.config.QRCodeConfig;
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

    private void clearCacheCycle(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                File[] files = directory.listFiles();
                // 如果文件没有缓存在
                Arrays.stream(files).parallel().forEach(file -> {
                    redisCache.getCacheObject(QRCodeConfig.WX_CHECK_QR_CODE_CACHE + file.getName()).orElseGet(file::delete);
                });
                clearCacheCycle(directory);
            }
        }, QRCodeConfig.WX_CHECK_QR_CODE_TTL, QRCodeConfig.WX_CHECK_QR_CODE_UNIT);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.warn("--> --> --> 应用启动成功 --> 开始清除微信绑定码的缓存 --> --> -->");
        // 查看 media/binding/ 下的文件
        String path = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.BINDING_PATH;
        File directory = new File(path);
        // 循环检查是否清除缓存
        clearCacheCycle(directory);
        log.warn("<-- <-- <-- <-- <-- 清除微信绑定码的缓存的任务启动成功 <-- <-- <-- <-- <--");
    }
}

