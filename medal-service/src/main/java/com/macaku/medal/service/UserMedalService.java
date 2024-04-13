package com.macaku.medal.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.domain.vo.UserMedalVO;

import java.util.List;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service
* @createDate 2024-04-07 11:36:52
*/
public interface UserMedalService extends IService<UserMedal> {

    UserMedal getUserMedal(Long userId, Long medalId);

    void deleteDbUserMedalCache(Long userId, Long medalId);

    List<UserMedalVO> getUserMedalListAll(Long userId);

    List<UserMedalVO> getUserMedalListUnread(Long userId);

    void readUserMedal(Long userId, Long medalId);

}
