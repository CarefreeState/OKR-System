package com.macaku.core.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName okr_core
 */
@TableName(value ="okr_core")
@Data
public class OkrCore implements Serializable {
    private Long id;

    private Integer celebrateDay;

    private Integer secondQuadrantCycle;

    private Integer thirdQuadrantCycle;

    private Boolean isOver;

    private String summary;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}