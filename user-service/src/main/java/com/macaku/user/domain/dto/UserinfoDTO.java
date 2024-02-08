package com.macaku.user.domain.dto;

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
 * Date: 2024-02-05
 * Time: 18:45
 */
@ApiModel("用户完善信息")
@Data
public class UserinfoDTO {

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String photo;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(nickname)) {
            messageBuilder.append("\n-> 昵称 为 空");
        }
        if(!StringUtils.hasText(photo)) {
            messageBuilder.append("\n-> 头像 为 空");
        }
//        if(!MediaUtil.isImage(photo)) {
//            messageBuilder.append("\n-> 图片 非法");
//        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

}
