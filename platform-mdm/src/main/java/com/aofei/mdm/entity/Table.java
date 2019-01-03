package com.aofei.mdm.entity;

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
 * 主数据表
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_TABLE")
public class Table extends DataEntity<Table> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_TABLE", type = IdType.ID_WORKER)
    private Long tableId;
    /**
     * ID in model table
     */
    @TableField("ID_MODEL")
    private Long modelId;

    @TableField(exist = false)
    private String modelName;

    @TableField("REPOSITORY_NAME")
    private String repositoryName;
    /**
     * ID in r_databsae table
     */
    @TableField("ID_DATABASE")
    private String databaseId;
    @TableField("SCHEMA_NAME")
    private String schemaName;
    @TableField("TABLE_NAME")
    private String tableName;



    @Override
    protected Serializable pkVal() {
        return this.tableId;
    }

}
