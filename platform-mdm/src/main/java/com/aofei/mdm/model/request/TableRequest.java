package com.aofei.mdm.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 主数据表
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class TableRequest extends BaseRequest<TableRequest> {

    private static final long serialVersionUID = 1L;

    private Long tableId;
    /**
     * ID in model table
     */
    @ApiModelProperty(value = "模型ID")
    private Long modelId;

    @ApiModelProperty(value = "资源库")
    private String repositoryName;
    /**
     * ID in r_databsae table
     *
     */
    @ApiModelProperty(value = "数据库ID")
    private String databaseId;

    @ApiModelProperty(value = "模式名")
    private String schemaName;

    @ApiModelProperty(value = "表名")
    private String tableName;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true,value = "组织ID")
    private Long organizerId;


}
