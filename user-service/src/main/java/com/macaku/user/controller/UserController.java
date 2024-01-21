package com.macaku.user.controller;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.dto.LoginDTO;
import com.macaku.user.service.LoginService;
import io.swagger.annotations.Api;
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

    @GetMapping("/hello")
    public String test() {
        return "hello";
    }

    @PostMapping("/login")
    public SystemJsonResponse login(LoginDTO loginDTO, @NonNull @RequestHeader("type") String type) {
        loginDTO.validate();
        ServiceLoader<LoginService> loginServices = ServiceLoader.load(LoginService.class);
        Iterator<LoginService> serviceIterator = loginServices.iterator();
        Map<String, Object> data = null;
        while (serviceIterator.hasNext()) {
            LoginService loginService =  serviceIterator.next();
            if(loginService.match(type)) {
                data = loginService.login(loginDTO);
                break;
            }
        }
        return SystemJsonResponse.SYSTEM_SUCCESS(data);
    }

}
