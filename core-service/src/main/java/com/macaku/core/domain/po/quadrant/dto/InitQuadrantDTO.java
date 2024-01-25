package com.macaku.core.domain.po.quadrant.dto;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-22
 * Time: 20:07
 */
@ApiModel("初始化第二第三象限数据")
@Data
public class InitQuadrantDTO {

    @ApiModelProperty("象限 ID")
    private Long id;

    @ApiModelProperty("截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    @ApiModelProperty("象限周期")
    private Integer quadrantCycle;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("\n-> 象限 ID 为 null");
        }
        if(Objects.isNull(deadline) || deadline.getTime() < System.currentTimeMillis()) {
            messageBuilder.append("\n-> 截止时间非法");
        }
        if(Objects.isNull(quadrantCycle) || quadrantCycle.compareTo(0) <= 0) {
            messageBuilder.append("\n-> 周期非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
