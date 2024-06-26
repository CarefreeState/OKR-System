package com.macaku.email.component;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.email.service.EmailService;
import com.macaku.common.exception.GlobalServiceException;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 14:05
 */
@Component
public class EmailServiceSelector {

    public final static String EMAIL_LOGIN = "email-login";

    public final static String EMAIL_BINDING = "email-binding";

    private final ServiceLoader<EmailService> emailServices = ServiceLoader.load(EmailService.class);

    public EmailService select(String type) {
        // 选取服务
        for (EmailService emailService : emailServices) {
            if (emailService.match(type)) {
                return emailService;
            }
        }
        throw new GlobalServiceException(GlobalServiceStatusCode.REQUEST_NOT_VALID);
    }

}
