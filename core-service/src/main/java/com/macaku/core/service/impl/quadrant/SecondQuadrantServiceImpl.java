package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import com.macaku.core.service.quadrant.SecondQuadrantService;
import com.macaku.core.mapper.quadrant.SecondQuadrantMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class SecondQuadrantServiceImpl extends ServiceImpl<SecondQuadrantMapper, SecondQuadrant>
    implements SecondQuadrantService{

    private final SecondQuadrantMapper secondQuadrantMapper;

    @Override
    public SecondQuadrantVO searchSecondQuadrant(Long coreId) {
        return secondQuadrantMapper.searchSecondQuadrant(coreId).orElseThrow(() ->
            new GlobalServiceException(GlobalServiceStatusCode.SECOND_QUADRANT_NOT_EXISTS)
        );
    }
}




