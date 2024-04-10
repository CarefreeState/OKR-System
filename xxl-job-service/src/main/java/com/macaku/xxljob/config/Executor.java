package com.macaku.xxljob.config;

import lombok.Data;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-10
 * Time: 11:14
 */
@Data
public class Executor {

    private String title;

    private String appname;

    private String address;

    private String ip;

    private Integer port;

    private String logpath;

    private Integer logretentiondays;

    private String addressType;

    private String addressList;

}
