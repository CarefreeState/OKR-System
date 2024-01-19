package com.macaku.core.domain.po.quadrant;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName third_quadrant
 */
@TableName(value ="third_quadrant")
@Data
public class ThirdQuadrant implements Serializable {
    private Long id;

    private Long coreId;

    private Date deadline;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}