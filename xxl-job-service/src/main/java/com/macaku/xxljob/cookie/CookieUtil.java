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

    private final static JobLoginService JOB_LOGIN_SERVICE = SpringUtil.getBean(JobLoginService.class);

    public static String login() {
        return JOB_LOGIN_SERVICE.login();
    }

    public static String getCookie() {
        return XxlJobCookie.getXxlJobCookie().getCookie();
    }

}
