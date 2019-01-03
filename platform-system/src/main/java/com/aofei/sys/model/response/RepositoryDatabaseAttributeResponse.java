package com.aofei.sys.model.response;

import com.aofei.base.model.response.IgnoreResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 资源库链接属性
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
@ApiModel(value = "资源库链接属性相应体")
public class RepositoryDatabaseAttributeResponse extends IgnoreResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 资源库属性ID
     */
    @ApiModelProperty(value = "资源库属性ID")
    private Long repositoryDatabaseAttributeId;

    /**
     * 属性名
     */
    @ApiModelProperty(value = "属性名")
    private String code;
    /**
     * 属性值
     */
    @ApiModelProperty(value = "属性值")
    private String valueStr;



}
