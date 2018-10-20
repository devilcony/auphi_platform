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
@TableName("PROFILE_TABLE_COLUMN")
public class ProfileTableColumn extends DataEntity<ProfileTableColumn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_PROFILE_TABLE_COLUMN", type = IdType.ID_WORKER)
    private Long profileTableColumnId;
    @TableField("PROFILE_TABLE_COLUMN_NAME")
    private String profileTableColumnName;
    @TableField("PROFILE_TABLE_COLUMN_TYPE")
    private String profileTableColumnType;
    @TableField("ID_PROFILE_TABLE")
    private Long profileTableId;
    @TableField("PROFILE_TABLE_COLUMN_DESC")
    private String profileTableColumnDesc;
    @TableField("PROFILE_TABLE_COLUMN_SIZE")
    private String profileTableColumnSize;
    @TableField("PROFILE_TABLE_COLUMN_PRECISION")
    private String profileTableColumnPrecision;
    @TableField("PROFILE_TABLE_COLUMN_ORDER")
    private Integer profileTableColumnOrder;

    @TableField(exist = false)
    private Integer indicatorAllCount;//总数
    @TableField(exist = false)
    private Integer indicatorDistinctCount; //不同值的个数
    @TableField(exist = false)
    private Integer indicatorNullCount; //空值的个数
    @TableField(exist = false)
    private Integer indicatorZeroCount; //零的个数
    @TableField(exist = false)
    private String indicatorAggAvg;//平均值
    @TableField(exist = false)
    private String indicatorAggMax;//最大值
    @TableField(exist = false)
    private String indicatorAggMin;//最小值
    @TableField(exist =  false)
    private Date resultTime;


    @Override
    protected Serializable pkVal() {
        return this.profileTableColumnId;
    }

}
