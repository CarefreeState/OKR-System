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
 * Date: 2024-01-21
 * Time: 21:48
 */
@ApiModel("初始化第一象限数据")
@Data
public class FirstQuadrantDTO {

    @ApiModelProperty("第一象限 ID")
    private Long id;

    @ApiModelProperty("目标")
    private String objective;

    @ApiModelProperty("截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("-> 第一象限 ID 为 null\n");
        }
        if(!StringUtils.hasText(objective)) {
            messageBuilder.append("-> 没有目标\n");
        }
        if(Objects.isNull(deadline) || deadline.getTime() < System.currentTimeMillis()) {
            messageBuilder.append("-> 截止时间非法\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }
}
