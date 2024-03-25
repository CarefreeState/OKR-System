package com.macaku.user.filter;

import com.macaku.common.util.JwtUtil;
import com.macaku.user.interceptor.config.VisitConfig;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*"); // 可以设置允许访问的域，也可以是具体的域名
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                String.format("Content-Type, Accept, X-Requested-With, %s, %s", JwtUtil.JWT_HEADER, VisitConfig.HEADER));
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 可以在这里进行一些初始化操作
    }

    @Override
    public void destroy() {
        // 可以在这里进行一些清理操作
    }
}