package com.macaku.center.controller;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.handler.AuthFailHandler;
import com.macaku.common.response.SystemJsonResponse;
import lombok.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-02-22
 * Time: 4:14
 */
@RestController
public class CenterController {

    @RequestMapping(AuthFailHandler.REDIRECT_URL)
    public SystemJsonResponse getUserInfo(@NonNull @RequestParam(AuthFailHandler.EXCEPTION_MESSAGE) String exceptionMessage) {
        throw new GlobalServiceException(exceptionMessage, GlobalServiceStatusCode.USER_NOT_LOGIN);
    }

}
