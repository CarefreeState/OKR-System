package com.macaku.user.interceptor.config;

import com.macaku.user.interceptor.UserLoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class VisitConfig implements WebMvcConfigurer {

    public static final String HEADER = "Login-Type";

    private final UserLoginInterceptor userLoginInterceptor;

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
        InterceptorRegistration registration =
                registry.addInterceptor(userLoginInterceptor)
                        .addPathPatterns("/**")
                        // 不拦截的路径
                        .excludePathPatterns("/user/login")
                        // 静态资源
                        .excludePathPatterns("/media/**");
        if(Boolean.TRUE.equals(swaggerCanBeVisited)) {
            registration.excludePathPatterns(swaggers);
        }
    }


}
