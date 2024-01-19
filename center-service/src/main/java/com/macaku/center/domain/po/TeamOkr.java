package com.macaku.center.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName team_okr
 */
@TableName(value ="team_okr")
@Data
public class TeamOkr implements Serializable {
    private Long id;

    private Long coreId;

    private Long parentTeamId;

    private Long managerId;

    private String teamName;

    private Integer version;

    private Boolean isDeleted;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;
}