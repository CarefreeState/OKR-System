package com.macaku.xxljob.cookie;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.xxljob.service.JobLoginService;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-11
 * Time: 12:31
 */
@Component
public class CookieUtil {

    public final static String XXL_JOB_LOGIN_IDENTITY = "XXL_JOB_LOGIN_IDENTITY";

    private final static JobLoginService JOB_LOGIN_SERVICE = SpringUtil.getBean(JobLoginService.class);

    public static String login() {
        return JOB_LOGIN_SERVICE.login();
    }

    public static String getCookie() {
        String cookie = XxlJobCookie.getXxlJobCookie().getCookie();
        return String.format("%s=%s", XXL_JOB_LOGIN_IDENTITY, cookie);
    }

}
