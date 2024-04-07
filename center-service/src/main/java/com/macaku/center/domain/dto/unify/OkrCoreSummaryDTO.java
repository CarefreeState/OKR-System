package com.macaku.center.domain.dto.unify;

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
 * Date: 2024-01-27
 * Time: 1:47
 */
@ApiModel("总结 OKR 所需数据")
@Data
public class OkrCoreSummaryDTO {

    private final static Integer MAX_DEGREE = 300;

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("内核 ID")
    private Long coreId;

    @ApiModelProperty("总结的内容")
    private String summary;

    @ApiModelProperty("完成度")
    private Integer degree;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(coreId)) {
            messageBuilder.append("\n-> 内核 ID 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        if(!StringUtils.hasText(summary)) {
            messageBuilder.append("\n-> 总结没有内容");
        }
        if(Objects.isNull(degree) || degree.compareTo(0) < 0 || degree.compareTo(MAX_DEGREE) > 0) {
            messageBuilder.append("\n-> 完成度非法");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
