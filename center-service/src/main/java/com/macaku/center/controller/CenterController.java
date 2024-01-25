package com.macaku.center.controller;

import com.macaku.center.domain.po.TeamOkr;
import com.macaku.center.service.TeamOkrService;
import com.macaku.common.response.SystemJsonResponse;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 18:22
 */
@RestController
@RequestMapping("/center")
@RequiredArgsConstructor
public class CenterController {

    private final TeamOkrService teamOkrService;

    @PostMapping("/tree/{id}")
    public SystemJsonResponse<List<TeamOkr>> getAllOkr(@PathVariable("id") @NonNull @ApiParam("团队 OKR ID'") Long id) {
        List<TeamOkr> teamOkrs = teamOkrService.selectChildTeams(id);
        return SystemJsonResponse.SYSTEM_SUCCESS(teamOkrs);
    }

    @PostMapping("/root/{id}")
    public SystemJsonResponse<Long> getRoot(@PathVariable("id") @NonNull @ApiParam("团队 OKR ID'") Long id) {
        Long rootId = teamOkrService.findRootTeam(id).getId();
        return SystemJsonResponse.SYSTEM_SUCCESS(rootId);
    }


}
