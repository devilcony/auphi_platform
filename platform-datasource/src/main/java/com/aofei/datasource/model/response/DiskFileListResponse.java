package com.aofei.datasource.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DiskFileListResponse {

    @ApiModelProperty(value = "路径")
    private String path;

    /**
     * 当前页记录数
     */
    @ApiModelProperty(value = "列表数据")
    private List<DiskFileResponse> list;
}
