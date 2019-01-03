package com.aofei.datasource.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 本地数据库
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DatabaseRequest extends BaseRequest<DatabaseRequest> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(hidden = true)
    private Long organizerId;

    private String name;

    private String repository;




}
