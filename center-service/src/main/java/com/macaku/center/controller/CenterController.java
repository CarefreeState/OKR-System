package com.macaku.center.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 18:22
 */
@RestController
@RequestMapping("/center")
public class CenterController {

    @GetMapping("/test")
    public String test() {
        return " test";
    }


}
