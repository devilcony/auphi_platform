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
@TableName("COMPARE_SQL_RESULT")
public class CompareSqlResult extends DataEntity<CompareSqlResult> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_COMPARE_SQL_RESULT", type = IdType.ID_WORKER)
    private Long compareSqlResultId;
    @TableField("ID_COMPARE_SQL_COLUMN")
    private Long compareSqlColumnId;
    @TableField("COLUMN_VALUE")
    private String columnValue;
    @TableField("REFERENCE_COLUMN_VALUE")
    private String referenceColumnValue;
    /**
     * 1 equals,   0  not equals
     */
    @TableField("COMPARE_RESULT")
    private Integer compareResult;




    @Override
    protected Serializable pkVal() {
        return this.compareSqlResultId;
    }

}
