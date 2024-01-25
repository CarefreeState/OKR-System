package com.macaku.center.controller;

import com.macaku.center.service.TeamOkrService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CenterController {

    private final TeamOkrService teamOkrService;

}
