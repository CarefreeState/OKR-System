package com.macaku.corerecord.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.config.StatusFlagConfig;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.corerecord.domain.po.CoreRecorder;
import com.macaku.corerecord.domain.po.DayRecord;
import com.macaku.corerecord.mapper.DayRecordMapper;
import com.macaku.corerecord.service.CoreRecorderService;
import com.macaku.corerecord.service.DayRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【day_record(OKR 内核日记录表)】的数据库操作Service实现
* @createDate 2024-04-21 02:02:14
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class DayRecordServiceImpl extends ServiceImpl<DayRecordMapper, DayRecord>
    implements DayRecordService{

    private final CoreRecorderService coreRecorderService;

    private final FirstQuadrantService firstQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final StatusFlagConfig statusFlagConfig;

    private boolean checkNeedSwitch(long gap) {
        // 相差大于一天就代表是隔天了
        return gap >= TimeUnit.DAYS.toMillis(1);
    }

    private int getIncrement(Boolean isCompleted, Boolean oldCompleted) {
        if(Boolean.TRUE.equals(oldCompleted)) {
            return Boolean.TRUE.equals(isCompleted) ? 0 : -1;
        }else {
            return Boolean.TRUE.equals(isCompleted) ? 1 : 0;
        }
    }

    @Override
    public DayRecord getNowRecordByCoreId(Long coreId) {
        CoreRecorder coreRecorder = coreRecorderService.getCoreRecorderByCoreId(coreId);
        Long dayRecordId = coreRecorder.getRecordMap().getDayRecordId();
        DayRecord dayRecord = Db.lambdaQuery(DayRecord.class)
                .eq(DayRecord::getId, dayRecordId)
                .oneOpt()
                .orElseThrow(() -> new GlobalServiceException(GlobalServiceStatusCode.DAY_RECORD_NOT_EXISTS));
        Date recordDate = dayRecord.getRecordDate();
        Date today = new Date();
        long gap = today.getTime() - recordDate.getTime();
        if(Boolean.TRUE.equals(checkNeedSwitch(gap))) {
            dayRecord = coreRecorderService.switchRecord(coreRecorder);
        }
        return dayRecord;
    }

    @Override
    public List<DayRecord> getDayRecords(Long coreId) {
        getNowRecordByCoreId(coreId);// 保证记录器指向的是今天的记录
        return this.lambdaQuery().eq(DayRecord::getCoreId, coreId)
                .list()
                .stream()
                .sorted(Comparator.comparing(DayRecord::getId))
                .collect(Collectors.toList());
    }


    @Override
    public void recordFirstQuadrant(Long coreId) {
        DayRecord nowRecord = getNowRecordByCoreId(coreId);
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        List<KeyResult> keyResults = firstQuadrantVO.getKeyResults();
        Integer sum = keyResults.stream()
                .parallel()
                .map(KeyResult::getProbability)
                .reduce(Integer::sum).orElse(0);
        int size = keyResults.size();
        double credit1 = size == 0 ? Double.valueOf(0) : Double.valueOf(sum * 1.0 / size);
        this.lambdaUpdate()
                .eq(DayRecord::getId, nowRecord.getId())
                .set(DayRecord::getCredit1, credit1)
                .update();
    }

    @Override
    public void recordSecondQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        DayRecord nowRecord = getNowRecordByCoreId(coreId);
        Integer credit2 = nowRecord.getCredit2();
        int increment = getIncrement(isCompleted, oldCompleted);
        log.info("OKR {} 第二象限积分 + {} ", coreId, increment);
        if(increment != 0) {
            this.lambdaUpdate()
                    .eq(DayRecord::getId, nowRecord.getId())
                    .set(DayRecord::getCredit2, credit2 + increment)
                    .update();
        }
    }

    @Override
    public void recordThirdQuadrant(Long coreId, Boolean isCompleted, Boolean oldCompleted) {
        DayRecord nowRecord = getNowRecordByCoreId(coreId);
        Integer credit3 = nowRecord.getCredit2();
        int increment = getIncrement(isCompleted, oldCompleted);
        log.info("OKR {} 第三象限积分 + {} ", coreId, increment);
        if(increment != 0) {
            this.lambdaUpdate()
                    .eq(DayRecord::getId, nowRecord.getId())
                    .set(DayRecord::getCredit3, credit3 + increment)
                    .update();
        }
    }

    @Override
    public void recordFourthQuadrant(Long coreId) {
        DayRecord nowRecord = getNowRecordByCoreId(coreId);
        Long quadrantId = fourthQuadrantService.searchFourthQuadrant(coreId).getId();
        this.lambdaUpdate()
                .eq(DayRecord::getId, nowRecord.getId())
                .set(DayRecord::getCredit4, (int) statusFlagConfig.calculateCoreStatusFlag(quadrantId))
                .update();

    }
}




