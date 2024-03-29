package com.macaku.center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.center.domain.po.PersonalOkr;
import com.macaku.center.domain.vo.PersonalOkrVO;
import com.macaku.user.domain.po.User;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【personal_okr(个人 OKR 表)】的数据库操作Service
* @createDate 2024-01-20 02:25:52
*/
public interface PersonalOkrService extends IService<PersonalOkr> {

    List<PersonalOkrVO> getPersonalOkrList(User user);
}
