package com.macaku.xxljob.core;

import com.macaku.xxljob.annotation.XxlRegister;
import com.macaku.xxljob.model.XxlJobGroup;
import com.macaku.xxljob.model.XxlJobInfo;
import com.macaku.xxljob.service.JobGroupService;
import com.macaku.xxljob.service.JobInfoService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 这个类搭配 XxlRegister 使用，是初始化型的 注解
 * 也就是之后这个注解不会产生啥效果
 * 但是在初始化的时候可以被扫描并执行相关的业务
 */
@Component
@Slf4j
public class XxlJobAutoRegister implements ApplicationListener<ApplicationReadyEvent>,
        ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private JobGroupService jobGroupService;

    @Autowired
    private JobInfoService jobInfoService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //注册执行器
        addJobGroup();
        //注册任务
        addJobInfo();
    }

    //自动注册执行器
    private void addJobGroup() {
        jobGroupService.addJobGroup();
    }

    private void addJobInfo() {
        XxlJobGroup xxlJobGroup = jobGroupService.getJobGroupOne(0);
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, Boolean.FALSE, Boolean.TRUE);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();
                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobInfo> jobInfo = jobInfoService.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
                    if (!jobInfo.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent())
                            continue;
                    }
                    XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
                    jobInfoService.addJob(xxlJobInfo);
                }
            }
        }
    }

    private XxlJobInfo createXxlJobInfo(XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
        return XxlJobInfo.builder()
                .jobGroup(xxlJobGroup.getId())
                .jobDesc(xxlRegister.jobDesc())
                .author(xxlRegister.author())
                .scheduleType("CRON")
                .scheduleConf(xxlRegister.cron())
                .glueType("BEAN")
                .executorHandler(xxlJob.value())
                .executorRouteStrategy(xxlRegister.executorRouteStrategy())
                .misfireStrategy("DO_NOTHING")
                .executorBlockStrategy("SERIAL_EXECUTION")
                .executorTimeout(0)
                .executorFailRetryCount(0)
                .glueRemark("GLUE代码初始化")
                .triggerStatus(xxlRegister.triggerStatus())
                .build();
    }

}