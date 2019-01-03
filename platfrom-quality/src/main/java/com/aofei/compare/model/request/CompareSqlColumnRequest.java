package com.aofei.compare.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
public class CompareSqlColumnRequest  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "列名")
    private String columnName;

    @ApiModelProperty(value = "列数据类型")
    private String columnType;

    @ApiModelProperty(value = "参照列名")
    private String referenceColumnName;

    @ApiModelProperty(value = "列描述")
    private String columnDesc;

    /**
     * 0 等值比较，1 范围比较
     */
    @ApiModelProperty(value = "0 等值比较，1 范围比较")
    private Integer compareStyle;

    @ApiModelProperty(value = "范围比较:最小值")
    private BigDecimal minRatio;

    @ApiModelProperty(value = "范围比较:最大值")
    private BigDecimal maxRatio;


}
