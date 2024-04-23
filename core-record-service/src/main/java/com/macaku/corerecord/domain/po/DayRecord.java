package com.macaku.corerecord.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.macaku.corerecord.domain.po.ext.Record;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName day_record
 */
@TableName(value ="day_record")
@Data
public class DayRecord extends Record implements Serializable {
    private Long id;

    private Long coreId;

    @TableField(jdbcType = JdbcType.DATE)
    private Date recordDate;

    @TableField(jdbcType = JdbcType.DECIMAL)
    private Double credit1;

    private Integer credit2;

    private Integer credit3;

    private Integer credit4;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}