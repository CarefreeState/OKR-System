package com.macaku.center.service;

import com.macaku.center.domain.po.TeamOkr;

import java.util.List;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:44
 */
public interface MemberService {

    Optional<Long> findExistsInTeam(List<TeamOkr> teamOkrs, Long userId);

    void checkExistsInTeam(Long teamId, Long userId);

}
