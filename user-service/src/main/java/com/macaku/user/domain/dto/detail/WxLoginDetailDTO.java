package com.macaku.user.domain.dto.detail;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.codec.Base64;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.convert.JsonUtil;
import com.macaku.user.domain.po.User;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;
import java.util.Map;

@ApiModel("微信小程序登录数据")
@Data
public class WxLoginDetailDTO {

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("encryptedData")
    private String encryptedData;

    @ApiModelProperty("iv")
    private String iv;

    @ApiModelProperty("rawData")
    private String rawData;

    @ApiModelProperty("signature")
    private String signature;

    public void validate() {
        StringBuilder messageBuilder = new StringBuilder();
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> code 为 空");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> encryptedData 为 空");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> iv 为 空");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> rawData 为 空");
        }
        if(!StringUtils.hasText(code)) {
            messageBuilder.append("\n-> signature 为 空");
        }
        String message = messageBuilder.toString();
        if(StringUtils.hasLength(message)) {
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE);
        }
    }

    public User transToUser() {
        User user = new User();
        Map<String, Object> data = JsonUtil.analyzeJson(this.rawData, Map.class);
        user.setNickname((String) data.get("nickname"));
        user.setPhoto((String) data.get("avatarUrl"));
        return user;
    }

    public static WxLoginDetailDTO create(Map<?, ?> data) {
        return BeanUtil.mapToBean(data, WxLoginDetailDTO.class, false, new CopyOptions());
    }

    public String getUserInfoByEncryptedData(String sessionKey){
        // 被加密的数据
        byte[] dataByte = Base64.decode(this.encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(this.iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + 1;
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                return new String(resultByte, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            throw new GlobalServiceException(e.getMessage());
        }
        return null;
    }
}
