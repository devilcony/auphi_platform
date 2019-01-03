package com.aofei.datasource.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 创建文件夹
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DiskFileCreateRequest  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(hidden = true)
    private Long organizerId;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true)
    private String organizerName;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "路径")
    private String path;




}
