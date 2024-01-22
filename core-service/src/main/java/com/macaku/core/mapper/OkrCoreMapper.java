package com.macaku.core.mapper;

import com.macaku.core.domain.po.OkrCore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.core.domain.po.vo.OkrCoreVO;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * @author 马拉圈
 * @description 针对表【okr_core(OKR 内核表)】的数据库操作Mapper
 * @createDate 2024-01-19 21:19:05
 * @Entity com.macaku.core.domain.po.OkrCore
 */
public interface OkrCoreMapper extends BaseMapper<OkrCore> {

    Optional<OkrCoreVO> searchOkrCore(@Param("id") Long id);

}



