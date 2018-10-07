package com.aofei.sys.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther Tony
 * @create 2018-09-28 12:05
 */
@Data
public class ModifyPasswordResquest {


    @ApiModelProperty(value = "原密码")
    private String originalPassword;

    @ApiModelProperty(value = "新密码")
    private String newPassword;
}
