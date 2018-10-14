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
@TableName("COMPARE_SQL")
public class CompareSql extends DataEntity<CompareSql> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_COMPARE_SQL", type = IdType.ID_WORKER)
    private Long compareSqlId;
    /**
     * ID in r_databsae table
     */
    @TableField("ID_DATABASE")
    private String databaseId;
    /**
     * 参考sql数据库ID
     */
    @TableField("ID_REFERENCE_DB")
    private String referenceDbId;
    /**
     * ID in profile_table_group
     */
    @TableField("ID_COMPARE_TABLE_GROUP")
    private Integer compareTableGroupId;
    /**
     * profile_name
     */
    @TableField("COMPARE_NAME")
    private String compareName;
    /**
     * compare desc
     */
    @TableField("COMPARE_DESC")
    private String compareDesc;
    /**
     * 1 for one value compare, 2 for multi-value compare, default 1
     */
    @TableField("COMPARE_TYPE")
    private Integer compareType;
    @TableField("SQL")
    private String sql;
    @TableField("REFERENCE_SQL")
    private String referenceSql;

    @TableField("USER_ID")
    private Integer userId;



    @Override
    protected Serializable pkVal() {
        return this.compareSqlId;
    }

}
