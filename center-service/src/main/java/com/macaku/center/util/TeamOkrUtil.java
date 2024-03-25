package com.macaku.center.util;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.service.TeamOkrService;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.redis.RedisCache;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public final static String TEAM_ID_MANAGER_MAP = "teamIdManagerMap:";

    public final static Long TEAM_ID_NAME_TTL = 1L;

    public final static Long TEAM_ID_MANAGER_TTL = 1L;

    public final static TimeUnit TEAM_ID_NAME_UNIT = TimeUnit.DAYS;

    public final static TimeUnit TEAM_ID_MANAGER_UNIT = TimeUnit.DAYS;

    public final static  String TEAM_ROOT_MAP = "teamRootMap:";

    public final static  String TEAM_CHILD_LIST = "teamChildList:";

    public final static Long TEAM_ROOT_TTL = 30L;

    public final static Long TEAM_CHILD_TTL = 1L;

    public final static TimeUnit TEAM_ROOT_TTL_UNIT = TimeUnit.DAYS;

    public final static TimeUnit TEAM_CHILD_TTL_UNIT = TimeUnit.DAYS;

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

    public static List<Long> getChildIds(Long id) {
        String redisKey = TEAM_CHILD_LIST + id;
        return REDIS_CACHE.getCacheList(redisKey, Long.class).orElseGet(() -> {
            List<TeamOkr> teamOkrs = TEAM_OKR_SERVICE.selectChildTeams(id);
            List<Long> ids = teamOkrs.stream().parallel().map(TeamOkr::getId).collect(Collectors.toList());
            REDIS_CACHE.setOverCacheList(redisKey, ids, TEAM_CHILD_TTL, TEAM_CHILD_TTL_UNIT);
            return ids;
        });
    }

    public static List<Long> getAllChildIds(Long id) {
        Long rootId = getTeamRootId(id);
        return getChildIds(rootId);
    }

    public static void deleteChildListCache(Long teamId) {
        List<Long> ids = getAllChildIds(teamId);
        REDIS_CACHE.execute(() -> {
            ids.forEach(id -> {
                String redisKey = TEAM_CHILD_LIST + id;
                REDIS_CACHE.deleteObject(redisKey);
            });
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

    public static Long getManagerId(Long teamId) {
        // 由于团队的管理者暂时不可变，所以设置缓存
        String redisKey = TEAM_ID_MANAGER_MAP + teamId;
        return (Long) REDIS_CACHE.getCacheObject(redisKey).orElseGet(() -> {
            Long managerId = Db.lambdaQuery(TeamOkr.class).eq(TeamOkr::getId, teamId).oneOpt().orElseThrow(() ->
                    new GlobalServiceException(GlobalServiceStatusCode.TEAM_NOT_EXISTS)).getManagerId();
            REDIS_CACHE.setCacheObject(redisKey, managerId, TeamOkrUtil.TEAM_ID_MANAGER_TTL, TeamOkrUtil.TEAM_ID_MANAGER_UNIT);
            return managerId;
        });
    }
}
