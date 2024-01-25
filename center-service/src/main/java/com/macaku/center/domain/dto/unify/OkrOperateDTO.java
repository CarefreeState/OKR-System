package com.macaku.center.domain.dto.unify;

import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 19:18
 */
@ApiModel("OKR 操作数据")
@Data
public class OkrOperateDTO {

    @ApiModelProperty("作用领域")
    private String scope;

    @ApiModelProperty("团队 OKR ID")
    private Long teamOkrId;

    @ApiModelProperty("团队个人 OKR ID")
    private Long teamPersonalOkrId;

    public void validate() {
        if(!StringUtils.hasText(scope)) {
            throw new GlobalServiceException(GlobalServiceStatusCode.PARAM_IS_BLANK);
        }
    }

}
