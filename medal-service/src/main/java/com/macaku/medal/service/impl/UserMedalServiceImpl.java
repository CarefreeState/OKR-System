package com.macaku.medal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.medal.domain.config.MedalList;
import com.macaku.medal.domain.config.MedalMap;
import com.macaku.medal.domain.po.Medal;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.domain.vo.UserMedalVO;
import com.macaku.medal.mapper.UserMedalMapper;
import com.macaku.medal.service.UserMedalService;
import com.macaku.redis.repository.RedisCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
* @author 马拉圈
* @description 针对表【user_medal(用户勋章关联表)】的数据库操作Service实现
* @createDate 2024-04-07 11:36:52
*/
@Service
@RequiredArgsConstructor
public class UserMedalServiceImpl extends ServiceImpl<UserMedalMapper, UserMedal>
    implements UserMedalService{

    private final static String USER_MEDAL_ID_MAP = "userMedalIdMap:%d:%d";

    private final static Long USER_MEDAL_ID_TTL = 1L;

    private final static TimeUnit USER_MEDAL_ID_UNIT = TimeUnit.DAYS;

    private final RedisCache redisCache;

    private final MedalMap medalMap;

    private final MedalList medalList;

    @Override
    public UserMedal getUserMedal(Long userId, Long medalId) {
        String redisKey = String.format(USER_MEDAL_ID_MAP, userId, medalId);
        Boolean exists = redisCache.isExists(redisKey);
        if(Boolean.TRUE.equals(exists)) {
            return (UserMedal) redisCache.getCacheObject(redisKey).orElse(null);
        } else {
            UserMedal userMedal = this.lambdaQuery()
                    .eq(UserMedal::getUserId, userId)
                    .eq(UserMedal::getMedalId, medalId).one();
            redisCache.setCacheObject(redisKey, userMedal, USER_MEDAL_ID_TTL, USER_MEDAL_ID_UNIT);
            return userMedal;
        }
    }

    @Override
    public void deleteDbUserMedalCache(Long userId, Long medalId) {
        String redisKey = String.format(USER_MEDAL_ID_MAP, userId, medalId);
        redisCache.deleteObject(redisKey);
    }

    private UserMedalVO userMedalMap(UserMedal userMedal) {
        UserMedalVO userMedalVO = BeanUtil.copyProperties(userMedal, UserMedalVO.class);
        Medal medal = medalMap.get(userMedal.getMedalId());
        BeanUtil.copyProperties(medal, userMedalVO);
        return userMedalVO;
    }

    @Override
    public List<UserMedalVO> getUserMedalListAll(Long userId) {
        // 获取灰色
        List<UserMedalVO> grepList = medalList.getGrepList();
        this.lambdaQuery().eq(UserMedal::getUserId, userId)
                .ne(UserMedal::getLevel, 0).isNotNull(UserMedal::getIssueTime)
                .list().stream()
                .forEach(userMedal -> {
                    Long medalId = userMedal.getMedalId();
                    int index = (int) (medalId - 1);
                    UserMedalVO userMedalVO = grepList.get(index);
                    BeanUtil.copyProperties(userMedal, userMedalVO);
                    userMedalVO.setUrl(medalMap.get(medalId).getUrl());
                });
        return grepList;
    }

    @Override
    public List<UserMedalVO> getUserMedalListUnread(Long userId) {
        return this.lambdaQuery().eq(UserMedal::getUserId, userId)
                .ne(UserMedal::getLevel, 0).isNotNull(UserMedal::getIssueTime).eq(UserMedal::getIsRead, Boolean.FALSE)
                .list().stream().parallel()
                .map(this::userMedalMap)
                .sorted(Comparator.comparing(UserMedalVO::getMedalId))
                .collect(Collectors.toList());
    }

    @Override
    public void readUserMedal(Long userId, Long medalId) {
        if (!medalMap.containsKey(medalId)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.MEDAL_NOT_EXISTS);
        }
        this.lambdaUpdate()
                .eq(UserMedal::getUserId, userId)
                .eq(UserMedal::getMedalId, medalId)
                .eq(UserMedal::getIsRead, Boolean.FALSE)
                .set(UserMedal::getIsRead, Boolean.TRUE)
                .update();
    }
}




