package com.macaku.center.interceptor;

import com.macaku.common.util.thread.local.ThreadLocalUtil;
import com.macaku.common.util.thread.pool.IOThreadPool;
import com.macaku.medal.domain.entry.StayTrueBeginning;
import com.macaku.medal.domain.po.UserMedal;
import com.macaku.medal.handler.chain.MedalHandlerChain;
import com.macaku.medal.service.UserMedalService;
import com.macaku.user.util.UserRecordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-04-14
 * Time: 0:45
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuadrantInitialInterceptor implements HandlerInterceptor {

    @Value("${medal.stay-true-beginning.id}")
    private Long medalId;

    private final UserMedalService userMedalService;

    private final MedalHandlerChain medalHandlerChain;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 初心启航
        // 启动一个异步线程
        Long userId = UserRecordUtil.getUserRecord().getId();
        Long coreId = ThreadLocalUtil.get(Long::parseLong);
        IOThreadPool.submit(() -> {
            UserMedal dbUserMedal = userMedalService.getUserMedal(userId, medalId);
            if(Objects.isNull(dbUserMedal)) {
                StayTrueBeginning stayTrueBeginning = StayTrueBeginning.builder().userId(userId).coreId(coreId).build();
                medalHandlerChain.handle(stayTrueBeginning);
            }
        });
    }
}
