package com.macaku.corerecord.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-21
 * Time: 12:42
 */
public interface DayRecordCompleteService {

    boolean match(Integer option);

    Object getEvent(Long coreId, Boolean isCompleted, Boolean oldCompleted);

}
