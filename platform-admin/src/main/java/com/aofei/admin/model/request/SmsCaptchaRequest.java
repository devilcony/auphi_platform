package com.aofei.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SmsCaptchaRequest {

    @ApiModelProperty(value = "手机号")
    private String mobilephone;


    /**
     * 手机国家代码
     */
    @ApiModelProperty(value = "国家代码")
    private String countryCode;
}
