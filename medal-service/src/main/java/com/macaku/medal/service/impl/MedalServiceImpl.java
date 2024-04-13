package com.macaku.medal.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.macaku.medal.domain.po.Medal;
import com.macaku.medal.service.MedalService;
import com.macaku.medal.mapper.MedalMapper;
import org.springframework.stereotype.Service;

/**
* @author 马拉圈
* @description 针对表【medal(勋章表)】的数据库操作Service实现
* @createDate 2024-04-07 11:36:52
*/
@Service
public class MedalServiceImpl extends ServiceImpl<MedalMapper, Medal>
    implements MedalService{

}




