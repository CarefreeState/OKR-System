package com.macaku.core.init.util;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.timer.TimerUtil;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import lombok.extern.slf4j.Slf4j;

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
        log.warn("第一象限 {} 使用定时器, 任务：结束 OKR", coreId);
        TimerUtil.schedule(new TimerTask() {
            @Override
            public void run() {
                OkrCore updateOkrCore = new OkrCore();
                updateOkrCore.setId(coreId);
                updateOkrCore.setIsOver(true);
                Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
                log.warn("OKR {} 结束！ {}", coreId, deadline);
            }
        }, TimeUnit.MILLISECONDS.toSeconds(deadline.getTime() - System.currentTimeMillis()), TimeUnit.SECONDS);
    }

    public static <T> void scheduledUpdateThirdQuadrant(Long coreId, Long id, Date deadline, Integer quadrantCycle) {
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
        log.warn("第三象限 {} 使用定时器, 任务：截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
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
                    if(Boolean.TRUE.equals(isOver)) {
                        log.warn("OKR {} 已结束，第三象限 {} 停止截止时间的刷新", coreId, id);
                    }else {
                        // 设置新的截止时间
                        ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
                        thirdQuadrant.setId(id);
                        thirdQuadrant.setDeadline(nextDeadline);
                        // 更新
                        Db.updateById(thirdQuadrant);
                        log.warn("第三象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                        // 发起下一个定时事件
                        scheduledUpdateThirdQuadrant(coreId, id, nextDeadline, quadrantCycle);
                    }
                } catch (Exception e) {
                    throw new GlobalServiceException(e.getMessage());
                }
            }
        }, TimeUnit.MILLISECONDS.toSeconds(delay), TimeUnit.SECONDS);
    }

    public static <T> void scheduledUpdateSecondQuadrant(Long coreId, Long id, Date deadline, Integer quadrantCycle) {
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
        log.warn("第二象限 {} 使用定时器, 任务：截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
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
                    if(Boolean.TRUE.equals(isOver)) {
                        log.warn("OKR {} 已结束，第二象限 {} 停止截止时间的刷新", coreId, id);
                    }else {
                        // 设置新的截止时间
                        SecondQuadrant secondQuadrant = new SecondQuadrant();
                        secondQuadrant.setId(id);
                        secondQuadrant.setDeadline(nextDeadline);
                        // 更新
                        Db.updateById(secondQuadrant);
                        log.warn("第二象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                        // 发起下一个定时事件
                        scheduledUpdateSecondQuadrant(coreId, id, nextDeadline, quadrantCycle);
                    }
                } catch (Exception e) {
                    throw new GlobalServiceException(e.getMessage());
                }
            }
        }, TimeUnit.MILLISECONDS.toSeconds(delay), TimeUnit.SECONDS);
    }
}

