package com.macaku.user.interceptor.config;

import com.macaku.user.interceptor.UserLoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class VisitConfig implements WebMvcConfigurer {

    public static final String HEADER = "Login-Type";

    private final UserLoginInterceptor userLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(userLoginInterceptor)
                    .addPathPatterns("/**")
                    // 不拦截的路径
                    .excludePathPatterns("/user/login")
                    // 静态资源
                    .excludePathPatterns("/media/**")
                    // 接口文档
                    .excludePathPatterns("/doc.html/**")
                    .excludePathPatterns("/v3/api-docs/**")
                    .excludePathPatterns("/webjars/**")
                    .excludePathPatterns("/error")
                    .excludePathPatterns("/favicon.ico")
                    .excludePathPatterns("/swagger-resources/**")
                    .excludePathPatterns("/swagger-ui/**")

            ;


    }


}
