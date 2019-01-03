package com.aofei.compare.model.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
public class CompareSqlColumnResponse  {

    private static final long serialVersionUID = 1L;

    private Long compareSqlColumnId;

    /**
     * 列名
     */
    private String columnName;
    /**
     * 列类型
     */
    private String columnType;

    /**
     * 参考列名
     */
    private String referenceColumnName;

    /**
     * 参考列名
     */
    private String columnDesc;

    private Long compareSqlId;
    /**
     * 通过标准 0 等值比较，1 范围比较
     */
    private Integer compareStyle;
    /**
     * 最小系数
     */
    private BigDecimal minRatio;
    /**
     * 最大系数
     */
    private BigDecimal maxRatio;

    private String columnValue;

    private String referenceColumnValue;
    /**
     * 1 equals,   0  not equals
     */
    private Integer compareResult;

    private Date resultTime;


}
