package com.macaku.core.mapper.quadrant;

import com.macaku.core.domain.po.quadrant.FourthQuadrant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.core.domain.po.quadrant.vo.FourthQuadrantVO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【fourth_quadrant(第四象限表)】的数据库操作Mapper
* @createDate 2024-01-20 01:04:21
* @Entity com.macaku.core.domain.po.quadrant.FourthQuadrant
*/
public interface FourthQuadrantMapper extends BaseMapper<FourthQuadrant> {

    Optional<FourthQuadrantVO> searchFourthQuadrant(@Param("coreId") Long coreId);

}




