package com.aofei.compare.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
public class CompareSqlResultResponse  {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "字段ID")
    private Long compareSqlColumnId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "数据稽核名称")
    private String compareName;

    /**
     * ID in r_databsae table
     */
    @ApiModelProperty(value = "数据源名称")
    private String databaseName;

    /**
     * 列名
     */
    @ApiModelProperty(value = "列名")
    private String columnName;
    /**
     * 列类型
     */
    @ApiModelProperty(value = "列类型")
    private String columnType;
    /**
     * 参考列名
     */
    @ApiModelProperty(value = "参考列名")
    private String referenceColumnName;
    /**
     * 列描述
     */
    @ApiModelProperty(value = "列描述")
    private String columnDesc;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private Long compareTableGroupName;

    @ApiModelProperty(value = "实际值")
    private String columnValue;

    @ApiModelProperty(value = "参考值")
    private String referenceColumnValue;
    /**
     * 1 equals,   0  not equals
     */
    @ApiModelProperty(value = "稽核记过 1:通过,0:不通过")
    private Integer compareResult;




}
