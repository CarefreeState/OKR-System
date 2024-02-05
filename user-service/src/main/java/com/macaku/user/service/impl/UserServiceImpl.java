package com.macaku.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.web.HttpUtils;
import com.macaku.user.domain.dto.UserinfoDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.service.UserService;
import com.macaku.user.mapper.UserMapper;
import com.macaku.user.token.TokenUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
* @author 马拉圈
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-01-22 14:18:10
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public String getUserFlag(String code) {
        String code2SessionUrl = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, Object> param = new HashMap<String, Object>(){{
            this.put("appid", TokenUtil.APP_ID);
            this.put("secret", TokenUtil.APP_SECRET);
            this.put("js_code", code);
            this.put("grant_type", "authorization_code");
        }};
        return HttpUtils.doGet(code2SessionUrl, param);
    }

    @Override
    public void improveUserinfo(UserinfoDTO userinfoDTO, Long userId) {
        User user = BeanUtil.copyProperties(userinfoDTO, User.class);
        user.setId(userId);
        // 修改
        this.lambdaUpdate().eq(User::getId, userId).update(user);
    }
}




