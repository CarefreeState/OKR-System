package com.macaku.center.controller.user;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.interceptor.config.VisitConfig;
import com.macaku.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    @ApiOperation("这里传递的参数根据具体的登录方式传递对应的数据！")
    public SystemJsonResponse<Map<String, Object>> login(HttpServletRequest request, LoginDTO loginDTO) {
        String type = request.getHeader(VisitConfig.HEADER);
        // 检查
        loginDTO.validate();
        // 选取服务
        LoginService loginService = loginServiceSelector.select(type);
        Map<String, Object> result = loginService.login(loginDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

}
