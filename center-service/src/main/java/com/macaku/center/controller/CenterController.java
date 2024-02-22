package com.macaku.center.controller;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.handler.AuthFailHandler;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.media.config.StaticMapperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

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

}
