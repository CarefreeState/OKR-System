package com.macaku.common.locator;

import com.macaku.common.util.convert.ShortCodeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 11:52
 */
@Configuration
@Setter
@Getter
@Slf4j
@ConfigurationProperties(prefix = "okr.service")
public class BeanNameProperties implements InitializingBean {

    private Properties okrOperateServiceMap;

    private Properties loginServiceMap;

    private Properties userRecordServiceMap;

    private Properties InviteQRCodeServiceMap;

    private Properties convert(Properties oldProperties) {
        return new Properties() {{
            oldProperties.forEach((name, beanName) -> {
                this.put(ShortCodeUtil.getShortCode(String.valueOf(name)), beanName);
            });
        }};
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loginServiceMap = convert(loginServiceMap);
        userRecordServiceMap = convert(userRecordServiceMap);
    }
}
