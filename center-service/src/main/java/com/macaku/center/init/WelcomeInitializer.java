package com.macaku.center.init;

import com.github.lalyos.jfiglet.FigletFont;
import lombok.SneakyThrows;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-13
 * Time: 23:46
 */
@Component
@Order(0)
public class WelcomeInitializer implements ApplicationListener<ApplicationStartedEvent> {

    private final static String OKR_SYSTEM = "OKR-System";

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println(FigletFont.convertOneLine(OKR_SYSTEM));
    }
}
