package com.macaku.center.component;

import com.macaku.common.locator.CustomServiceLocatorFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-04
 * Time: 10:44
 */
@Component
public class OkrServiceLocatorFactoryBean extends CustomServiceLocatorFactoryBean {

    @Override
    protected void init() {
        // 设置要代理的工厂接口
        super.setServiceLocatorInterface(OkrOperateServiceFactory.class);
        // 自定义 beanName 映射
        super.setServiceMappings(PROPERTIES.getOkrOperateServiceMap());
    }
}
