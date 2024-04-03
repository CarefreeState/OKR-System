package com.macaku.center.controller;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.common.util.convert.JwtUtil;
import com.macaku.qrcode.service.OkrQRCodeService;
import com.macaku.user.security.handler.AuthFailHandler;
import com.macaku.user.util.ExtractUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CenterController {

    private final static String ROOT_HTML = "root.html";

    @Value("${spring.domain}")
    private String domain;

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    private final OkrQRCodeService okrQRCodeService;

    @GetMapping("/")
    public RedirectView rootHtml()  {
        String htmlUrl = domain + "/" + okrQRCodeService.getCommonQRCode();
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
