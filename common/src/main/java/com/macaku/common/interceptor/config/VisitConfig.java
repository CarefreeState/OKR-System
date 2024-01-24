package com.macaku.common.interceptor.config;

import com.macaku.common.interceptor.EmailUserInterceptor;
import com.macaku.common.interceptor.WxUserInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class VisitConfig implements WebMvcConfigurer {

    public static final String HEADER = "Type";

    private final WxUserInterceptor wxUserInterceptor;//自定义的登录拦截器

    private final EmailUserInterceptor emailUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(userInterceptor)
//                .addPathPatterns("/**")
//                //不拦截路由
//                .excludePathPatterns("/doc.html/**")
//                .excludePathPatterns("/v3/api-docs/**")
//                .excludePathPatterns("/webjars/**")
//                .excludePathPatterns("/error")
//                .excludePathPatterns("/favicon.ico")
//                .excludePathPatterns("/swagger-resources/**")
            registry.addInterceptor(emailUserInterceptor)
                    .addPathPatterns("/**")
                    // 不拦截的路径
                    .excludePathPatterns("/user/login")
                    .excludePathPatterns("/doc.html/**")
                    .excludePathPatterns("/v3/api-docs/**")
                    .excludePathPatterns("/webjars/**")
                    .excludePathPatterns("/error")
                    .excludePathPatterns("/favicon.ico")
                    .excludePathPatterns("/swagger-resources/**")
                    .excludePathPatterns("/swagger-ui/**")
//                    .addPathPatterns("")
            ;


    }


}
