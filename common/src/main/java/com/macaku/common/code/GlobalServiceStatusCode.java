package com.macaku.common.code;

import lombok.Getter;

@Getter
public enum GlobalServiceStatusCode {
    /* 成功, 默认200 */
    SYSTEM_SUCCESS(200, "操作成功"),

    /* 需要重定向 */
    NEED_REDIRECT(302, "需要重定向"),

    /* 系统错误 500 - 1000 */
    SYSTEM_SERVICE_FAIL(-4396, "操作失败"),
    SYSTEM_SERVICE_ERROR(-500, "系统异常"),
    SYSTEM_TIME_OUT(-1, "请求超时"),


    /* 参数错误：1001～2000 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),
    PARAM_FAILED_VALIDATE(1005, "参数未通过验证"),

    HEAD_NOT_VALID(1101, "请求头无效"),

    /* 用户错误 2001-3000 */
    USER_NOT_LOGIN(2001, "用户未登录"),
    USER_ACCOUNT_EXPIRED(2002, "账号已过期"),
    USER_CREDENTIALS_ERROR(2003, "密码错误"),
    USER_CREDENTIALS_EXPIRED(2004, "密码过期"),
    USER_ACCOUNT_DISABLE(2005, "账号不可用"),
    USER_ACCOUNT_LOCKED(2006, "账号被锁定"),
    USER_ACCOUNT_NOT_EXIST(2007, "账号不存在"),
    USER_ACCOUNT_ALREADY_EXIST(2008, "账号已存在"),
    USER_ACCOUNT_USE_BY_OTHERS(2009, "账号下线"),

    USER_NO_PERMISSION(2403, "用户无权限"),
    USER_NO_PHONE_CODE(2500, "验证码错误"),

    USER_CANNOT_JOIN_TEAM(2600, "用户可能并非受邀，无法加入团队"),

    DATA_NOT_SECURITY(3000, "数据不安全"),



    // -------- 象限相关：
    FIRST_QUADRANT_UPDATE_ERROR(4001, "第一象限更新失败"),
    SECOND_QUADRANT_UPDATE_ERROR(4002, "第二象限更新失败"),
    THIRD_QUADRANT_UPDATE_ERROR(4003, "第三象限更新失败"),
    FOURTH_QUADRANT_UPDATE_ERROR(4004, "第四象限更新失败"),
    FIRST_QUADRANT_NOT_EXISTS(4002, "第一象限不存在"),
    SECOND_QUADRANT_NOT_EXISTS(4003, "第二象限不存在"),
    THIRD_QUADRANT_NOT_EXISTS(4004, "第三象限不存在"),
    FOURTH_QUADRANT_NOT_EXISTS(4005, "第四象限不存在"),

    SECOND_FIRST_QUADRANT_NOT_INIT(4100, "第二象限未初始化"),

    OKR_IS_OVER(4100, "OKR 已结束"),
    OKR_IS_NOT_OVER(4101, "OKR 未结束"),

    INVALID_CELEBRATE_DAY(4200, "庆祝日非法更新"),
    CELEBRATE_DAY_CANNOT_CHANGE(4201, "庆祝日不能修改"),

    /* 团队相关 */
    TEAM_NOT_EXISTS(5000, "团队不存在"),
    TEAM_CREATE_TOO_FREQUENT(5001, "团队 OKR 创建太频繁了"),
    REPEATED_JOIN_TEAM(5002, "重复加入团队"),
    NON_TEAM_MEMBER(5003, "非团队人员"),
    NON_TEAM_MANAGER(5004, "非团队管理者"),
    REPEATED_GRANT(5005, "重复授权"),

    /*内核相关*/
    CORE_NOT_EXISTS(6000, "OKR 内核不存在或者并将其当作内核的 p/t/tp OKR "),
    USER_NOT_CORE_MANAGER(6001, "用户并不是 OKR 的管理者"),

    KEY_RESULT_NOT_EXISTS(6100, "关键结果不存在"),
    STATUS_FLAG_NOT_EXISTS(6101, "状态指标不存在"),
    TASK_NOT_EXISTS(6102, "任务不存在"),

    SECOND_CYCLE_TOO_SHORT(6200, "第二象限周期太短了"),
    THIRD_CYCLE_TOO_SHORT(6201, "第三象限周期太短了"),











    /* -------------- */;

    private final Integer code;
    private final String message;

    GlobalServiceStatusCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据code获取message
     *
     * @param code 状态码的code
     * @return 状态码的状态信息
     */
    public static String GetStatusMsgByCode(Integer code) {
        for (GlobalServiceStatusCode ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
