package com.macaku.medal.domain.config;

import com.macaku.medal.domain.po.Medal;
import com.macaku.medal.service.MedalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-08
 * Time: 0:22
 */
@Component
@RequiredArgsConstructor
public class MedalMap {

    private Map<Long, Medal> medalMap = new HashMap<>();

    private final MedalService medalService;

    @PostConstruct
    public void doPostConstruct() {
        medalService.lambdaQuery().list().stream().parallel().forEach(medal -> {
            medalMap.put(medal.getId(), medal);
        });
    }

    public Medal get(Long medalId) {
        return medalMap.get(medalId);
    }
}
