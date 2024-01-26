package com.macaku.center.domain.dto.unify;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.dto.FirstQuadrantDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-26
 * Time: 23:05
 */
@ApiModel("第一象限修改所需数据")
@Data
public class OkrFirstQuadrantDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("第一象限 数据")
    private FirstQuadrantDTO firstQuadrantDTO;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(firstQuadrantDTO)) {
            messageBuilder.append("\n-> 第一象限 为 null");
        }
        if(!StringUtils.hasText(scene)) {
            messageBuilder.append("\n-> 缺少场景值");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
