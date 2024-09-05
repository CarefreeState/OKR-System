package com.macaku.common.locator;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-09-05
 * Time: 12:29
 */
public interface ServiceFactory <T> {

    T getService(String name);
}
