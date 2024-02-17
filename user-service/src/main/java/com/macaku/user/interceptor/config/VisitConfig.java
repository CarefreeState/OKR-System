package com.macaku.user.interceptor.config;

import com.macaku.user.interceptor.ForceInterceptor;
import com.macaku.user.interceptor.UserLoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class VisitConfig implements WebMvcConfigurer {

    public static final String HEADER = "Login-Type";

    private final UserLoginInterceptor userLoginInterceptor;

    private final ForceInterceptor forceInterceptor;

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 接口文档
        String[] swaggers = {
                "/doc.html/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/error",
                "/favicon.ico",
                "/swagger-resources/**",
                "/swagger-ui/**"
        };
        registry.addInterceptor(userLoginInterceptor)
                .addPathPatterns("/**")
                // 不拦截的路径
                .excludePathPatterns("/user/login")
                // 静态资源
                .excludePathPatterns("/media/**")
                .excludePathPatterns(swaggers)
        ;
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            registry.addInterceptor(forceInterceptor)
                            .addPathPatterns(swaggers);
        }
    }


}
