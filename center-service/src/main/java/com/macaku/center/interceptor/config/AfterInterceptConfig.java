package com.macaku.center.interceptor.config;

import com.macaku.center.interceptor.QuadrantInitialInterceptor;
import com.macaku.center.interceptor.ThreadLocalClearInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-14
 * Time: 0:52
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AfterInterceptConfig implements WebMvcConfigurer {

    public final static String CORE_ID = "coreId";

    private final QuadrantInitialInterceptor quadrantInitialInterceptor;

    private final ThreadLocalClearInterceptor threadLocalClearInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(quadrantInitialInterceptor)
                .addPathPatterns("/firstquadrant/init")
                .addPathPatterns("/secondquadrant/init")
                .addPathPatterns("/thirdquadrant/init");
        registry.addInterceptor(threadLocalClearInterceptor)
                .addPathPatterns("/**");
    }
}
