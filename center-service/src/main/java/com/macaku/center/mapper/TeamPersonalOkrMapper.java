package com.macaku.center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamMemberVO;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Mapper
* @createDate 2024-01-20 02:25:52
* @Entity com.macaku.center.domain.po.TeamPersonalOkr
*/
public interface TeamPersonalOkrMapper extends BaseMapper<TeamPersonalOkr> {

    List<TeamPersonalOkrVO> getTeamPersonalOkrList(@Param("id") Long id);

    List<TeamMemberVO> getTeamMembers(@Param("id") Long id);

}




