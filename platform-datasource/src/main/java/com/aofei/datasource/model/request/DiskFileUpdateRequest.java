package com.aofei.datasource.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 文件或文件夹修改名称
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DiskFileUpdateRequest  {

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

    @ApiModelProperty(value = "新文件名称")
    private String newName;



}
