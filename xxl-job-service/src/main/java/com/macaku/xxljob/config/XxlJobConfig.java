package com.macaku.xxljob.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobConfig {

    private Admin admin;

    private String accessToken;

    private Executor executor;

    private XxlUrl url;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(admin.getAddresses());
        xxlJobSpringExecutor.setAppname(executor.getAppname());
        xxlJobSpringExecutor.setAddress(executor.getAddress());
        xxlJobSpringExecutor.setIp(executor.getIp());
        xxlJobSpringExecutor.setPort(executor.getPort());
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(executor.getLogpath());
        xxlJobSpringExecutor.setLogRetentionDays(executor.getLogretentiondays());
        return xxlJobSpringExecutor;
    }

    @Bean
    public Admin admin() {
        return admin;
    }

    @Bean
    public Executor executor() {
        return executor;
    }

    @Bean
    public XxlUrl xxlUrl() {
        return url;
    }


    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            while(true) {
//                System.out.println("好耶^v^");
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
        t1.start();
        Thread.sleep(3000);
        t1.interrupt();
        System.out.println("不好耶T.T");
    }
}