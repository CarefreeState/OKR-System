package com.macaku.core.service.impl.inner;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import com.macaku.core.domain.po.inner.StatusFlag;
import com.macaku.core.mapper.inner.StatusFlagMapper;
import com.macaku.core.service.inner.StatusFlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Service实现
* @createDate 2024-01-20 02:24:49
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class StatusFlagServiceImpl extends ServiceImpl<StatusFlagMapper, StatusFlag>
    implements StatusFlagService{

    private final static String FLAG_FOURTH_QUADRANT_MAP = "flagFourthQuadrantMap:";

    private final static Long FLAG_FOURTH_QUADRANT_TTL = 6L;

    private final static TimeUnit FLAG_FOURTH_QUADRANT_UNIT = TimeUnit.HOURS;

    private final RedisCache redisCache;

    @Override
    public Long addStatusFlag(StatusFlag statusFlag) {
        StatusFlag newFlag = new StatusFlag();
        String color = statusFlag.getColor();
        newFlag.setColor(color);
        String label = statusFlag.getLabel();
        newFlag.setLabel(label);
        Long fourthQuadrantId = statusFlag.getFourthQuadrantId();
        newFlag.setFourthQuadrantId(fourthQuadrantId);
        this.save(newFlag);
        Long id = newFlag.getId();
        log.info("成功为第四象限 {} 新增一条指标 {} {} -- {}", fourthQuadrantId, id, label, color);
        return id;
    }

    @Override
    public void removeStatusFlag(Long id) {
        // 逻辑删除
        boolean ret = this.lambdaUpdate().eq(StatusFlag::getId, id).remove();
        if(Boolean.TRUE.equals(ret)) {
            log.info("成功为第四象限删除一条指标 {}", id);
        }
    }

    @Override
    public void updateStatusFlag(StatusFlag statusFlag) {
        // 提取数据
        StatusFlag updateFlag = new StatusFlag();
        updateFlag.setId(statusFlag.getId());
        updateFlag.setColor(statusFlag.getColor());
        updateFlag.setLabel(statusFlag.getLabel());
        // 修改
        this.updateById(updateFlag);
        log.info("成功为第四象限修改一条指标为 -> {}", updateFlag);
    }

    @Override
    public Long getFlagFourthQuadrantId(Long id) {
        String redisKey = FLAG_FOURTH_QUADRANT_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询数据库
            Long fourthQuadrant = this.lambdaQuery()
                    .eq(StatusFlag::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.STATUS_FLAG_NOT_EXISTS)
                    ).getFourthQuadrantId();
            redisCache.setCacheObject(redisKey, fourthQuadrant, FLAG_FOURTH_QUADRANT_TTL, FLAG_FOURTH_QUADRANT_UNIT);
            return fourthQuadrant;
        });
    }
}




