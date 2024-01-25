package com.macaku.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.TeamPersonalOkr;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.TeamPersonalOkrService;
import com.macaku.center.mapper.TeamPersonalOkrMapper;
import com.macaku.user.domain.po.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【team_personal_okr(创建团队个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
@Slf4j
public class TeamPersonalOkrServiceImpl extends ServiceImpl<TeamPersonalOkrMapper, TeamPersonalOkr>
    implements TeamPersonalOkrService, OkrOperateService {

    private final static String SCOPE = OkrServiceSelector.TEAM_PERSONAL_OKR_SCOPE;

    @Override
    public boolean match(String scope) {
        return SCOPE.equals(scope);
    }

    @Override
    public void createOkrCore(User user, OkrOperateDTO okrOperateDTO) {

    }
}




