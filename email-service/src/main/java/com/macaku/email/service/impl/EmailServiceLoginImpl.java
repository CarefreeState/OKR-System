package com.macaku.email.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.macaku.common.code.GlobalServiceStatusCode;
import com.macaku.common.exception.GlobalServiceException;
import com.macaku.common.util.thread.pool.CPUThreadPool;
import com.macaku.email.component.EmailSender;
import com.macaku.email.component.model.po.EmailMessage;
import com.macaku.email.html.service.HtmlEngine;
import com.macaku.email.model.vo.VerificationCodeTemplate;
import com.macaku.email.repository.EmailRepository;
import com.macaku.email.service.EmailService;
import com.macaku.email.util.IdentifyingCodeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created With Intellij IDEA
 * Description:
 * User: 马拉圈
 * Date: 2024-03-09
 * Time: 13:52
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceLoginImpl implements EmailService {

    private static final int IDENTIFYING_CODE_MINUTES = 5;//过期分钟数

    private static final TimeUnit IDENTIFYING_CODE_UNIT = TimeUnit.MINUTES;//过期分钟

    private static final int IDENTIFYING_CODE_CD_MINUTES = 1;//CD分钟数

    private static final TimeUnit IDENTIFYING_CODE_CD_UNIT = TimeUnit.MINUTES;//CD分钟

    private static final long IDENTIFYING_CODE_TIMEOUT = IDENTIFYING_CODE_UNIT.toMillis(IDENTIFYING_CODE_MINUTES); //单位为毫秒

    private static final long IDENTIFYING_CODE_INTERVAL_Limit = IDENTIFYING_CODE_CD_UNIT.toMillis(IDENTIFYING_CODE_CD_MINUTES); // 两次发送验证码的最短时间间隔

    private static final int IDENTIFYING_CODE_INTERVAL_LIMIT = 5; // 只有五次验证机会

    private static final String EMAIL_MODEL_HTML = SpringUtil.getProperty("email.template"); // Email 验证码通知 -模板

    private static final String systemEmail = SpringUtil.getProperty("spring.mail.username");

    private final EmailSender emailSender;

    private final EmailRepository emailRepository;

    private final HtmlEngine htmlEngine;

    private boolean canSendEmail(long ttl) {
        return ttl > IDENTIFYING_CODE_TIMEOUT - IDENTIFYING_CODE_INTERVAL_Limit;
    }

    private long getCanSendSeconds(long ttl) {
        return TimeUnit.MILLISECONDS.toSeconds(ttl + IDENTIFYING_CODE_INTERVAL_Limit - IDENTIFYING_CODE_TIMEOUT);
    }

    @Override
    public void sendIdentifyingCode(String email, String code) {
        final String redisKey = IdentifyingCodeValidator.REDIS_EMAIL_IDENTIFYING_CODE + email;
        // 验证一下一分钟以内发过了没有
        long ttl = emailRepository.getTTLOfCode(redisKey); // 小于 0 则代表没有到期时间或者不存在，允许发送
        if(Boolean.TRUE.equals(canSendEmail(ttl))) {
            String message = String.format("请在 %d 秒后再重新申请", getCanSendSeconds(ttl));
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_SEND_FAIL);
        }
        // 封装 Email
        CPUThreadPool.submit(() -> {
            EmailMessage emailMessage = new EmailMessage();
            emailMessage.setContent(code);
            emailMessage.setCreateTime(new Date());
            emailMessage.setTitle(IdentifyingCodeValidator.IDENTIFYING_CODE_PURPOSE);
            emailMessage.setRecipient(email);
            emailMessage.setCarbonCopy();
            emailMessage.setSender(systemEmail);
            // 存到 redis 中
            emailRepository.setIdentifyingCode(redisKey, code, IDENTIFYING_CODE_TIMEOUT, IDENTIFYING_CODE_INTERVAL_LIMIT);
            // 构造模板消息
            VerificationCodeTemplate verificationCodeTemplate = VerificationCodeTemplate.builder()
                    .code(code)
                    .timeout((int) TimeUnit.MILLISECONDS.toMinutes(IDENTIFYING_CODE_TIMEOUT))
                    .build();
            // 发送模板消息
            String html = htmlEngine.builder()
                    .append(EMAIL_MODEL_HTML, verificationCodeTemplate)
                    .build();
            emailSender.sendModelMail(emailMessage, html);
            log.info("发送验证码:{} -> email:{}", code, email);
        });
    }

    @Override
    public void checkIdentifyingCode(String email, String code) {
        String redisKey = IdentifyingCodeValidator.REDIS_EMAIL_IDENTIFYING_CODE + email;
        Map<String, Object> map = emailRepository.getIdentifyingCode(redisKey)
                .map(value -> (Map<String, Object>)value)
                .orElseThrow(() -> {
                    String message = String.format("Redis 中不存在邮箱[%s]的相关记录", email);
                    return new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_NOT_EXIST_RECORD);
                });
        // 取出验证码和过期时间点
        String codeValue = (String) map.get(IdentifyingCodeValidator.IDENTIFYING_CODE);
        int opportunities = (int) map.get(IdentifyingCodeValidator.IDENTIFYING_OPPORTUNITIES);
        // 还有没有验证机会
        if (opportunities < 1) {
            throw new GlobalServiceException(GlobalServiceStatusCode.EMAIL_CODE_OPPORTUNITIES_EXHAUST);
        }
        // 验证是否正确
        if (!codeValue.equals(code)) {
            // 次数减一
            opportunities = (int)emailRepository.decrementOpportunities(redisKey);
            String message = String.format("验证码错误，剩余%d次机会", opportunities);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.EMAIL_CODE_NOT_CONSISTENT);
        }
        // 验证成功
        emailRepository.deleteIdentifyingCodeRecord(redisKey);
    }
}
