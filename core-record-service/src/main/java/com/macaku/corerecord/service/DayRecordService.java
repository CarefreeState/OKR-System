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

    DayRecord createNewDayRecord(Long coreId);

    /**
     * 此方法可以让 coreRecorder 的 recordMap 存在，且指向最新的 record
     * @param coreRecorder
     * @return
     */
    DayRecord switchRecord(CoreRecorder coreRecorder);

    DayRecord getNowRecordByCoreId(Long coreId);

    @Transactional
    List<DayRecord> getDayRecords(Long coreId);

    @Transactional
    void recordFirstQuadrant(Long coreId);

    @Transactional
    void recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    @Transactional
    void recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted);

    @Transactional
    void recordFourthQuadrant(Long coreId);

}
