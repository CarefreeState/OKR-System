package com.macaku.core.service.impl.quadrant;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;
import com.macaku.core.mapper.quadrant.FirstQuadrantMapper;
import com.macaku.core.service.quadrant.FirstQuadrantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
* @author 马拉圈
* @description 针对表【first_quadrant(第一象限表)】的数据库操作Service实现
* @createDate 2024-01-20 01:04:21
*/
@Service
@RequiredArgsConstructor
public class FirstQuadrantServiceImpl extends ServiceImpl<FirstQuadrantMapper, FirstQuadrant>
    implements FirstQuadrantService {

    private final FirstQuadrantMapper firstQuadrantMapper;

    @Override
    public void initFirstQuadrant(FirstQuadrant firstQuadrant) {
        Long id = firstQuadrant.getId();
        // 查询是否是第一次修改
        FirstQuadrant quadrant = this.lambdaQuery()
                .eq(FirstQuadrant::getId, id)
                .select(FirstQuadrant::getDeadline, FirstQuadrant::getObjective)
                .one();
        if(StringUtils.hasText(quadrant.getObjective()) || Objects.nonNull(quadrant.getDeadline())) {
            throw new GlobalServiceException("第一象限无法再次初始化！",
                    GlobalServiceStatusCode.FIRST_QUADRANT_UPDATE_ERROR);
        }
        // 构造对象
        FirstQuadrant updateQuadrant = new FirstQuadrant();
        updateQuadrant.setId(id);
        updateQuadrant.setDeadline(firstQuadrant.getDeadline());
        updateQuadrant.setObjective(firstQuadrant.getObjective());
        // 更新
        this.updateById(updateQuadrant);
    }

    @Override
    public FirstQuadrantVO searchFirstQuadrant(Long coreId) {
        return firstQuadrantMapper.searchFirstQuadrant(coreId).orElseThrow(() ->
                new GlobalServiceException("内核 ID: " + coreId, GlobalServiceStatusCode.FIRST_QUADRANT_NOT_EXISTS)
        );
    }
}




