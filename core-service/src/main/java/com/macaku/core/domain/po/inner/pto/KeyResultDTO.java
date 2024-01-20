package com.macaku.core.domain.po.inner.pto;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-21
 * Time: 2:25
 */
@Data
@ApiModel(description = "关键结果")
public class KeyResultDTO {

    @ApiModelProperty("第一象限 ID")
    private Long firstQuadrantId;

    @ApiModelProperty("关键结果内容")
    private String content;

    @ApiModelProperty("完成概率")
    private Integer probability;

    public void validate() {
        StringBuilder stringBuilder = new StringBuilder();
        if(Objects.isNull(firstQuadrantId)) {
            stringBuilder.append("--> 第一象限 ID 为 null\n");
        }
        if(!StringUtils.hasText(content)) {
            stringBuilder.append("--> 关键结果没有内容\n");
        }
        if(Objects.isNull(probability) ||
                probability.compareTo(0) < 0 || probability.compareTo(100) > 0) {
            stringBuilder.append("--> 完成概率非法");
        }
        String message = stringBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
