package com.aofei.compare.entity;

import com.aofei.base.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("COMPARE_SQL_COLUMN")
public class CompareSqlColumn extends DataEntity<CompareSqlColumn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_COMPARE_SQL_COLUMN", type = IdType.ID_WORKER)
    private Long compareSqlColumnId;

    /**
     * 列名
     */
    @TableField("COLUMN_NAME")
    private String columnName;
    /**
     * 列类型
     */
    @TableField("COLUMN_TYPE")
    private String columnType;

    /**
     * 参考列名
     */
    @TableField("REFERENCE_COLUMN_NAME")
    private String referenceColumnName;

    /**
     * 参考列名
     */
    @TableField("COLUMN_DESC")
    private String columnDesc;

    @TableField("ID_COMPARE_SQL")
    private Long compareSqlId;
    /**
     * 通过标准 0 等值比较，1 范围比较
     */
    @TableField("COMPARE_STYLE")
    private Integer compareStyle;
    /**
     * 最小系数
     */
    @TableField("MIN_RATIO")
    private Double minRatio;
    /**
     * 最大系数
     */
    @TableField("MAX_RATIO")
    private Double maxRatio;

    @TableField(exist =  false)
    private String columnValue;
    @TableField(exist =  false)
    private String referenceColumnValue;
    /**
     * 1 equals,   0  not equals
     */
    @TableField(exist =  false)
    private Integer compareResult;
    @TableField(exist =  false)

    private Date resultTime;


    @Override
    protected Serializable pkVal() {
        return this.compareSqlColumnId;
    }

}
