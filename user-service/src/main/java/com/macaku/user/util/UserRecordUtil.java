package com.macaku.user.util;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.user.domain.po.User;
import com.macaku.user.interceptor.config.VisitConfig;
import com.macaku.user.service.UserRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:58
 */
@Component
@Slf4j
public class UserRecordUtil {

    public static User getUserRecord(HttpServletRequest request) {
        String type = request.getHeader(VisitConfig.HEADER);
        if(!StringUtils.hasText(type)) {
            throw new GlobalServiceException("拦截路径：" + request.getRequestURI(),
                    GlobalServiceStatusCode.HEAD_NOT_VALID);
        }
        ServiceLoader<UserRecordService> interceptServices = ServiceLoader.load(UserRecordService.class);
        Iterator<UserRecordService> serviceIterator = interceptServices.iterator();
        while (serviceIterator.hasNext()) {
            UserRecordService interceptService = serviceIterator.next();
            if(interceptService.match(type)) {
                return interceptService.getRecord(request).orElseThrow(() ->
                    new GlobalServiceException("拦截路径：" + request.getRequestURI(),
                            GlobalServiceStatusCode.HEAD_NOT_VALID)
                );
            }
        }
        throw new GlobalServiceException("拦截路径：" + request.getRequestURI(),
                GlobalServiceStatusCode.HEAD_NOT_VALID);
    }

}
