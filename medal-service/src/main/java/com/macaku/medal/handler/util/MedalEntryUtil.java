package com.macaku.medal.handler.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:39
 */
@Slf4j
public class MedalEntryUtil {

    public static <T> Optional<T> getMedalEntry(Object object, Class<T> clazz) {
        return Optional.ofNullable(
                clazz.isInstance(object) ? (T) object : null
        );
    }

    public static double log2(double base) {
        return Math.log(base) / Math.log(2);
    }

    public static Integer getLevel(Long credit, Integer coefficient) {
        double base = credit * 1.0 / coefficient;
        base = base < 1 ? 0.5 : base;
        int level = coefficient == 0 ? Integer.MAX_VALUE : (int) log2(base) + 1;
        log.info("积分 {} 基数 {} 计算等级 -> {}", credit, coefficient, level);
        return level;
    }
}
