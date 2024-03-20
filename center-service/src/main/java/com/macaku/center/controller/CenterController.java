package com.macaku.center.controller;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.handler.AuthFailHandler;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.ExtractUtil;
import com.macaku.common.util.JsonUtil;
import com.macaku.common.util.JwtUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 4:14
 */
@RestController
public class CenterController {

    private final static String ROOT_HTML = "root.html";

    @Value("${spring.domain}")
    private String domain;

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    @GetMapping("/")
    public RedirectView rootHtml()  {
        String htmlUrl = domain + "/" + StaticMapperConfig.MAP_ROOT + StaticMapperConfig.STATIC_PATH + ROOT_HTML;
        return new RedirectView(htmlUrl);
    }

    @RequestMapping(AuthFailHandler.REDIRECT_URL)
    public SystemJsonResponse getUserInfo(@RequestParam(value = AuthFailHandler.EXCEPTION_MESSAGE, required = false) String exceptionMessage) {
        throw new GlobalServiceException(Optional.ofNullable(exceptionMessage)
                .orElseGet(GlobalServiceStatusCode.USER_NOT_LOGIN::getMessage),
                GlobalServiceStatusCode.USER_NOT_LOGIN);
    }

    @GetMapping("/jwt/{openid}")
    @ApiOperation("测试阶段获取微信用户的 token")
    public SystemJsonResponse<String> getJWTByOpenid(@PathVariable("openid") @ApiParam("openid") @NonNull String openid) {
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            // 无法访问 swagger，代表这个接口无法访问
            return SystemJsonResponse.SYSTEM_FAIL();
        }
        Map<String, Object> tokenData = new HashMap<String, Object>(){{
            this.put(ExtractUtil.OPENID, openid);
        }};
        String jsonData = JsonUtil.analyzeData(tokenData);
        String token = JwtUtil.createJWT(jsonData);
        return SystemJsonResponse.SYSTEM_SUCCESS(token);
    }

}
