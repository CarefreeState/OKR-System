package com.macaku.core.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.core.domain.po.event.quadrant.FirstQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.SecondQuadrantEvent;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.core.service.QuadrantDeadlineService;
import com.macaku.core.xxljob.XxlDeadlineJobConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:43
 */
@Slf4j
public class QuadrantDeadlineServiceXxlJobImpl implements QuadrantDeadlineService {

    private final static XxlDeadlineJobConfig XXL_DEADLINE_JOB_CONFIG = SpringUtil.getBean(XxlDeadlineJobConfig.class);

    @Override
    public void clear() {
        XXL_DEADLINE_JOB_CONFIG.clear();
    }

    @Override
    public void scheduledComplete(FirstQuadrantEvent firstQuadrantEvent) {
        Long coreId = firstQuadrantEvent.getCoreId();
        Date deadline = firstQuadrantEvent.getDeadline();
        // 发起一个定时任务
        XXL_DEADLINE_JOB_CONFIG.submitJob("【动态任务】目标完成 " + coreId, deadline,
                firstQuadrantEvent, XxlDeadlineJobConfig.SCHEDULE_COMPLETE);
    }

    @Override
    public void scheduledUpdateSecondQuadrant(SecondQuadrantEvent secondQuadrantEvent) {
        Long id = secondQuadrantEvent.getId();
        Date deadline = secondQuadrantEvent.getDeadline();
        // 发起一个定时任务
        XXL_DEADLINE_JOB_CONFIG.submitJob("【动态任务】第二象限截止时间刷新  " + id, deadline,
                secondQuadrantEvent, XxlDeadlineJobConfig.SCHEDULE_SECOND_QUADRANT_UPDATE);
    }

    @Override
    public void scheduledUpdateThirdQuadrant(ThirdQuadrantEvent thirdQuadrantEvent) {
        Long id = thirdQuadrantEvent.getId();
        Date deadline = thirdQuadrantEvent.getDeadline();
        // 发起一个定时任务
        XXL_DEADLINE_JOB_CONFIG.submitJob("【动态任务】第三象限截止时间刷新  " + id, deadline,
                thirdQuadrantEvent, XxlDeadlineJobConfig.SCHEDULE_THIRD__QUADRANT_UPDATE);
    }

}
