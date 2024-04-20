package com.macaku.core.mapper.inner;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.core.domain.po.inner.StatusFlag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【status_flag(指标表)】的数据库操作Mapper
* @createDate 2024-01-20 02:24:49
* @Entity com.macaku.core.domain.po.inner.StatusFlag
*/
public interface StatusFlagMapper extends BaseMapper<StatusFlag> {

    List<StatusFlag> getStatusFlagsByUserId(@Param("userId") Long userId);

    List<StatusFlag> getStatusFlagsByQuadrantId(@Param("quadrantId") Long quadrantId);

}




