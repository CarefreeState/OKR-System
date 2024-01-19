package com.macaku.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.UserService;
import com.macaku.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-19 21:05:22
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




