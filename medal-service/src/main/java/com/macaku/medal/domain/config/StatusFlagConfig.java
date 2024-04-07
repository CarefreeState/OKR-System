package com.macaku.medal.domain.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 13:49
 */
@Setter
@Configuration
@ConfigurationProperties(prefix = "medal.status-flag")
public class StatusFlagConfig {

    private List<StatusFlagProperty> statusFlagProperties;

    private Double threshold;

    private final Map<String, Long> colorCreditMap = new HashMap<>();

    @PostConstruct
    public void doPostConstruct() {
        statusFlagProperties.stream().parallel().forEach(statusFlagProperty -> {
            colorCreditMap.put(statusFlagProperty.getColor(), statusFlagProperty.getCredit());
        });
    }

    public Long getCredit(String color) {
        return colorCreditMap.get(color);
    }

    public boolean isTouch(double average) {
        return average >= threshold;
    }
}
