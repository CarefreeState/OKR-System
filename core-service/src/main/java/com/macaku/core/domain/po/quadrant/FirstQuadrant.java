package com.macaku.core.domain.po.quadrant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName first_quadrant
 */
@TableName(value ="first_quadrant")
@Data
public class FirstQuadrant implements Serializable {
    private Long id;

    private Long coreId;

    private String objective;

    private Date deadline;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}