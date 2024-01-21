package com.macaku.core.service;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 1:24
 */

public interface TaskService {

    boolean match(Integer type);

    void addTask(Long quadrantId, String content);

}
