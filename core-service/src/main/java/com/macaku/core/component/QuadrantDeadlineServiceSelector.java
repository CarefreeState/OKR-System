package com.macaku.core.component;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.core.service.QuadrantDeadlineService;
import com.macaku.core.service.impl.QuadrantDeadlineServiceXxlJobImpl;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 19:16
 */
@Component
public class QuadrantDeadlineServiceSelector {

    public QuadrantDeadlineService select() {
        ServiceLoader<QuadrantDeadlineService> quadrantDeadlineServices = ServiceLoader.load(QuadrantDeadlineService.class);
        // 选取服务
        Iterator<QuadrantDeadlineService> iterator = quadrantDeadlineServices.iterator();
        return iterator.hasNext() ? iterator.next() : SpringUtil.getBean(QuadrantDeadlineServiceXxlJobImpl.class);
    }

}
