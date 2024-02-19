package com.macaku.center.init;

import com.macaku.common.util.media.MediaUtil;
import com.macaku.common.util.media.config.StaticMapperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class OpenAPISwaggerFileInitializer implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${visit.swagger}")
    private Boolean swaggerCanBeVisited;

    @Value("${server.port}")
    private String port;

    @Value("${spring.application.name}")
    private String applicationName;

    private final String suffix = "json";

    private final String swaggerPath = "swagger/";

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        if(Boolean.FALSE.equals(swaggerCanBeVisited)) {
            return;
        }
        String savePath = StaticMapperConfig.ROOT + StaticMapperConfig.MAP_ROOT + swaggerPath;
        String filePath = String.format("%s%s.%s", savePath, applicationName, suffix);
        String url = "http://localhost:" + port + "/v3/api-docs";
        MediaUtil.saveFile(savePath, filePath, url);
    }
}
