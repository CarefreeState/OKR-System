package com.macaku.center.controller.user;

import cn.hutool.core.bean.BeanUtil;
import com.macaku.center.domain.vo.LoginQRCodeVO;
import com.macaku.center.service.WxQRCodeService;
import com.macaku.common.email.component.EmailServiceSelector;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.common.util.IdentifyingCodeValidator;
import com.macaku.user.component.LoginServiceSelector;
import com.macaku.user.domain.dto.EmailBindingDTO;
import com.macaku.user.domain.dto.EmailCheckDTO;
import com.macaku.user.domain.dto.UserinfoDTO;
import com.macaku.user.domain.dto.WxBindingDTO;
import com.macaku.user.domain.dto.unify.LoginDTO;
import com.macaku.user.domain.po.User;
import com.macaku.user.domain.vo.UserVO;
import com.macaku.user.interceptor.config.VisitConfig;
import com.macaku.user.service.LoginService;
import com.macaku.user.service.UserService;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-20
 * Time: 0:07
 */
@RestController
@Api(tags = "用户测试接口")
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final LoginServiceSelector loginServiceSelector;

    private final UserService userService;

    private final EmailServiceSelector emailServiceSelector;

    private final WxQRCodeService wxQRCodeService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public SystemJsonResponse<Map<String, Object>> login(HttpServletRequest request,
                                                         @RequestBody LoginDTO loginDTO) {
        String type = request.getHeader(VisitConfig.HEADER);
        // 检查
        loginDTO.validate();
        // 选取服务
        LoginService loginService = loginServiceSelector.select(type);
        Map<String, Object> result = loginService.login(loginDTO);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/check/email")
    @ApiOperation("验证邮箱用户")
    public SystemJsonResponse emailIdentityCheck(@RequestBody EmailCheckDTO emailCheckDTO) {
        emailCheckDTO.validate();
        // 获得随机验证码
        String code = IdentifyingCodeValidator.getIdentifyingCode();
        String type = emailCheckDTO.getType();
        String email = emailCheckDTO.getEmail();
        emailServiceSelector.
                select(type).
                sendIdentifyingCode(email, code);
        // 能到这一步就成功了
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/check/wx")
    @ApiOperation("验证微信用户")
    public SystemJsonResponse<String> wxIdentifyCheck() {
        Long userId = UserRecordUtil.getUserRecord().getId();
        String randomCode = IdentifyingCodeValidator.getIdentifyingCode();
        // 生成一个小程序检查码
        String mapPath = wxQRCodeService.getBindingQRCode(userId, randomCode);
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @GetMapping("/wx/login")
    @ApiOperation("获取微信登录码")
    public SystemJsonResponse<LoginQRCodeVO> wxLoginCheck() {
        // 生成一个小程序检查码
        LoginQRCodeVO result = wxQRCodeService.getLoginQRCode();
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/wx/confirm/{secret}")
    @ApiOperation("微信登录确认")
    public SystemJsonResponse wxLoginConfirm(@PathVariable("secret") @NonNull @ApiParam("secret") String secret) {
        User user = UserRecordUtil.getUserRecord();
        String openid = user.getOpenid();
        String unionid = user.getUnionid();
        userService.onLoginState(secret, openid, unionid);//如果不是微信用户，token 的数据没意义，对不上
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/wx/login/{secret}")
    @ApiOperation("微信登录检查")
    public SystemJsonResponse<Map<String, Object>> wxLoginCheck(@PathVariable("secret") @NonNull @ApiParam("secret") String secret) {
        Map<String, Object> result = userService.checkLoginState(secret);
        return SystemJsonResponse.SYSTEM_SUCCESS(result);
    }

    @PostMapping("/binding/email")
    @ApiOperation("绑定用户邮箱")
    public SystemJsonResponse emailBinding(HttpServletRequest request,
                                           @RequestBody EmailBindingDTO emailBindingDTO) {
        emailBindingDTO.validate();
        String email = emailBindingDTO.getEmail();
        String code = emailBindingDTO.getCode();
        // 获取当前登录的用户
        // todo: 考察是否要限制绑定次数，或者是否可以重新绑定，当前不做限制
        User userRecord = UserRecordUtil.getUserRecord();
        Long userId = userRecord.getId();
        String recordEmail = userRecord.getEmail();
        userService.bindingEmail(userId, email, code, recordEmail);
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/binding/wx")
    @ApiOperation("绑定用户微信")
    public SystemJsonResponse wxBinding(HttpServletRequest request,
                                           @RequestBody WxBindingDTO wxBindingDTO) {
        wxBindingDTO.validate();
        Long userId = wxBindingDTO.getUserId();
        String randomCode = wxBindingDTO.getRandomCode();
        String code = wxBindingDTO.getCode();
        userService.bindingWx(userId, randomCode, code);
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @PostMapping("/photo/upload")
    @ApiOperation("上传用户头像")
    public SystemJsonResponse<String> uploadPhoto(HttpServletRequest request,
            @ApiParam("用户头像（只能上传图片，最大 1MB）") @NonNull @RequestPart("photo") MultipartFile multipartFile) throws IOException {
        byte[] photoData = multipartFile.getBytes();
        User user = UserRecordUtil.getUserRecord();
        Long userId = user.getId();
        String originPhoto = user.getPhoto();
        String mapPath = userService.tryUploadPhoto(photoData, userId, originPhoto);
        // 删除记录
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS(mapPath);
    }

    @PostMapping("/improve")
    @ApiOperation("完善用户信息")
    public SystemJsonResponse improveUserinfo(HttpServletRequest request,
                                              @RequestBody UserinfoDTO userinfoDTO) {
        // 获取当前用户 ID
        Long userId = UserRecordUtil.getUserRecord().getId();
        // 完善信息
        userService.improveUserinfo(userinfoDTO, userId);
        // 删除记录
        UserRecordUtil.deleteUserRecord(request);
        return SystemJsonResponse.SYSTEM_SUCCESS();
    }

    @GetMapping("/userinfo")
    @ApiOperation("获取用户信息")
    public SystemJsonResponse<UserVO> getUserInfo() {
        // 获取当前登录用户
        User user = UserRecordUtil.getUserRecord();
        // 提取信息
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        // 返回
        return SystemJsonResponse.SYSTEM_SUCCESS(userVO);
    }
}
