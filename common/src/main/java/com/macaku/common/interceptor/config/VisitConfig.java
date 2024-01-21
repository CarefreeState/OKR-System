package com.macaku.common.interceptor.config;

import com.macaku.common.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class VisitConfig implements WebMvcConfigurer {

    @Resource
    private UserInterceptor userInterceptor;//自定义的登录拦截器


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                //不拦截路由
                .excludePathPatterns("/doc.html/**")
                .excludePathPatterns("/v3/api-docs/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/error")
                .excludePathPatterns("/favicon.ico")
                .excludePathPatterns("/swagger-resources/**")


        ;
    }


}
