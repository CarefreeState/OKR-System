package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.config.OkrCoreConfig;
import com.macaku.core.domain.po.event.quadrant.ThirdQuadrantEvent;
import com.macaku.redis.repository.RedisCache;
import com.macaku.core.domain.po.OkrCore;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import com.macaku.core.util.QuadrantDeadlineUtil;
import com.macaku.core.mapper.quadrant.ThirdQuadrantMapper;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:20
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class ThirdQuadrantServiceImpl extends ServiceImpl<ThirdQuadrantMapper, ThirdQuadrant>
    implements ThirdQuadrantService {

    private final static String THIRD_QUADRANT_CORE_MAP = "thirdQuadrantCoreMap:";

    private final static Long THIRD_CORE_MAP_TTL = 1L;

    private final static TimeUnit THIRD_CORE_MAP_UNIT = TimeUnit.DAYS;

    private final ThirdQuadrantMapper thirdQuadrantMapper;

    private final RedisCache redisCache;

    @Override
    public void initThirdQuadrant(InitQuadrantDTO initQuadrantDTO) {
        Long id = initQuadrantDTO.getId();
        Date deadline = this.lambdaQuery()
                .eq(ThirdQuadrant::getId, id)
                .one()
                .getDeadline();
        if(Objects.nonNull(deadline)) {
            throw new GlobalServiceException("第三象限无法再次初始化！",
                    GlobalServiceStatusCode.THIRD_QUADRANT_UPDATE_ERROR);
        }
        Integer quadrantCycle = initQuadrantDTO.getQuadrantCycle();
        deadline = initQuadrantDTO.getDeadline();
        // 查询内核 ID
        Long coreId = this.lambdaQuery()
                .eq(ThirdQuadrant::getId, id)
                .one()
                .getCoreId();
        Boolean isOver = Db.lambdaQuery(OkrCore.class)
                .eq(OkrCore::getId, coreId)
                .one()
                .getIsOver();
        if(Boolean.TRUE.equals(isOver)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.OKR_IS_OVER);
        }
        // 为 core 设置周期
        OkrCore updateOkrCore = new OkrCore();
        updateOkrCore.setId(coreId);
        updateOkrCore.setThirdQuadrantCycle(quadrantCycle);
        Db.lambdaUpdate(OkrCore.class).eq(OkrCore::getId, coreId).update(updateOkrCore);
        // 设置象限的截止时间
        ThirdQuadrant updateQuadrant = new ThirdQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(deadline);
        this.lambdaUpdate().eq(ThirdQuadrant::getId, id).update(updateQuadrant);
        // 发起一个定时任务
        ThirdQuadrantEvent event = ThirdQuadrantEvent.builder()
                .coreId(coreId).id(id).cycle(quadrantCycle).deadline(deadline).build();
        QuadrantDeadlineUtil.scheduledUpdateThirdQuadrant(event);
        // 清楚缓存
        redisCache.deleteObject(OkrCoreConfig.OKR_CORE_ID_MAP + coreId);
    }

    @Override
    public ThirdQuadrantVO searchThirdQuadrant(Long coreId) {
        return thirdQuadrantMapper.searchThirdQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.THIRD_QUADRANT_NOT_EXISTS));
    }

    @Override
    public Long getThirdQuadrantCoreId(Long id) {
        String redisKey = THIRD_QUADRANT_CORE_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询
            Long coreId = this.lambdaQuery()
                    .eq(ThirdQuadrant::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.SECOND_QUADRANT_NOT_EXISTS)
                    ).getCoreId();
            redisCache.setCacheObject(redisKey, coreId, THIRD_CORE_MAP_TTL, THIRD_CORE_MAP_UNIT);
            return coreId;
        });
    }
}




