package com.macaku.core.controller;

import com.macaku.common.response.SystemJsonResponse;
import com.macaku.core.service.OkrCoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-19
 * Time: 23:57
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/test")
public class OkrCoreController {


    private final OkrCoreService okrCoreService;

    @GetMapping("/exception")
    public SystemJsonResponse test() {
        okrCoreService.test();
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

}
