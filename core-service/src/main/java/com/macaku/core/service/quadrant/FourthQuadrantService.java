package com.macaku.core.service.quadrant;

import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;

/**
* @author 马拉圈
* @description 针对表【fourth_quadrant(第四象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:21
*/
public interface FourthQuadrantService extends IService<FourthQuadrant> {

    FourthQuadrantVO searchFourthQuadrant(Long coreId);

    Long getFourthQuadrantCoreId(Long id);
}
