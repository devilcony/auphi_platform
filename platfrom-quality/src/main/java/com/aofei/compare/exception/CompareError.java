package com.aofei.compare.exception;

/**
 * ${DESCRIPTION}
 *
 * @auther Tony
 * @create 2018-10-19 20:15
 */
public enum CompareError {

    /*
        错误码格式说明（示例：202001），1为系统级错误，2为业务逻辑错误
        --------------------------------------------------------------------
        服务级错误（1为系统级错误）	服务模块代码(即业务模块标识)	具体错误代码
                2                            10	                    001
        --------------------------------------------------------------------
    */
    //2 00 001 释义：  00 = System 业务模块标识，001为具体的错误代码
    TYPE_INCONSISTENCY(210001, "字段类型不一致"),//用户名或密码错误
    QUANTITY_DISCREPANCY(210002, "字段数量不一致"),//用户名或密码错误

    ;

    /*状态码*/
    private int code;
    /*信息*/
    private String message;

    CompareError(int code, String message) {
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
