package com.aofei.base.model.response;

import com.aofei.base.exception.StatusCode;

import java.util.Date;

/**
 * 响应封装
 */
public class Response<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述信息
     */
    private String message;

    /**
     * 响应主体
     */
    private T body;

    /**
     * 服务器时间
     */
    private Date now;

    public Response() {
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public static Response ok(Object t) {
        Response response = new Response();
        response.setCode(StatusCode.OK.getCode());
        response.setMessage(StatusCode.OK.getMessage());
        response.setNow(new Date());
        response.setBody(t);
        return response;
    }

    public static Response ok() {
        Response response = new Response();
        response.setCode(StatusCode.OK.getCode());
        response.setMessage(StatusCode.OK.getMessage());
        response.setNow(new Date());
        response.setBody(true);
        return response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public Date getNow() {
        return new Date();
    }

    public void setNow(Date now) {
        this.now = now;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body=" + body +
                ", now=" + now +
                '}';
    }
}
