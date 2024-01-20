package com.macaku.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-19
 * Time: 20:55
 */
@SpringBootApplication
@MapperScan({"com.macaku.*.mapper"})
@ComponentScan(value = {"com.macaku"})
@EnableSwagger2WebMvc
public class OkrCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OkrCoreApplication.class, args);
    }

}
