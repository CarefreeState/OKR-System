package com.macaku.core.init.handler.ext;

import com.macaku.core.domain.po.event.DeadlineEvent;
import com.macaku.core.init.handler.EventHandler;
import com.macaku.core.init.util.QuadrantDeadlineUtil;
import com.macaku.core.service.OkrCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-12
 * Time: 9:49
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FirstQuadrantEventHandler extends EventHandler {

    private final OkrCoreService okrCoreService;

    @Override
    public void handle(DeadlineEvent deadlineEvent, long nowTimestamp) {
        Long id = deadlineEvent.getId();
        Date firstQuadrantDeadline = deadlineEvent.getFirstQuadrantDeadline();
        log.warn("处理事件：内核 ID {}，第一象限截止时间 {}", id, firstQuadrantDeadline);
        // 1. 判断是否截止
        if(Objects.nonNull(firstQuadrantDeadline) &&
                firstQuadrantDeadline.getTime() <= nowTimestamp) {
            okrCoreService.complete(id);
            return; // 责任链终止
        }
        // 2. 是否设置了第一象限截止时间（这里一定代表未截止）
        if(Objects.nonNull(firstQuadrantDeadline)) {
            QuadrantDeadlineUtil.scheduledComplete(id, firstQuadrantDeadline);
        }
        super.doNextHandler(deadlineEvent, nowTimestamp);//执行下一个责任处理器
    }
}
