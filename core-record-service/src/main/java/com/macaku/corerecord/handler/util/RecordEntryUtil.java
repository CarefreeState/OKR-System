package com.macaku.corerecord.handler.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class RecordEntryUtil {

    public static <T> Optional<T> getMedalEntry(Object object, Class<T> clazz) {
        return Optional.ofNullable(
                clazz.isInstance(object) ? (T) object : null
        );
    }
}
