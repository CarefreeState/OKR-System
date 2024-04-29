package com.macaku.user.security.config;

import com.macaku.user.security.handler.AuthFailHandler;
import com.macaku.user.security.filter.JwtAuthenticationTokenFilter;
import com.macaku.user.interceptor.config.VisitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 马拉圈
 * Date: 2024-01-11
 * Time: 20:29
 */
@Configuration
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)//开启权限控制，不开启这个，注解的权限控制不能生效
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public final static String USER_SECURITY_RECORD = "userSecurityRecord";

    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    private final AuthFailHandler authFailHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        //关闭 csrf
            .csrf().disable()
        //不通过 Session 获取 SecurityContext
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/user/login").permitAll()
            .antMatchers("/user/check/email").permitAll()
            .antMatchers("/user/binding/wx").permitAll()
            .antMatchers("/web/wxlogin/**").permitAll()
            .antMatchers("/events/web/wxlogin/**").permitAll()
            .antMatchers("/user/wx/login/**").permitAll()
            .antMatchers("/team/describe/**").permitAll()
            .antMatchers("/jwt/**").permitAll()
            .antMatchers("/media/**").permitAll()
            .antMatchers(AuthFailHandler.REDIRECT_URL).permitAll()
            .antMatchers(VisitConfig.swaggers).permitAll()
            .anyRequest().authenticated();
        // 添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        // 配置异常处理器（默认的话貌似是抛对应的异常，一大串的东西、或者控制台无表示，用对应的响应状态码表示异常）
        http.exceptionHandling()
                .authenticationEntryPoint(authFailHandler);
        // 处理跨域
//        http.cors();
    }
}
