package com.macaku.user.component;

import com.macaku.common.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 13:38
 */
@Component
public class UserRecordServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(UserRecordServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getUserRecordServiceMap());
    }
}
