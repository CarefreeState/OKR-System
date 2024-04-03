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

    public static String INVITE_PATH;

    public static String BINDING_PATH;

    public static String SWAGGER_PATH;

    public static String LOGIN_PATH;

    public static String PHOTO_PATH;

    public static String COMMON_PATH;

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

    @Value("${media.invite}")
    private void setINVITE_PATH(String invitePath) {
        INVITE_PATH = invitePath;
    }

    @Value("${media.binding}")
    private void setBINDING_PATH(String bindingPath) {
        BINDING_PATH = bindingPath;
    }

    @Value("${media.swagger}")
    private void setSWAGGER_PATH(String swaggerPath) {
        SWAGGER_PATH = swaggerPath;
    }

    @Value("${media.login}")
    private void setLOGIN_PATH(String loginPath) {
        LOGIN_PATH = loginPath;
    }

    @Value("${media.photo}")
    private void setPHOTO_PATH(String photoPath) {
        PHOTO_PATH = photoPath;
    }

    @Value("${media.common}")
    private void setCOMMON_PATH(String commonPath) {
        COMMON_PATH = commonPath;
    }

}
