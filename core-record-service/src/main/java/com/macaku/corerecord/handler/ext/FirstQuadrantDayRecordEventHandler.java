package com.macaku.corerecord.handler.ext;

import com.macaku.corerecord.domain.entry.KeyResultUpdate;
import com.macaku.corerecord.handler.ApplyRecordEventHandler;
import com.macaku.corerecord.handler.util.RecordEntryUtil;
import com.macaku.corerecord.service.DayRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-22
 * Time: 16:23
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FirstQuadrantDayRecordEventHandler extends ApplyRecordEventHandler {

    private final static Class<KeyResultUpdate> RECORD_ENTRY = KeyResultUpdate.class;

    private final DayRecordService dayRecordService;

    @Override
    public void handle(Object object) {
        log.info("{} 尝试处理对象 {}", this.getClass(), object);
        RecordEntryUtil.getMedalEntry(object, RECORD_ENTRY).ifPresent(keyResultUpdate -> {
            Long coreId = keyResultUpdate.getCoreId();
            dayRecordService.recordFirstQuadrant(coreId);
        });
        super.doNextHandler(object);
    }

}
