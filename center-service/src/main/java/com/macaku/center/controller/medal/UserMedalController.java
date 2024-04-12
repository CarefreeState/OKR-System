package com.macaku.center.controller.medal;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.medal.domain.vo.UserMedalVO;
import com.macaku.medal.service.UserMedalService;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:32
 */
@RestController
@Slf4j
@Api(tags = "用户勋章测试接口")
@RequestMapping("/medal")
@RequiredArgsConstructor
public class UserMedalController {

    private final UserMedalService userMedalService;

    @GetMapping("/list/all")
    @ApiOperation("获得用户的所有勋章")
    public SystemJsonResponse<List<UserMedalVO>> getAll() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListAll(userId);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @GetMapping("/list/unread")
    @ApiOperation("获得用户的所有未读勋章")
    public SystemJsonResponse<List<UserMedalVO>> getUnread() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        List<UserMedalVO> result = userMedalService.getUserMedalListUnread(userId);
        log.info("查询用户 {} 的所有未读勋章 : {} 个", userId, result.size());
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/read/{medalId}")
    @ApiOperation("用户知晓获得了新勋章")
    public SystemJsonResponse readUserMedal(@PathVariable("medalId") @NonNull @ApiParam("勋章 ID") Long medalId) {
        Long userId = UserRecordUtil.getUserRecord().getId();
        log.info("用户 {} 查看勋章 {}", userId, medalId);
        userMedalService.readUserMedal(userId, medalId);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
