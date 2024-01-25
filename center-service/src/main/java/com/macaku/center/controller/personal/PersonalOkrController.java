package com.macaku.center.controller.personal;

import com.macaku.center.domain.vo.PersonalOkrVO;
import com.macaku.center.service.PersonalOkrService;
import com.macaku.common.response.SystemJsonResponse;
import com.macaku.user.domain.po.User;
import com.macaku.user.util.UserRecordUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-01-25
 * Time: 21:35
 */
@RestController
@Api(tags = "个人 OKR 测试接口")
@RequestMapping("/personal")
@RequiredArgsConstructor
public class PersonalOkrController {

    private final PersonalOkrService personalOkrService;

    @GetMapping("/list")
    @ApiOperation("获取个人 OKR 列表")
    public SystemJsonResponse<List<PersonalOkrVO>> getPersonalOkrs(HttpServletRequest request) {
        // 获取当前登录的用户
        User user = UserRecordUtil.getUserRecord(request);
        // 调用方法
        List<PersonalOkrVO> personalOkrVOS = personalOkrService.getPersonalOkrList(user);
        return SystemJsonResponse.SYSTEM_SUCCESS(personalOkrVOS);
    }


}
