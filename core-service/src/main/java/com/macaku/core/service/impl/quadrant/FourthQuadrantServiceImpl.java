package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.redis.repository.RedisCache;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.mapper.quadrant.FourthQuadrantMapper;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
* @author 马拉圈
* @description 针对表【fourth_quadrant(第四象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@Slf4j
@RequiredArgsConstructor
public class FourthQuadrantServiceImpl extends ServiceImpl<FourthQuadrantMapper, FourthQuadrant>
    implements FourthQuadrantService{

    private final static String FOURTH_QUADRANT_CORE_MAP = "fourthQuadrantCoreMap:";

    private final static Long FOURTH_CORE_MAP_TTL = 1L;

    private final static TimeUnit FOURTH_CORE_MAP_UNIT = TimeUnit.DAYS;

    private final FourthQuadrantMapper fourthQuadrantMapper;

    private final RedisCache redisCache;

    @Override
    public FourthQuadrantVO searchFourthQuadrant(Long coreId) {
        return fourthQuadrantMapper.searchFourthQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.FOURTH_QUADRANT_NOT_EXISTS));
    }

    @Override
    public Long getFourthQuadrantCoreId(Long id) {
        String redisKey = FOURTH_QUADRANT_CORE_MAP + id;
        return (Long) redisCache.getCacheObject(redisKey).orElseGet(() -> {
            // 查询
            Long coreId = this.lambdaQuery()
                    .eq(FourthQuadrant::getId, id)
                    .oneOpt().orElseThrow(() ->
                            new GlobalServiceException(GlobalServiceStatusCode.FOURTH_QUADRANT_NOT_EXISTS)
                    ).getCoreId();
            redisCache.setCacheObject(redisKey, coreId, FOURTH_CORE_MAP_TTL, FOURTH_CORE_MAP_UNIT);
            return coreId;
        });
    }
}




