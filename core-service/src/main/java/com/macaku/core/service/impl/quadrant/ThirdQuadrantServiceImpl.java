package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;
import com.macaku.core.mapper.quadrant.ThirdQuadrantMapper;
import com.macaku.core.service.quadrant.ThirdQuadrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final ThirdQuadrantMapper thirdQuadrantMapper;

    @Override
    public ThirdQuadrantVO searchThirdQuadrant(Long coreId) {
        return thirdQuadrantMapper.searchThirdQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.THIRD_QUADRANT_NOT_EXISTS));
    }
}




