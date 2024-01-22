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
@Data
@ApiModel(description = "第二象限实体")
public class InitQuadrantDTO {

    @ApiModelProperty("第二象限 ID")
    private Long id;

    @ApiModelProperty("截止时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    @ApiModelProperty("象限周期")
    private Integer quadrantCycle;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("-> 象限 ID 为 null\n");
        }
        if(Objects.isNull(deadline) || deadline.getTime() < System.currentTimeMillis()) {
            messageBuilder.append("-> 截止时间非法\n");
        }
        if(Objects.isNull(quadrantCycle)) {
            messageBuilder.append("-> 周期为空\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
