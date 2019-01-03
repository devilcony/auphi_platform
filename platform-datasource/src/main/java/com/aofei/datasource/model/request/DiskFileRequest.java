package com.aofei.datasource.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 文件查询
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DiskFileRequest extends BaseRequest<DiskFileRequest> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(hidden = true)
    private Long organizerId;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true)
    private String organizerName;

    @ApiModelProperty(value = "路径")
    private String path;




}
