package com.macaku.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.user.domain.po.User;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface TeamPersonalOkrService extends IService<TeamPersonalOkr> {

    List<TeamPersonalOkrVO> getTeamPersonalOkrList(User user);

}
