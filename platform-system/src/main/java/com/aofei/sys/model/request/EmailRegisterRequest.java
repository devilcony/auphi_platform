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
public class EmailRegisterRequest {


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
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**
     * 手机
     */
    @ApiModelProperty(value = "邮箱验证码)")
    private String captcha;

}
