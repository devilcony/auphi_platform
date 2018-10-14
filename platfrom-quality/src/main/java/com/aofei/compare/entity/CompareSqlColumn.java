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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("COMPARE_SQL_COLUMN")
public class CompareSqlColumn extends DataEntity<CompareSqlColumn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_COMPARE_SQL_COLUMN", type = IdType.ID_WORKER)
    private Long compareSqlColumnId;
    @TableField("COLUMN_NAME")
    private String columnName;
    @TableField("COLUMN_TYPE")
    private String columnType;
    @TableField("REFERENCE_COLUMN_NAME")
    private String referenceColumnName;
    @TableField("COLUMN_DESC")
    private String columnDesc;
    @TableField("ID_COMPARE_SQL")
    private Long compareSqlId;
    /**
     * 0 等值比较，1 范围比较
     */
    @TableField("COMPARE_STYLE")
    private Integer compareStyle;
    @TableField("MIN_RATIO")
    private BigDecimal minRatio;
    @TableField("MAX_RATIO")
    private BigDecimal maxRatio;



    @Override
    protected Serializable pkVal() {
        return this.compareSqlColumnId;
    }

}
