package com.macaku.medal.domain.config;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.medal.domain.po.Medal;
import com.macaku.medal.domain.vo.UserMedalVO;
import com.macaku.medal.service.MedalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-12
 * Time: 18:08
 */
@Component
@RequiredArgsConstructor
public class MedalList {

    private final List<Medal> medalList = new ArrayList<>();

    private final MedalService medalService;

    @PostConstruct
    public void doPostConstruct() {
        medalService.lambdaQuery().list().stream().forEach(medalList::add);
    }

    public Medal get(int index) {
        return medalList.get(index);
    }

    public List<UserMedalVO> getGrepList() {
        // clone 的一份
        return medalList.stream().map(medal -> {
            UserMedalVO userMedalVO = BeanUtil.copyProperties(medal, UserMedalVO.class);
            userMedalVO.setMedalId(medal.getId());
            userMedalVO.setUrl(medal.getGreyUrl());
            return userMedalVO;
        }).sorted(Comparator.comparing(UserMedalVO::getMedalId)).collect(Collectors.toList());
    }

}
