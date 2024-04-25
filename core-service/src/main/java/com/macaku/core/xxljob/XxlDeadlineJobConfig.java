package com.macaku.core.xxljob;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.thread.timer.TimerUtil;
import com.macaku.core.domain.po.event.quadrant.FirstQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.SecondQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.service.OkrCoreService;
import com.macaku.redis.repository.RedisCache;
import com.macaku.redis.repository.RedisLock;
import com.macaku.xxljob.annotation.XxlRegister;
import com.macaku.xxljob.model.XxlJobInfo;
import com.macaku.xxljob.service.JobGroupService;
import com.macaku.xxljob.service.JobInfoService;
import com.macaku.xxljob.util.CronUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:46
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class XxlDeadlineJobConfig {

    private final static String AUTHOR = "macaku";

    private final static String ROUTE = "ROUND";

    private final static String STOPPED_JOB_CLEAR_CRON = "0 0 0 1/1 * ? *";

    private final static int TRIGGER_STATUS = 1;

    public final static String SCHEDULE_COMPLETE = "scheduledComplete";

    public final static String SCHEDULE_SECOND_QUADRANT_UPDATE = "scheduledQuadrantUpdate2";

    public final static String SCHEDULE_THIRD_QUADRANT_UPDATE = "scheduledQuadrantUpdate3";

    private final static String SCHEDULE_LOCK = "scheduleLock:";

    private final JobInfoService jobInfoService;

    private final JobGroupService jobGroupService;

    private final OkrCoreService okrCoreService;

    private final RedisLock redisLock;

    public void clear() {
        // 删除之前的任务（有些任务是真的没必要删除，有些任务需要修改，有些任务需要删除，但是判断起来太麻烦了）
        jobInfoService.removeAll(SCHEDULE_COMPLETE);
        jobInfoService.removeAll(SCHEDULE_SECOND_QUADRANT_UPDATE);
        jobInfoService.removeAll(SCHEDULE_THIRD_QUADRANT_UPDATE);
    }

    public void removeStoppedJob(String handler) {
        redisLock.tryLockDoSomething(SCHEDULE_LOCK + handler, () -> {
            jobInfoService.removeStoppedJob(handler);
        }, () -> {});
    }

    @XxlJob(value = "clearStoppedJob")
    @XxlRegister(cron = STOPPED_JOB_CLEAR_CRON, executorRouteStrategy = ROUTE,
            author = AUTHOR, triggerStatus = TRIGGER_STATUS, jobDesc = "【固定任务】清除停止的任务")
    private void clearStoppedJob() {
        removeStoppedJob(SCHEDULE_COMPLETE);
        removeStoppedJob(SCHEDULE_SECOND_QUADRANT_UPDATE);
        removeStoppedJob(SCHEDULE_THIRD_QUADRANT_UPDATE);
    }

    public <T> XxlJobInfo getJob(String jobDesc, Date deadline, T params, String handler){
        return XxlJobInfo.of(jobGroupService.getJobGroupId(), jobDesc, AUTHOR, CronUtil.getCorn(deadline),
                handler, ROUTE, TRIGGER_STATUS, JsonUtil.analyzeData(params));
    }

    public <T> void submitJob(String jobDesc, Date deadline, T params, String handler){
        XxlJobInfo xxlJobInfo = getJob(jobDesc, deadline, params, handler);
        jobInfoService.addJob(xxlJobInfo);
    }

    public <T> void updateJob(long jobId, String jobDesc, Date deadline, T params, String handler){
        XxlJobInfo xxlJobInfo = getJob(jobDesc, deadline, params, handler);
        xxlJobInfo.setId((int) jobId);
        jobInfoService.updateJob(xxlJobInfo);
    }

    @XxlJob(SCHEDULE_COMPLETE)
    public void scheduledComplete() {
        redisLock.tryLockDoSomething(SCHEDULE_LOCK + SCHEDULE_COMPLETE, () -> {
            String jobParam = XxlJobHelper.getJobParam();
            FirstQuadrantEvent firstQuadrantEvent = JsonUtil.analyzeJson(jobParam, FirstQuadrantEvent.class);
            Long coreId = firstQuadrantEvent.getCoreId();
            okrCoreService.complete(coreId);
        }, () -> {});
    }

    @XxlJob(SCHEDULE_SECOND_QUADRANT_UPDATE)
    public void scheduledUpdateSecondQuadrant() {
        redisLock.tryLockDoSomething(SCHEDULE_LOCK + SCHEDULE_SECOND_QUADRANT_UPDATE, () -> {
//            long jobId = XxlJobHelper.getJobId();
            String jobParam = XxlJobHelper.getJobParam();
            SecondQuadrantEvent secondQuadrantEvent = JsonUtil.analyzeJson(jobParam, SecondQuadrantEvent.class);
            Long id = secondQuadrantEvent.getId();
            Long coreId = secondQuadrantEvent.getCoreId();
            Integer cycle = secondQuadrantEvent.getCycle();
            Date deadline = secondQuadrantEvent.getDeadline();
            Date nextDeadline = new Date(deadline.getTime() + TimeUnit.SECONDS.toMillis(cycle));
            // 更新任务参数
            secondQuadrantEvent.setDeadline(nextDeadline);
            try {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = okrCoreService.getOkrCore(coreId).getIsOver();
                if (Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第二象限 {} 停止对截止时间的刷新", coreId, id);
                } else {
                    // 设置新的截止时间
                    SecondQuadrant secondQuadrant = new SecondQuadrant();
                    secondQuadrant.setId(id);
                    secondQuadrant.setDeadline(nextDeadline);
                    // 更新
                    Db.updateById(secondQuadrant);
                    log.warn("第二象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                    // 发起一个定时任务
                    submitJob("【动态任务】第二象限截止时间刷新  " + id, nextDeadline,
                            secondQuadrantEvent, SCHEDULE_SECOND_QUADRANT_UPDATE);
                }
            } catch (Exception e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }, () -> {});
    }

    @XxlJob(SCHEDULE_THIRD_QUADRANT_UPDATE)
    public void scheduledUpdateThirdQuadrant() {
        redisLock.tryLockDoSomething(SCHEDULE_LOCK + SCHEDULE_THIRD_QUADRANT_UPDATE, () -> {
//            long jobId = XxlJobHelper.getJobId();
            String jobParam = XxlJobHelper.getJobParam();
            ThirdQuadrantEvent thirdQuadrantEvent = JsonUtil.analyzeJson(jobParam, ThirdQuadrantEvent.class);
            Long id = thirdQuadrantEvent.getId();
            Long coreId = thirdQuadrantEvent.getCoreId();
            Integer cycle = thirdQuadrantEvent.getCycle();
            Date deadline = thirdQuadrantEvent.getDeadline();
            Date nextDeadline = new Date(deadline.getTime() + TimeUnit.SECONDS.toMillis(cycle));
            // 更新任务参数
            thirdQuadrantEvent.setDeadline(nextDeadline);
            try {
                // 如果 OKR 没有结束，更新截止时间，发起新的定时任务
                Boolean isOver = okrCoreService.getOkrCore(coreId).getIsOver();
                if(Boolean.TRUE.equals(isOver)) {
                    log.warn("OKR {} 已结束，第三象限 {} 停止对截止时间的刷新", coreId, id);
                }else {
                    ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
                    thirdQuadrant.setId(id);
                    thirdQuadrant.setDeadline(nextDeadline);
                    // 更新
                    Db.updateById(thirdQuadrant);
                    log.warn("第三象限 {} 截止时间更新 -> {}", id, TimerUtil.getDateFormat(nextDeadline));
                    // 发起一个定时任务
                    submitJob("【动态任务】第三象限截止时间刷新  " + id, nextDeadline,
                            thirdQuadrantEvent, SCHEDULE_THIRD_QUADRANT_UPDATE);
                }
            } catch (Exception e) {
                throw new GlobalServiceException(e.getMessage());
            }
        }, () -> {});
    }
}
