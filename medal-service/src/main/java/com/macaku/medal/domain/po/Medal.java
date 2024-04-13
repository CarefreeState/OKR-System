package com.macaku.medal.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName medal
 */
@TableName(value ="medal")
@Data
public class Medal implements Serializable {
    private Long id;

    private String name;

    private String description;

    private String url;

    private String greyUrl;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}