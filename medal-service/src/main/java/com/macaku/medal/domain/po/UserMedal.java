package com.macaku.medal.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName user_medal
 */
@TableName(value ="user_medal")
@Data
public class UserMedal implements Serializable {
    private Long userId;

    private Long medalId;

    private Long credit;

    private Integer level;

    private Boolean isRead;

    private Date issueTime;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}