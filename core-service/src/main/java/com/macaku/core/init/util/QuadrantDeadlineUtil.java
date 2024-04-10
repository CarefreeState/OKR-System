package com.macaku.core.init.util;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.thread.timer.TimerUtil;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.event.quadrant.FirstQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.SecondQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.xxljob.executor.model.XxlJobInfo;
import com.macaku.xxljob.executor.service.JobGroupService;
import com.macaku.xxljob.executor.service.JobInfoService;
import com.macaku.xxljob.executor.util.CronUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-23
 * Time: 12:53
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuadrantDeadlineUtil {

    public final static String SCHEDULE_COMPLETE = "scheduledComplete";

    public final static String SCHEDULE_SECOND_QUADRANT_UPDATE = "scheduledQuadrantUpdate2";

    public final static String SCHEDULE_THIRD__QUADRANT_UPDATE = "scheduledQuadrantUpdate3";

    private final static JobInfoService JOB_INFO_SERVICE = SpringUtil.getBean(JobInfoService.class);

    private final static JobGroupService JOB_GROUP_SERVICE = SpringUtil.getBean(JobGroupService.class);

    private static Map<String, Object> getParams(Long coreId, Date deadline) {
        return new HashMap<String, Object>() {{
            this.put("coreId", coreId);
            this.put("deadline", deadline);
        }};
    }

    private static Map<String, Object> getParams(Long id, Long coreId, Integer cycle, Date deadline) {
        return new HashMap<String, Object>(){{
            this.put("id", id);
            this.put("coreId", coreId);
            this.put("cycle", cycle);
            this.put("deadline", deadline);
        }};
    }

    private static void submitJob(String jobDesc, Date deadline, String params, String handler){
        XxlJobInfo xxlJobInfo = XxlJobInfo.of(JOB_GROUP_SERVICE.getJobGroupId(), jobDesc, "macaku",
                CronUtil.getCorn(deadline), handler,
                "ROUND", 1, params);
        JOB_INFO_SERVICE.addJob(xxlJobInfo);
    }

    private static XxlJobInfo getJob(String jobDesc, Date deadline, Map<String, Object> params, String handler){
        return XxlJobInfo.of(JOB_GROUP_SERVICE.getJobGroupId(), jobDesc, "macaku",
                CronUtil.getCorn(deadline), handler,
                "ROUND", 1, JsonUtil.analyzeData(params));
    }

    @XxlJob(SCHEDULE_COMPLETE)
    public void scheduledComplete() {
        String jobParam = XxlJobHelper.getJobParam();
        Long coreId = JsonUtil.analyzeJsonField(jobParam, "coreId", Long.class);
        Date deadline = JsonUtil.analyzeJsonField(jobParam, "deadline", Date.class);
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(coreId);
        updateOkrCore.setIsOver(Boolean.TRUE);
        Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
        log.warn("OKR {} 结束！ {}", coreId, TimerUtil.getDateFormat(deadline));
    }

    public static void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        Long coreId = firstQuadrantEvent.getCoreId();
        Date deadline = firstQuadrantEvent.getDeadline();
        Map<String, Object> params = getParams(coreId, deadline);
        // 发起一个定时任务
        submitJob("目标完成 " + coreId, deadline, JsonUtil.analyzeData(params), SCHEDULE_COMPLETE);
        JOB_INFO_SERVICE.removeStopJob(SCHEDULE_THIRD__QUADRANT_UPDATE);
    }

//    public static void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
//        Long coreId = firstQuadrantEvent.getCoreId();
//        Date deadline = firstQuadrantEvent.getDeadline();
//        // 发起一个定时任务
//        SchedulerThreadPool.schedule(() -> {
//            OkrCore updateOkrCore = new OkrCore();
//            updateOkrCore.setId(coreId);
//            updateOkrCore.setIsOver(Boolean.TRUE);
//            Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
//            log.warn("OKR {} 结束！ {}", coreId, TimerUtil.getDateFormat(deadline));
//        }, TimeUnit.MILLISECONDS.toSeconds(deadline.getTime() - System.currentTimeMillis()), TimeUnit.SECONDS);
//    }

    @XxlJob(SCHEDULE_SECOND_QUADRANT_UPDATE)
    public void scheduledUpdateSecondQuadrant() {
        String jobParam = XxlJobHelper.getJobParam();
        Long id = JsonUtil.analyzeJsonField(jobParam, "id", Long.class);
        Long coreId = JsonUtil.analyzeJsonField(jobParam, "coreId", Long.class);
        Integer cycle = JsonUtil.analyzeJsonField(jobParam, "cycle", Integer.class);
        Date deadline = JsonUtil.analyzeJsonField(jobParam, "deadline", Date.class);
        Date nextDeadline = new Date(deadline.getTime() + TimeUnit.SECONDS.toMillis(cycle));
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
                // 发起一个定时任务
                submitJob("第二象限截止时间刷新  " + id, nextDeadline, JsonUtil.analyzeData(getParams(id, coreId, cycle, nextDeadline)), SCHEDULE_SECOND_QUADRANT_UPDATE);
                JOB_INFO_SERVICE.removeStopJob(SCHEDULE_SECOND_QUADRANT_UPDATE);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }


    public static void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        Long coreId = secondQuadrantEvent.getCoreId();
        Long id = secondQuadrantEvent.getId();
        Integer cycle = secondQuadrantEvent.getCycle();
        Date deadline = secondQuadrantEvent.getDeadline();
        Map<String, Object> params = getParams(id, coreId, cycle, deadline);
        // 发起一个定时任务
        submitJob("第二象限截止时间刷新  " + id, deadline, JsonUtil.analyzeData(params), SCHEDULE_SECOND_QUADRANT_UPDATE);
        JOB_INFO_SERVICE.removeStopJob(SCHEDULE_THIRD__QUADRANT_UPDATE);
    }

//    public static void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
//        Long coreId = secondQuadrantEvent.getCoreId();
//        Long id = secondQuadrantEvent.getId();
//        Integer cycle = secondQuadrantEvent.getCycle();
//        Date deadline = secondQuadrantEvent.getDeadline();
//        final long deadTimestamp = deadline.getTime(); // 截止时间的时间戳
//        final long nowTimestamp = System.currentTimeMillis(); // 当前时间
//        final long delay = nowTimestamp == deadTimestamp ? TimeUnit.SECONDS.toMillis(cycle) : deadTimestamp - nowTimestamp;
//        SchedulerThreadPool.scheduleCircle(() -> {
//            final long nextDeadTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cycle);
//            Date nextDeadline = new Date(nextDeadTimestamp);
//            try {
//                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
//                Boolean isOver = Db.lambdaQuery(OkrCore.class)
//                        .eq(OkrCore::getId, coreId)
//                        .select(OkrCore::getIsOver)
//                        .one()
//                        .getIsOver();
//                if(Boolean.TRUE.equals(isOver)) {
//                    log.warn("OKR {} 已结束，第二象限 {} 停止对截止时间的刷新", coreId, id);
//                }else {
//                    // 设置新的截止时间
//                    SecondQuadrant secondQuadrant = new SecondQuadrant();
//                    secondQuadrant.setId(id);
//                    secondQuadrant.setDeadline(nextDeadline);
//                    // 更新
//                    Db.updateById(secondQuadrant);
//                    log.warn("第二象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
//                }
//                return !isOver;
//            } catch (Exception e) {
//                throw new GlobalServiceException(e.getMessage());
//            }
//        }, TimeUnit.MILLISECONDS.toSeconds(delay), cycle, TimeUnit.SECONDS);
//    }

    @XxlJob(SCHEDULE_THIRD__QUADRANT_UPDATE)
    public void scheduledUpdateThirdQuadrant() {
//        long jobId = XxlJobContext.getXxlJobContext().getJobId();
        String jobParam = XxlJobHelper.getJobParam();
        Long id = JsonUtil.analyzeJsonField(jobParam, "id", Long.class);
        Long coreId = JsonUtil.analyzeJsonField(jobParam, "coreId", Long.class);
        Integer cycle = JsonUtil.analyzeJsonField(jobParam, "cycle", Integer.class);
        Date deadline = JsonUtil.analyzeJsonField(jobParam, "deadline", Date.class);
        Date nextDeadline = new Date(deadline.getTime() + TimeUnit.SECONDS.toMillis(cycle));
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
                // 发起一个定时任务
                submitJob("第三象限截止时间刷新  " + id, nextDeadline, JsonUtil.analyzeData(getParams(id, coreId, cycle, nextDeadline)), SCHEDULE_THIRD__QUADRANT_UPDATE);
                JOB_INFO_SERVICE.removeStopJob(SCHEDULE_THIRD__QUADRANT_UPDATE);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
    }


    public static void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        Long coreId = thirdQuadrantEvent.getCoreId();
        Long id = thirdQuadrantEvent.getId();
        Integer cycle = thirdQuadrantEvent.getCycle();
        Date deadline = thirdQuadrantEvent.getDeadline();
        Map<String, Object> params = getParams(id, coreId, cycle, deadline);
        // 发起一个定时任务
        submitJob("第三象限截止时间刷新  " + id, deadline, JsonUtil.analyzeData(params), SCHEDULE_THIRD__QUADRANT_UPDATE);
        JOB_INFO_SERVICE.removeStopJob(SCHEDULE_THIRD__QUADRANT_UPDATE);
    }

//    public static void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
//        Long coreId = thirdQuadrantEvent.getCoreId();
//        Long id = thirdQuadrantEvent.getId();
//        Integer cycle = thirdQuadrantEvent.getCycle();
//        Date deadline = thirdQuadrantEvent.getDeadline();
//        final long deadTimestamp = deadline.getTime(); // 截止时间的时间戳
//        final long nowTimestamp = System.currentTimeMillis(); // 当前时间
//        final long delay = nowTimestamp == deadTimestamp ? TimeUnit.SECONDS.toMillis(cycle) : deadTimestamp - nowTimestamp;
//        SchedulerThreadPool.scheduleCircle(() -> {
//            final long nextDeadTimestamp = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(cycle);
//            Date nextDeadline = new Date(nextDeadTimestamp);
//            try {
//                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
//                Boolean isOver = Db.lambdaQuery(OkrCore.class)
//                        .eq(OkrCore::getId, coreId)
//                        .select(OkrCore::getIsOver)
//                        .one()
//                        .getIsOver();
//                if(Boolean.TRUE.equals(isOver)) {
//                    log.warn("OKR {} 已结束，第三象限 {} 停止对截止时间的刷新", coreId, id);
//                }else {
//                    // 设置新的截止时间
//                    ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
//                    thirdQuadrant.setId(id);
//                    thirdQuadrant.setDeadline(nextDeadline);
//                    // 更新
//                    Db.updateById(thirdQuadrant);
//                    log.warn("第三象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
//                }
//                return !isOver;
//            } catch (Exception e) {
//                throw new GlobalServiceException(e.getMessage());
//            }
//        }, TimeUnit.MILLISECONDS.toSeconds(delay), cycle, TimeUnit.SECONDS);
//    }

}

