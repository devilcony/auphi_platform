package com.aofei.base.exception;

/**
 * Created by Hao on 2017-03-23.
 */
public enum StatusCode {

    /*
        错误码格式说明（示例：202001）
        --------------------------------------------------------------------
        服务级错误（1为系统级错误）	服务模块代码(即业务模块标识)	具体错误代码
                2                            02	                    001
        --------------------------------------------------------------------
    */

    /*常用的状态码，业务模块的状态码请勿添加在此处，而应该是在各个业务模块定义，格式如上*/
    OK(200, "ok"),//成功
    SERVER_ERROR(500, "internal server error"),//服务器内部错误
    BAD_REQUEST(400, "bad request"),//请求格式错误、参数错误
    UNAUTHORIZED(401, "unauthorized"),//未授权
    NOT_FOUND(404, "not found"),//请求的资源不存在、数据不存在
    CONFLICT(409, "conflict"),//资源存在冲突、数据已存在
    MAXIMUM_UPLOAD(511, "Maximum upload size of 1G exceeded"),//上传文件超过规定大小
    DATA_INTEGRITY_VIOLATION_EXCEPTION(424, "data integrity violation exception");//违背数据完整性、当插入、删除和修改数据的时候，违背的数据完整性约束抛出的异常。例如：主键重复异常、存在外键关联数据依赖等

    /*状态码*/
    private int code;
    /*信息*/
    private String message;

    StatusCode(int code, String message) {
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
