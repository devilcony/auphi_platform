package com.aofei.sys.exception;

/**
 * Created by Hao on 2017-03-23.
 */
public enum SystemError {

    /*
        错误码格式说明（示例：202001），1为系统级错误，2为业务逻辑错误
        --------------------------------------------------------------------
        服务级错误（1为系统级错误）	服务模块代码(即业务模块标识)	具体错误代码
                2                            02	                    001
        --------------------------------------------------------------------
    */
    //2 00 001 释义：  00 = System 业务模块标识，001为具体的错误代码
    NOT_LOGIN(200000, "not login"),//用户名或密码错误
    LOGIN_FAILED(200001, "invalid username or password"),//用户名或密码错误
    CAPTCHA_ERROR(200002, "captcha error"),//验证码错误
    DISABLED_ACCOUNT(200003, "Account disabled"),//账号被禁用
    ORIGINAL_PASSWORD_ERROR(200004, "invalid original password error"),//原始密码错误
    PHONE_NUMBER_EXIST(200005, "the phone number is exist"),//手机号被占用
    PERMISSION_DENIED(200006, "Permission Denied"),
    NATIONCODE_EMPTY(200007, "Permission Denied"),
    STATUS_DISABLED(200008, "user was disabled"),
    CHECK_DATABASE_FAILURE(200009, "数据库连接验证失败"),
    DATABASE_EXIST(200010, "数据量连接已存在")
    ;

    /*状态码*/
    private int code;
    /*信息*/
    private String message;

    SystemError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}

