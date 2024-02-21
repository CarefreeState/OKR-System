package com.macaku.user.interceptor.config;

import com.macaku.user.interceptor.ForceInterceptor;
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

    public final static String HEADER = "Login-Type";

    private final ForceInterceptor forceInterceptor;

    public final static String[] swaggers = {
        "/doc.html/**",
        "/v3/api-docs/**",
        "/webjars/**",
        "/error",
        "/favicon.ico",
        "/swagger-resources/**",
        "/swagger-ui/**"
    };

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 接口文档
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            registry.addInterceptor(forceInterceptor)
                            .addPathPatterns(swaggers);
        }
    }


}
