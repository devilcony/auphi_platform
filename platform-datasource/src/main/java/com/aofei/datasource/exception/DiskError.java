package com.aofei.datasource.exception;

public enum DiskError {

    /*
        错误码格式说明（示例：202001），1为系统级错误，2为业务逻辑错误
        --------------------------------------------------------------------
        服务级错误（1为系统级错误）	服务模块代码(即业务模块标识)	具体错误代码
                2                            02	                    001
        --------------------------------------------------------------------
    */
    //2 00 001 释义：  00 = System 业务模块标识，001为具体的错误代码
    DIR_EXISTS(300000, "dir exists")//文件夹已存在

    ;

    /*状态码*/
    private int code;
    /*信息*/
    private String message;

    DiskError(int code, String message) {
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
