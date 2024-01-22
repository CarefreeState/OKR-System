package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import com.macaku.core.service.quadrant.FourthQuadrantService;
import com.macaku.core.mapper.quadrant.FourthQuadrantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final FourthQuadrantMapper fourthQuadrantMapper;

    @Override
    public FourthQuadrantVO searchFourthQuadrant(Long coreId) {
        return fourthQuadrantMapper.searchFourthQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException(GlobalServiceStatusCode.FOURTH_QUADRANT_NOT_EXISTS));
    }
}




