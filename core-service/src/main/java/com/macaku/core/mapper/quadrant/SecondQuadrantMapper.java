package com.macaku.core.mapper.quadrant;

import com.macaku.core.domain.po.quadrant.SecondQuadrant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.core.domain.po.quadrant.vo.SecondQuadrantVO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
* @author 马拉圈
* @description 针对表【second_quadrant(第二象限表)】的数据库操作Mapper
* @createDate 2024-01-20 01:04:21
* @Entity com.macaku.core.domain.po.quadrant.SecondQuadrant
*/
public interface SecondQuadrantMapper extends BaseMapper<SecondQuadrant> {

    Optional<SecondQuadrantVO> searchSecondQuadrant(@Param("coreId") Long coreId);

}




