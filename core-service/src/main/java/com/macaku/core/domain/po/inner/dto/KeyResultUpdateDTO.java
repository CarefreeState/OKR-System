package com.macaku.core.domain.po.inner.dto;

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
 * Time: 22:54
 */
@ApiModel("关键结果更新数据")
@Data
public class KeyResultUpdateDTO {

    @ApiModelProperty("关键结果 ID")
    private Long id;

    @ApiModelProperty("完成概率")
    private Integer probability;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(id)) {
            messageBuilder.append("-> 关键结果 ID 为 null\n");
        }
        if(Objects.isNull(probability) ||
                probability.compareTo(0) < 0 || probability.compareTo(100) > 0) {
            messageBuilder.append("-> 完成概率非法\n");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
