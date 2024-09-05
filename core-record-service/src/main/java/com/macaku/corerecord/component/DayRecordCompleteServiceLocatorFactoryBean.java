package com.macaku.corerecord.component;

import com.macaku.common.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 16:48
 */
@Component
public class DayRecordCompleteServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        super.setServiceLocatorInterface(DayaRecordCompleteServiceFactory.class);
        super.setServiceMappings(PROPERTIES.getDayRecordCompleteServiceMap());
    }
}
