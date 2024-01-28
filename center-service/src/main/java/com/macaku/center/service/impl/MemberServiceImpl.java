package com.macaku.center.service.impl;

import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.domain.vo.TeamPersonalOkrVO;
import com.macaku.center.mapper.TeamOkrMapper;
import com.macaku.center.mapper.TeamPersonalOkrMapper;
import com.macaku.center.service.MemberService;
import com.macaku.center.util.TeamOkrUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:45
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final static String USER_TEAM_MEMBER = "userTeamMember:";

    private final static Long USER_TEAM_MEMBER_TTL = 30L;

    private final static TimeUnit USER_TEAM_MEMBER_TTL_UNIT = TimeUnit.DAYS;

    private final TeamPersonalOkrMapper teamPersonalOkrMapper;

    private final TeamOkrMapper teamOkrMapper;

    private final RedisCache redisCache;

    @Override
    public Boolean findExistsInTeam(List<TeamOkr> teamOkrs, Long userId) {
        List<Long> ids = teamOkrs.stream()
                .parallel()
                .map(TeamOkr::getId)
                .collect(Collectors.toList());
        return teamPersonalOkrMapper.getTeamPersonalOkrList(userId).stream()
                .parallel()
                .map(TeamPersonalOkrVO::getTeamId)
                .anyMatch(ids::contains);
    }

    @Override
    public void checkExistsInTeam(Long teamId, Long userId) {
        Boolean isExists = isExistsInTeam(teamId, userId);
        if(Boolean.FALSE.equals(isExists)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.NON_TEAM_MEMBER);
        }
    }

    @Override
    public Boolean isExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
       return (Boolean) redisCache.getCacheMapValue(redisKey, userId).orElseGet(() -> {
            List<TeamOkr> teamOkrs = teamOkrMapper.selectChildTeams(rootId);
            Boolean isExists = findExistsInTeam(teamOkrs, userId);
            if(Boolean.FALSE.equals(isExists)) {
                redisCache.getCacheMap(redisKey).orElseGet(() -> {
                    Map<Long, Boolean> data = new HashMap<>();
                    data.put(userId, false);
                    redisCache.setCacheMap(redisKey, data, USER_TEAM_MEMBER_TTL, USER_TEAM_MEMBER_TTL_UNIT);
                    return null;
                });
            }else {
                redisCache.setCacheMapValue(redisKey, userId, true);
            }
            return isExists;
        });
    }

    @Override
    public void setExistsInTeam(Long teamId, Long userId) {
        Long rootId = TeamOkrUtil.getTeamRootId(teamId);
        // 查看是否有缓存
        String redisKey = USER_TEAM_MEMBER + rootId;
        redisCache.setCacheMapValue(redisKey, userId, true);
    }

}
