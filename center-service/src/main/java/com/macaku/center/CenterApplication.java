package com.macaku.center;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 1:01
 */
@SpringBootApplication
@MapperScan({"com.macaku.center.mapper"})
@ComponentScan(value = {"com.macaku"})
public class CenterApplication {

    public static void main(String[] args) {
        SpringApplication.run(CenterApplication.class, args);
    }

}
