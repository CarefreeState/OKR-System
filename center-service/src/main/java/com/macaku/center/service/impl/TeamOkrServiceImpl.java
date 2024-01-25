package com.macaku.center.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.mapper.TeamOkrMapper;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamOkrService;
import com.macaku.user.domain.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【team_okr(团队 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class TeamOkrServiceImpl extends ServiceImpl<TeamOkrMapper, TeamOkr>
    implements TeamOkrService, OkrOperateService {

    private final static String SCOPE = OkrServiceSelector.TEAM_OKR_SCOPE;

    private final TeamOkrMapper teamOkrMapper = SpringUtil.getBean(TeamOkrMapper.class);

    @Override
    public List<TeamOkr> selectChildTeams(Long id) {
        return teamOkrMapper.selectChildTeams(id);
    }

    @Override
    public TeamOkr findRootTeam(Long id) {
        return teamOkrMapper.findRootTeam(id).orElse(null);
    }

    @Override
    public boolean match(String scope) {
        return SCOPE.equals(scope);
    }

    @Override
    public void createOkrCore(User user, OkrOperateDTO okrOperateDTO) {

    }
}




