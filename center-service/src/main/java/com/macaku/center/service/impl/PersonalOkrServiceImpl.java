package com.macaku.center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.center.component.OkrServiceSelector;
import com.macaku.center.domain.dto.unify.OkrOperateDTO;
import com.macaku.center.domain.po.PersonalOkr;
import com.macaku.center.service.OkrOperateService;
import com.macaku.center.service.PersonalOkrService;
import com.macaku.center.mapper.PersonalOkrMapper;
import com.macaku.user.domain.po.User;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Service实现
* @createDate 2024-01-20 02:25:52
*/
@Service
public class PersonalOkrServiceImpl extends ServiceImpl<PersonalOkrMapper, PersonalOkr>
    implements PersonalOkrService, OkrOperateService {

    private final static String SCOPE = OkrServiceSelector.PERSONAL_OKR_SCOPE;

    @Override
    public boolean match(String scope) {
        return SCOPE.equals(scope);
    }

    @Override
    public void createOkrCore(User user, OkrOperateDTO okrOperateDTO) {

    }
}




