package com.aofei.profile.entity;

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
@TableName("PROFILE_TABLE_RESULT")
public class ProfileTableResult extends DataEntity<ProfileTableResult> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_PROFILE_TABLE_RESULT", type = IdType.ID_WORKER)
    private Integer profileTableResultId;
    /**
     * COLUMN ID
     */
    @TableField("ID_PROFILE_TABLE_COLUMN")
    private Long profileTableColumnId;
    @TableField("INDICATOR_DATA_TYPE")
    private String indicatorDataType;
    @TableField("INDICATOR_DATA_LENGTH")
    private Integer indicatorDataLength;
    @TableField("INDICATOR_DATA_PRECISION")
    private Integer indicatorDataPrecision;
    @TableField("INDICATOR_DATA_SCALE")
    private Integer indicatorDataScale;
    @TableField("INDICATOR_ALL_COUNT")
    private Integer indicatorAllCount;
    @TableField("INDICATOR_DISTINCT_COUNT")
    private Integer indicatorDistinctCount;
    @TableField("INDICATOR_NULL_COUNT")
    private Integer indicatorNullCount;
    @TableField("INDICATOR_ZERO_COUNT")
    private Integer indicatorZeroCount;
    @TableField("INDICATOR_AGG_AVG")
    private String indicatorAggAvg;
    @TableField("INDICATOR_AGG_MAX")
    private String indicatorAggMax;
    @TableField("INDICATOR_AGG_MIN")
    private String indicatorAggMin;

    @TableField("EXECUTE_SQL")
    private String executeSql;



    @Override
    protected Serializable pkVal() {
        return this.profileTableResultId;
    }

}
