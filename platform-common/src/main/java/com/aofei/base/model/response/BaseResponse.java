package com.aofei.base.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseResponse {

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    protected Date createTime;



}
