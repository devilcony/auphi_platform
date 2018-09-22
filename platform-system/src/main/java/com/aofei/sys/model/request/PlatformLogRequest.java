package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 系统日志
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
public class PlatformLogRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private Long logId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户操作
     */
    private String operation;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求参数
     */
    private String params;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 是否删除  1：已删除  0：正常
     */
    private Integer delFlag;




}
