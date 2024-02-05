package com.macaku.user.service;

import com.macaku.user.domain.dto.UserinfoDTO;
import com.macaku.user.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-01-22 14:18:10
*/
public interface UserService extends IService<User> {

    String getUserFlag(String code);

    void improveUserinfo(UserinfoDTO userinfoDTO, Long userId);
}
