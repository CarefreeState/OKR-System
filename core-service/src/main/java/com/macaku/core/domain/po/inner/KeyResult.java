package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName key_result
 */
@TableName(value ="key_result")
@Data
public class KeyResult implements Serializable {
    private Long id;

    private Long firstQuadrantId;

    private String content;

    private Integer probability;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}