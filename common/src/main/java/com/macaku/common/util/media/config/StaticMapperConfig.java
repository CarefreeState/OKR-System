package com.macaku.common.util.media.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticMapperConfig implements WebMvcConfigurer {

    public static String MAP_ROOT;

    public static String ROOT;

    public static String STATIC_PATH;

    /**
     * 配置静态访问资源
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + MAP_ROOT + "**")
                .addResourceLocations("file:" + ROOT + MAP_ROOT);
    }

    @Value("${media.map}")
    private void setMAP_ROOT(String mapRoot) {
        MAP_ROOT = mapRoot;
    }

    @Value("${media.root}")
    private void setROOT(String root) {
        ROOT = root;
    }

    @Value("${media.static}")
    private void setSTATIC_PATH(String staticPath) {
        STATIC_PATH = staticPath;
    }

}
