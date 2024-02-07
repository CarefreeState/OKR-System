package com.macaku.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.vo.TeamOkrStatisticVO;
import com.macaku.center.domain.vo.TeamOkrVO;
import com.macaku.user.domain.po.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface TeamOkrService extends IService<TeamOkr> {
    List<TeamOkr> selectChildTeams(Long id);

    TeamOkr findRootTeam(Long id);

    List<TeamOkrVO> getTeamOkrList(User user);

    TeamOkr checkManager(Long teamId, Long managerId);

    @Transactional
    Map<String, Object> grantTeamForMember(Long teamId, Long managerId, Long userId);

    List<TeamOkrStatisticVO> countCompletionRate(List<TeamOkr> teamOkrs);

    String getQRCode(Long teamId);
}
