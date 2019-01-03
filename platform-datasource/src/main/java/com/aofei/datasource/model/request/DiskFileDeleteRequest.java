package com.aofei.datasource.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 删除文件或文件夹
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DiskFileDeleteRequest  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(hidden = true)
    private Long organizerId;

    @ApiModelProperty(value = "文件名称")
    private String name;

    @ApiModelProperty(value = "路径")
    private String path;




}
