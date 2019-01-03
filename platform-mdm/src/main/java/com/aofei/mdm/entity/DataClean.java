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
 * 数据映射
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_DATA_CLEAN")
public class DataClean extends DataEntity<DataClean> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;
    @TableField("ID_MODEL")
    private Long modelId;

    @TableField(exist = false)
    private String modelName;

    @TableField("ATTRIBUTE_MODEL")
    private String attributeModel;

    @TableField("REPOSITORY_NAME")
    private String repositoryName;

    @TableField("MDM_ID_DATABASE")
    private String databaseId;

    @TableField("MDM_SCHEMA_NAME")
    private String schemaName;
    @TableField("MDM_TABLE_NAME")
    private String tableName;
    @TableField("MDM_PRIMARY_KEY")
    private String primaryKey;
    @TableField("MDM_COLUMN_NAME")
    private String columnName;
    @TableField("MDM_WHERE_CONDITION")
    private String whereCondition;
    @TableField("MAPING_MODE")
    private Integer mapingMode;

    @TableField("MAPING_ID_DATABASE")
    private String mapingIdDatabase;

    @TableField("MAPING_SCHEMA_NAME")
    private String mapingSchemaName;

    @TableField("MAPING_TABLE_NAME")
    private String mapingTableName;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
