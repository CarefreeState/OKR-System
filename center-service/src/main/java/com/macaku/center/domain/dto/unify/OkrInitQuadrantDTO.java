package com.macaku.center.domain.dto.unify;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.core.domain.po.quadrant.dto.InitQuadrantDTO;
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
 * Time: 23:21
 */
@ApiModel("初始化二三象限所需数据")
@Data
public class OkrInitQuadrantDTO {

    @ApiModelProperty("场景")
    private String scene;

    @ApiModelProperty("初始化象限数据")
    private InitQuadrantDTO initQuadrantDTO;


    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(Objects.isNull(initQuadrantDTO)) {
            messageBuilder.append("\n-> 初始化象限数据 为 null");
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
