package com.macaku.core.service.quadrant;

import com.macaku.core.domain.po.quadrant.ThirdQuadrant;
import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.core.domain.po.quadrant.vo.ThirdQuadrantVO;

/**
* @author 马拉圈
* @description 针对表【third_quadrant(第三象限表)】的数据库操作Service
* @createDate 2024-01-20 01:04:20
*/
public interface ThirdQuadrantService extends IService<ThirdQuadrant> {

    ThirdQuadrantVO searchThirdQuadrant(Long coreId);

}
