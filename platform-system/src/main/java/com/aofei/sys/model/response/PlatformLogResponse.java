package com.aofei.sys.model.response;

import io.swagger.annotations.ApiModelProperty;
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
public class PlatformLogResponse  {

    private static final long serialVersionUID = 1L;

    private Long platformLogId;
    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;
    /**
     * 用户操作
     */
    @ApiModelProperty(value = "用户操作")
    private String operation;

    /**
     * 模块
     */
    @ApiModelProperty(value = "模块")
    private String module;

    /**
     * 请求方法
     */
    @ApiModelProperty(value = "请求方法")
    private String method;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数(JSON)")
    private String params;
    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址")
    private String ip;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createDate;



}
