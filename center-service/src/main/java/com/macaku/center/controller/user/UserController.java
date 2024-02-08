package com.macaku.center.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.domain.vo.UserVO;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.UserinfoDTO;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.interceptor.config.VisitConfig;
import com.macaku.user.service.LoginService;
import com.macaku.user.service.UserService;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 0:07
 */
@RestController
@Api(tags = "用户测试接口")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final LoginServiceSelector loginServiceSelector;

    private final UserService userService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public SystemJsonResponse<Map<String, Object>> login(HttpServletRequest request,
                                                         @RequestBody LoginDTO loginDTO) {
        String type = request.getHeader(VisitConfig.HEADER);
        // 检查
        loginDTO.validate();
        // 选取服务
        LoginService loginService = loginServiceSelector.select(type);
        Map<String, Object> result = loginService.login(loginDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/improve")
    @ApiOperation("完善用户信息")
    public SystemJsonResponse improveUserinfo(HttpServletRequest request,
                                              @RequestBody UserinfoDTO userinfoDTO) {
        // 获取当前用户 ID
        Long userId = UserRecordUtil.getUserRecord(request).getId();
        // 完善信息
        userService.improveUserinfo(userinfoDTO, userId);
        // 删除记录
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/userinfo")
    @ApiOperation("获取用户信息")
    public SystemJsonResponse<UserVO> getUserInfo(HttpServletRequest request) {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord(request);
        // 提取信息
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 返回
        return SystemJsonResponse.SYSTEM_SUCCESS(userVO);
    }

}
