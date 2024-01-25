package com.macaku.core.init.util;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.TimerUtil;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.init.DeadlineEventInitializer;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:53
 */
@Slf4j
public class QuadrantDeadlineUtil {

    public static void scheduledComplete(Long coreId, Date deadline) {
        // 发起一个定时任务
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                OkrCore updateOkrCore = new OkrCore();
                updateOkrCore.setId(coreId);
                updateOkrCore.setIsOver(true);
                Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
                log.info("OKR 结束！ {}", deadline);
            }
        }, TimeUnit.MILLISECONDS.toSeconds(deadline.getTime() - System.currentTimeMillis()), TimeUnit.SECONDS);
    }

    public static <T> void scheduledUpdate(Long coreId, Long id, Date deadline, Integer quadrantCycle, Class<T> clazz) {
        final long deadTimestamp = deadline.getTime();
        final long nowTimestamp = System.currentTimeMillis();
        final long nextDeadTimestamp = deadTimestamp + TimeUnit.SECONDS.toMillis(quadrantCycle);
        final long delay;
        if(nowTimestamp == deadTimestamp) {
            delay = TimeUnit.SECONDS.toMillis(quadrantCycle);
        }else {
            delay = deadTimestamp - nowTimestamp;
        }
        Date nextDeadline = new Date(nextDeadTimestamp);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                    Boolean isOver = Db.lambdaQuery(OkrCore.class)
                            .eq(OkrCore::getId, coreId)
                            .select(OkrCore::getIsOver)
                            .one()
                            .getIsOver();
                    if(isOver) {
                        log.info("OKR 已结束");
                    }else {
                        // 设置新的截止时间
                            T updateQuadrant = clazz.newInstance();
                            // 设置字段 id
                            Field idField = clazz.getDeclaredField(DeadlineEventInitializer.QUADRANT_ID);
                            idField.setAccessible(true);
                            idField.set(updateQuadrant, id);
                            idField.setAccessible(false);
                            // 设置字段 deadline
                            Field deadlineField = clazz.getDeclaredField(DeadlineEventInitializer.QUADRANT_DEADLINE);
                            deadlineField.setAccessible(true);
                            deadlineField.set(updateQuadrant, nextDeadline);
                            deadlineField.setAccessible(false);
                            // 更新
                            Db.updateById(updateQuadrant);
                            // 发起下一个定时事件
                            scheduledUpdate(coreId, id, nextDeadline, quadrantCycle, clazz);
                    }
                } catch (Exception e) {
                    throw new GlobalServiceException(e.getMessage());
                }
            }
        }, delay, TimeUnit.SECONDS);
    }
}

