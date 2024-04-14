package com.macaku.medal.domain.config;

import com.macaku.medal.domain.po.Medal;
import com.macaku.medal.service.MedalService;
import com.macaku.xxljob.annotation.XxlRegister;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:22
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MedalMap {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static int TRIGGER_STATUS = 0;

    private final static String CRON = "0 0/1 * * * ? *";

    private final Map<Long, Medal> medalMap = new HashMap<>();

    private final MedalService medalService;

    @PostConstruct
    public void doPostConstruct() {
        medalService.lambdaQuery().list().stream().parallel().forEach(medal -> {
            medalMap.put(medal.getId(), medal);
        });
    }

    @XxlJob(value = "medalMapCacheUpdate")
    @XxlRegister(cron = CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】更新 MedalMap 的本地缓存")
    private void medalMapCacheUpdate() {
        synchronized (medalMap) {
            log.info("更新一次 MedalMap 的本地缓存");
            medalMap.clear();
            medalService.lambdaQuery().list().stream().parallel().forEach(medal -> {
                medalMap.put(medal.getId(), medal);
            });
        }
    }

    public Medal get(Long medalId) {
        Medal medal = null;
        synchronized (medalMap) {
            medal = medalMap.get(medalId);
        }
        return medal;
    }

    public boolean containsKey(Long medalId) {
        boolean flag = Boolean.FALSE;
        synchronized (medalMap) {
            flag = medalMap.containsKey(medalId);
        }
        return flag;
    }
}
