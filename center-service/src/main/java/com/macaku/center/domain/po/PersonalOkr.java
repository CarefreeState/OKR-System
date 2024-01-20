package com.macaku.center.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName personal_okr
 */
@TableName(value ="personal_okr")
@Data
public class PersonalOkr implements Serializable {

    private Long id;

    private Long coreId;

    private Long userId;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}