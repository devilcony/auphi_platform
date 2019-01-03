package com.aofei.sys.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther Tony
 * @create 2018-09-15 15:06
 */
@Setter
@Getter
public class PhoneRegisterRequest {


    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String username;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
    private String password;

    /**
     * 组织名称
     */
    @ApiModelProperty(value = "组织名称")
    private String organizerName;

    /**
     * 手机国家代码
     */
    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机")
    private String mobilephone;

    /**
     * 手机
     */
    @ApiModelProperty(value = "短信验证码(短信注册必填)")
    private String captcha;

}
