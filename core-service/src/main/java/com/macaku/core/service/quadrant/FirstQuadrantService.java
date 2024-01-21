package com.macaku.core.service.quadrant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.core.domain.po.quadrant.FirstQuadrant;
import com.macaku.core.domain.po.quadrant.vo.FirstQuadrantVO;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【first_quadrant(第一象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:21
*/
public interface FirstQuadrantService extends IService<FirstQuadrant> {

    void initFirstQuadrant(FirstQuadrant firstQuadrant);

    Optional<FirstQuadrantVO> searchFirstQuadrant(Long coreId);

}
