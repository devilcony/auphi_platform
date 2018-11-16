package com.aofei.mdm.model.response;

import com.aofei.base.model.response.BaseResponse;
import io.swagger.annotations.ApiModelProperty;

/**
 * @auther Tony
 * @create 2018-11-07 12:11
 */
public class TableResponse extends BaseResponse {

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
}
