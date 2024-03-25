package com.macaku.center.service;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 21:44
 */
public interface MemberService {

    Boolean findExistsInTeam(List<Long> ids, Long userId);

    void checkExistsInTeam(Long teamId, Long userId);

    Boolean isExistsInTeam(Long teamId, Long userId);

    Boolean haveExtendTeam(Long teamId, Long userId);

    void setExistsInTeam(Long teamId, Long userId);

    void setNotExistsInTeam(Long teamId, Long userId);

    void removeMember(Long teamId, Long memberOkrId, Long userId);

}
