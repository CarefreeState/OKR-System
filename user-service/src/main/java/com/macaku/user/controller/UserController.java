package com.macaku.user.controller;

import com.macaku.common.interceptor.config.VisitConfig;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

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

//    @PostMapping("/login")
//    @ApiOperation("这里传递的参数根据具体的登录方式传递对应的数据！")
//    public SystemJsonResponse<Map<String, Object>> login(@RequestParam Map<?, ?> data, @NonNull @RequestHeader("type") String type) {
//        ServiceLoader<LoginService> loginServices = ServiceLoader.load(LoginService.class);
//        Iterator<LoginService> serviceIterator = loginServices.iterator();
//        Map<String, Object> result = null;
//        while (serviceIterator.hasNext()) {
//            LoginService loginService =  serviceIterator.next();
//            if(loginService.match(type)) {
//                result = loginService.login(data);
//                break;
//            }
//        }
//        return SystemJsonResponse.SYSTEM_SUCCESS(result);
//    }

    @PostMapping("/login")
    @ApiOperation("这里传递的参数根据具体的登录方式传递对应的数据！")
    public SystemJsonResponse<Map<String, Object>> login(LoginDTO loginDTO, @NonNull @RequestHeader(VisitConfig.HEADER) String type) {
        // 检查
        loginDTO.validate();
        // 选取服务
        ServiceLoader<LoginService> loginServices = ServiceLoader.load(LoginService.class);
        Iterator<LoginService> serviceIterator = loginServices.iterator();
        Map<String, Object> result = null;
        while (serviceIterator.hasNext()) {
            LoginService loginService =  serviceIterator.next();
            if(loginService.match(type)) {
                result = loginService.login(loginDTO);
                break;
            }
        }
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

}
