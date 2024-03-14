package com.macaku.center.util;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.service.TeamOkrService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 0:38
 */
@Component
public class TeamOkrUtil {

    public final static String TEAM_ID_NAME_MAP = "teamIdNameMap:";

    public final static Long TEAM_ID_NAME_TTL = 1L;

    public final static TimeUnit TEAM_ID_NAME_UNIT = TimeUnit.DAYS;

    public final static  String TEAM_ROOT_MAP = "teamRootMap:";

    public final static Long TEAM_ROOT_TTL = 30L;

    public final static TimeUnit TEAM_ROOT_TTL_UNIT = TimeUnit.DAYS;

    public final static String CREATE_CD_FLAG = "createCDFlag:";

    public final static Long CREATE_CD = 1L;

    public final static TimeUnit CD_UNIT = TimeUnit.DAYS;

    private final static RedisCache REDIS_CACHE = SpringUtil.getBean(RedisCache.class);

    private final static TeamOkrService TEAM_OKR_SERVICE = SpringUtil.getBean(TeamOkrService.class);

    public static Long getTeamRootId(Long id) {
        String redisKey = TEAM_ROOT_MAP + id;
        return (Long) REDIS_CACHE.getCacheObject(redisKey).orElseGet(() -> {
            TeamOkr rootTeam = TEAM_OKR_SERVICE.findRootTeam(id);
            Long rootTeamId = rootTeam.getId();
            REDIS_CACHE.setCacheObject(redisKey, rootTeamId, TeamOkrUtil.TEAM_ROOT_TTL, TeamOkrUtil.TEAM_ROOT_TTL_UNIT);
            return rootTeam.getId();
        });
    }

    public static String getTeamName(Long id) {
        String redisKey = TEAM_ID_NAME_MAP + id;
        return (String) REDIS_CACHE.getCacheObject(redisKey).orElseGet(() -> {
            String teamName = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, id).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getTeamName();
            REDIS_CACHE.setCacheObject(redisKey, teamName, TeamOkrUtil.TEAM_ID_NAME_TTL, TeamOkrUtil.TEAM_ID_NAME_UNIT);
            return teamName;
        });
    }
}
