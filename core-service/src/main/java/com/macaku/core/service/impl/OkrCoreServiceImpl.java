package com.macaku.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.core.config.OkrCoreConfig;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import com.macaku.core.domain.vo.OkrCoreVO;
import com.macaku.core.mapper.OkrCoreMapper;
import com.macaku.core.service.OkrCoreService;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
* @author 马拉圈
* @description 针对表【okr_core(OKR 内核表)】的数据库操作Service实现
* @createDate 2024-01-22 13:42:11
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class OkrCoreServiceImpl extends ServiceImpl<OkrCoreMapper, OkrCore>
    implements OkrCoreService {

    @Value("${limit.time.multiple}")
    private Integer multiple;

    private final FirstQuadrantService firstQuadrantService;

    private final SecondQuadrantService secondQuadrantService;

    private final ThirdQuadrantService thirdQuadrantService;

    private final FourthQuadrantService fourthQuadrantService;

    private final RedisCache redisCache;

    public Long createOkrCore() {
        // 1. 创建一个内核
        OkrCore okrCore = new OkrCore();
        okrCore.setIsOver(Boolean.FALSE);
        this.save(okrCore);
        Long coreID = okrCore.getId();
        log.info("新增 OKR 内核：  okr core id : {}", coreID);
        // 2. 创建一二三四象限（datetime对象）
        // 第一象限
        FirstQuadrant firstQuadrant = new FirstQuadrant();
        firstQuadrant.setCoreId(coreID);
        firstQuadrantService.save(firstQuadrant);
        // 第二象限
        SecondQuadrant secondQuadrant = new SecondQuadrant();
        secondQuadrant.setCoreId(coreID);
        secondQuadrantService.save(secondQuadrant);
        // 第三象限
        ThirdQuadrant thirdQuadrant = new ThirdQuadrant();
        thirdQuadrant.setCoreId(coreID);
        thirdQuadrantService.save(thirdQuadrant);
        // 第四象限
        FourthQuadrant fourthQuadrant = new FourthQuadrant();
        fourthQuadrant.setCoreId(coreID);
        fourthQuadrantService.save(fourthQuadrant);
        return coreID;
    }

    @Override
    public OkrCore getOkrCore(Long coreId) {
        String redisKey = OkrCoreConfig.OKR_CORE_ID_MAP + coreId;
        return (OkrCore) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            OkrCore okrCore = this.lambdaQuery().eq(OkrCore::getId, coreId).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS));
            redisCache.setCacheObject(redisKey, okrCore, OkrCoreConfig.OKR_CORE_MAP_TTL, OkrCoreConfig.OKR_CORE_MAP_UNIT);
            return okrCore;
        });
    }

    @Override
    public void checkOverThrows(Long coreId) {
        if(Boolean.TRUE.equals(getOkrCore(coreId).getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
    }

    @Override
    public void checkNonOverThrows(Long coreId) {
        if(Boolean.FALSE.equals(getOkrCore(coreId).getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_NOT_OVER);
        }
    }

    @Override
    public void removeOkrCoreCache(Long coreId) {
        redisCache.deleteObject(OkrCoreConfig.OKR_CORE_ID_MAP + coreId);
    }

    @Override
    public OkrCoreVO searchOkrCore(Long id) {
        // 查询基本的 OKR 内核
        OkrCore okrCore = getOkrCore(id);
        OkrCoreVO okrCoreVO = BeanUtil.copyProperties(okrCore, OkrCoreVO.class);
        // 查询四象限
        FutureTask<FirstQuadrantVO> task1 = new FutureTask<>(() ->
            firstQuadrantService.searchFirstQuadrant(id)
        );
        FutureTask<SecondQuadrantVO> task2 = new FutureTask<>(() ->
            secondQuadrantService.searchSecondQuadrant(id)
        );
        FutureTask<ThirdQuadrantVO> task3 = new FutureTask<>(() ->
            thirdQuadrantService.searchThirdQuadrant(id)
        );
        FutureTask<FourthQuadrantVO> task4 = new FutureTask<>(() ->
            fourthQuadrantService.searchFourthQuadrant(id)
        );
        IOThreadPool.submit(task1, task2, task3, task4);
        try {
            okrCoreVO.setFirstQuadrantVO(task1.get());
            okrCoreVO.setSecondQuadrantVO(task2.get());
            okrCoreVO.setThirdQuadrantVO(task3.get());
            okrCoreVO.setFourthQuadrantVO(task4.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new GlobalServiceException(e.getMessage());
        }
        // 返回
        return okrCoreVO;
    }

    @Override
    public void confirmCelebrateDate(Long id, Integer celebrateDay) {
        OkrCore okrCore = getOkrCore(id);
        if(Boolean.TRUE.equals(okrCore.getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        if(Objects.nonNull(okrCore.getCelebrateDay())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.CELEBRATE_DAY_CANNOT_CHANGE);
        }
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setCelebrateDay(celebrateDay);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        removeOkrCoreCache(id);
    }

    @Override
    public Date summaryOKR(Long id, String summary, Integer degree) {
        OkrCore okrCore = getOkrCore(id);
        if(Boolean.FALSE.equals(okrCore.getIsOver())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_NOT_OVER);
        }
        // 根据业务，这必然是结束时间
        Date endTime = okrCore.getUpdateTime();
        if(Objects.nonNull(okrCore.getSummary()) || Objects.nonNull(okrCore.getDegree())) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_SUMMARIZED);
        }
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setSummary(summary);
        updateOkrCore.setDegree(degree);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        removeOkrCoreCache(id);
        return endTime;
    }

    @Override
    public void complete(Long id) {
        checkOverThrows(id);
        // 构造更新对象
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(id);
        updateOkrCore.setIsOver(true);
        // 更新
        this.lambdaUpdate().eq(OkrCore::getId, id).update(updateOkrCore);
        log.info("OKR 结束！ {}", new Date());
        removeOkrCoreCache(id);
    }

    @Override
    public void checkThirdCycle(Long id, Integer quadrantCycle) {
        Integer secondQuadrantCycle = this.lambdaQuery()
                .eq(OkrCore::getId, id)
                .oneOpt().orElseThrow(() ->
                        new GlobalServiceException(GlobalServiceStatusCode.CORE_NOT_EXISTS)
                ).getSecondQuadrantCycle();
        if(Objects.isNull(secondQuadrantCycle)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.SECOND_FIRST_QUADRANT_NOT_INIT);
        }
        if(quadrantCycle.compareTo(multiple * secondQuadrantCycle) < 0) {
            throw new GlobalServiceException(GlobalServiceStatusCode.THIRD_CYCLE_TOO_SHORT);
        }
    }
}




