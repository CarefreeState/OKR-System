package com.macaku.xxljob.cookie;

import com.macaku.common.util.thread.pool.SchedulerThreadPool;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-11
 * Time: 12:24
 */
@Getter
@Setter
public class XxlJobCookie {

    private final static long TIMEOUT = 2;

    private final static TimeUnit UNIT = TimeUnit.HOURS;

    private String cookie;

    volatile private static XxlJobCookie XXL_JOB_COOKIE = null;

    private XxlJobCookie() {

    }

    private static void clearCookie() {
        XXL_JOB_COOKIE.setCookie(null);
    }

    private static void setCookie() {
        if(XXL_JOB_COOKIE == null) {
            XXL_JOB_COOKIE = new XxlJobCookie();
        }
        XXL_JOB_COOKIE.setCookie(CookieUtil.login());
        // 在 2 小时后清除 Cookie
        SchedulerThreadPool.schedule(XxlJobCookie::clearCookie, TIMEOUT, UNIT);
    }

    private static boolean isExpired() {
       return Objects.isNull(XXL_JOB_COOKIE.getCookie());
    }

    public static XxlJobCookie getXxlJobCookie() {
        if(XXL_JOB_COOKIE == null || isExpired()) {
            synchronized (XxlJobCookie.class) {
                if(XXL_JOB_COOKIE == null || isExpired()) {
                    setCookie();
                }
            }
        }
        return XXL_JOB_COOKIE;
    }

}
