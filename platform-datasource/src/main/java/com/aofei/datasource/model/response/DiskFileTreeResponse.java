package com.aofei.datasource.model.response;

import io.swagger.annotations.ApiModelProperty;

public class DiskFileTreeResponse {

    @ApiModelProperty(value = "路径")
    private String path;
    @ApiModelProperty(value = "名称")
    private String filename;
}
