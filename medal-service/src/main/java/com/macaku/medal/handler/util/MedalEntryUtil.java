package com.macaku.medal.handler.util;

import java.util.Optional;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-07
 * Time: 12:39
 */
public class MedalEntryUtil {

    public static <T> Optional<T> getMedalEntry(Object object, Class<T> clazz) {
        return Optional.ofNullable(
                clazz.isInstance(object) ? (T) object : null
        );
    }

    public static Integer getLevel(Long credit, Integer coefficient) {
        return coefficient == 0 ? Integer.MAX_VALUE : (int) Math.log(credit * 1.0 / coefficient) + 1;
    }
}
