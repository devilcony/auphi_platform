package com.aofei.kettle.exception;

/**
 * @auther Tony
 * @create 2018-10-08 14:07
 */
public enum KettleError {
    /*
         错误码格式说明（示例：202001），1为系统级错误，2为业务逻辑错误
         --------------------------------------------------------------------
         服务级错误（1为系统级错误）	服务模块代码(即业务模块标识)	具体错误代码
                 2                            02	                    001
         --------------------------------------------------------------------
     */
    //2 00 001 释义：  00 = System 业务模块标识，001为具体的错误代码
    CHECK_DATABASE_FAILURE(200009, "数据库连接验证失败"),
    DATABASE_EXIST(200010, "数据量连接已存在")
    ;

    /*状态码*/
    private int code;
    /*信息*/
    private String message;

    KettleError(int code, String message) {
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
