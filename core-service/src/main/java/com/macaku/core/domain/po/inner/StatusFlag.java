package com.macaku.core.domain.po.inner;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName status_flag
 */
@TableName(value ="status_flag")
@Data
public class StatusFlag implements Serializable {
    private Long id;

    private Long fourthQuadrantId;

    private String label;

    private String color;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}