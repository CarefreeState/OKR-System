package com.macaku.corerecord.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.config.StatusFlagConfig;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.inner.KeyResult;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.corerecord.domain.po.CoreRecorder;
import com.macaku.corerecord.domain.po.DayRecord;
import com.macaku.corerecord.domain.po.RecordMap;
import com.macaku.corerecord.mapper.CoreRecorderMapper;
import com.macaku.corerecord.service.CoreRecorderService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【core_recorder(OKR 内核记录器表)】的数据库操作Service实现
* @createDate 2024-04-21 02:02:14
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class CoreRecorderServiceImpl extends ServiceImpl<CoreRecorderMapper, CoreRecorder>
    implements CoreRecorderService{

    private final static String CORE_RECORDER_MAP = "coreRecorderMap:";

    private final static Long CORE_RECORD_MAP_TTL = 1L;

    private final static TimeUnit CORE_RECORDER_MAP_UNIT = TimeUnit.DAYS;

    private final RedisCache redisCache;

    private final OkrCoreService okrCoreService;

    private final FirstQuadrantService firstQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final StatusFlagConfig statusFlagConfig;

    @Override
    public DayRecord createNewDayRecord(Long coreId) {
        FirstQuadrantVO firstQuadrantVO = firstQuadrantService.searchFirstQuadrant(coreId);
        List<KeyResult> keyResults = firstQuadrantVO.getKeyResults();
        Integer sum = keyResults.stream()
                .parallel()
                .map(KeyResult::getProbability)
                .reduce(Integer::sum).orElse(0);
        int size = keyResults.size();
        Long quadrantId = fourthQuadrantService.searchFourthQuadrant(coreId).getId();
        DayRecord dayRecord = new DayRecord();
        dayRecord.setCoreId(coreId);
        dayRecord.setRecordDate(new Date());
        dayRecord.setCredit1(size == 0 ? Double.valueOf(0) : Double.valueOf(sum * 1.0 / size));
        dayRecord.setCredit2(0);
        dayRecord.setCredit3(0);
        dayRecord.setCredit4((int) statusFlagConfig.calculateCoreStatusFlag(quadrantId));
        Db.save(dayRecord);
        return dayRecord;
    }

    @Override
    public void initRecordMap(CoreRecorder coreRecorder, Long coreId) {
        RecordMap recordMap = new RecordMap();
        DayRecord dayRecord = createNewDayRecord(coreId);
        recordMap.setDayRecordId(dayRecord.getId());
        coreRecorder.setCoreId(coreId);
        coreRecorder.setRecordMap(recordMap);
    }

    @Override
    public CoreRecorder initCoreRecorder(Long coreId) {
        CoreRecorder coreRecorder = new CoreRecorder();
        initRecordMap(coreRecorder, coreId);
        this.save(coreRecorder);
        return coreRecorder;
    }

    @Override
    public CoreRecorder getCoreRecorderByCoreId(Long coreId) {
        String redisKey = CORE_RECORDER_MAP + coreId;
        OkrCore okrCore = okrCoreService.getOkrCore(coreId);
        // 意味着每个记录的行为， OKR 内核都必须未完成，才会进行记录（异步，所以不影响请求的线程）
        if (Boolean.TRUE.equals(okrCore.getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        return (CoreRecorder) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            CoreRecorder coreRecorder = this.lambdaQuery().eq(CoreRecorder::getCoreId, coreId)
                    .oneOpt().orElseGet(() -> initCoreRecorder(coreId));
            redisCache.setCacheObject(redisKey, coreRecorder, CORE_RECORD_MAP_TTL, CORE_RECORDER_MAP_UNIT);
            return coreRecorder;
        });
    }

    @Override
    public void removeCache(Long coreId) {
        redisCache.deleteObject(CORE_RECORDER_MAP + coreId);
    }
}




