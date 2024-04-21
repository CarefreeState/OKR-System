package com.macaku.core.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import com.macaku.common.util.thread.timer.TimerUtil;
import com.macaku.core.config.OkrCoreConfig;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.event.quadrant.FirstQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.SecondQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.service.QuadrantDeadlineService;
import com.macaku.redis.repository.RedisCache;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:57
 */
@Slf4j
public class QuadrantDeadlineServiceThreadImpl implements QuadrantDeadlineService {

    private final static RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    @Override
    public void clear() {

    }

    @Override
    public void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        Long coreId = firstQuadrantEvent.getCoreId();
        Date deadline = firstQuadrantEvent.getDeadline();
        // 发起一个定时任务
        SchedulerThreadPool.schedule(() -> {
            OkrCore updateOkrCore = new OkrCore();
            updateOkrCore.setId(coreId);
            updateOkrCore.setIsOver(Boolean.TRUE);
            Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
            log.warn("OKR {} 结束！ {}", coreId, TimerUtil.getDateFormat(deadline));
            REDIS_CACHE.deleteObject(OkrCoreConfig.OKR_CORE_ID_MAP + coreId);
        }, TimeUnit.MILLISECONDS.toSeconds(deadline.getTime() - System.currentTimeMillis()), TimeUnit.SECONDS);
    }

    @Override
    public void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        Long coreId = secondQuadrantEvent.getCoreId();
        Long id = secondQuadrantEvent.getId();
        Integer cycle = secondQuadrantEvent.getCycle();
        Date deadline = secondQuadrantEvent.getDeadline();
        final long deadTimestamp = deadline.getTime(); // 截止时间的时间戳
        final long nowTimestamp = System.currentTimeMillis(); // 当前时间
        final long delay = nowTimestamp == deadTimestamp ? TimeUnit.SECONDS.toMillis(cycle) : deadTimestamp - nowTimestamp;
        SchedulerThreadPool.scheduleCircle(() -> {
            final long nextDeadTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cycle);
            Date nextDeadline = new Date(nextDeadTimestamp);
            try {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = Db.lambdaQuery(OkrCore.class)
                        .eq(OkrCore::getId, coreId)
                        .select(OkrCore::getIsOver)
                        .one()
                        .getIsOver();
                if(Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第二象限 {} 停止对截止时间的刷新", coreId, id);
                }else {
                    // 设置新的截止时间
                    SecondQuadrant secondQuadrant = new SecondQuadrant();
                    secondQuadrant.setId(id);
                    secondQuadrant.setDeadline(nextDeadline);
                    // 更新
                    Db.updateById(secondQuadrant);
                    log.warn("第二象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                }
                return !isOver;
            } catch (Exception e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }, TimeUnit.MILLISECONDS.toSeconds(delay), cycle, TimeUnit.SECONDS);
    }

    @Override
    public void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        Long coreId = thirdQuadrantEvent.getCoreId();
        Long id = thirdQuadrantEvent.getId();
        Integer cycle = thirdQuadrantEvent.getCycle();
        Date deadline = thirdQuadrantEvent.getDeadline();
        final long deadTimestamp = deadline.getTime(); // 截止时间的时间戳
        final long nowTimestamp = System.currentTimeMillis(); // 当前时间
        final long delay = nowTimestamp == deadTimestamp ? TimeUnit.SECONDS.toMillis(cycle) : deadTimestamp - nowTimestamp;
        SchedulerThreadPool.scheduleCircle(() -> {
            final long nextDeadTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cycle);
            Date nextDeadline = new Date(nextDeadTimestamp);
            try {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = Db.lambdaQuery(OkrCore.class)
                        .eq(OkrCore::getId, coreId)
                        .select(OkrCore::getIsOver)
                        .one()
                        .getIsOver();
                if(Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第三象限 {} 停止对截止时间的刷新", coreId, id);
                }else {
                    // 设置新的截止时间
                    ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
                    thirdQuadrant.setId(id);
                    thirdQuadrant.setDeadline(nextDeadline);
                    // 更新
                    Db.updateById(thirdQuadrant);
                    log.warn("第三象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                }
                return !isOver;
            } catch (Exception e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }, TimeUnit.MILLISECONDS.toSeconds(delay), cycle, TimeUnit.SECONDS);
    }

}
