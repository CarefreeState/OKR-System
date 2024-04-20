package com.macaku.corerecord.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.corerecord.domain.po.CoreRecorder;
import com.macaku.corerecord.domain.po.DayRecord;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【day_record(OKR 内核日记录表)】的数据库操作Service
* @createDate 2024-04-21 02:02:14
*/
public interface DayRecordService extends IService<DayRecord> {

    @Transactional
    DayRecord switchRecord(CoreRecorder coreRecorder);

    List<DayRecord> getDayRecords(Long coreId);

    DayRecord getNowRecordByCoreId(Long coreId);

    void recordFirstQuadrant(Long coreId);

    void recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    void recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    void recordFourthQuadrant(Long coreId);

}
