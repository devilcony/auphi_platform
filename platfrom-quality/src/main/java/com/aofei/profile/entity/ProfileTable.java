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
import java.util.List;

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
@TableName("PROFILE_TABLE")
public class ProfileTable extends DataEntity<ProfileTable> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_PROFILE_TABLE", type = IdType.ID_WORKER)
    private Long profileTableId;
    /**
     * 资源库名称
     */
    @TableField("REPOSITORY_NAME")
    private String repositoryName;
    /**
     * ID in r_databsae table
     */
    @TableField("ID_DATABASE")
    private String databaseId;
    /**
     * ID in profile_table_group
     */
    @TableField("ID_PROFIEL_TABLE_GROUP")
    private Long profielTableGroupId;
    /**
     * profile_name
     */
    @TableField("PROFIEL_NAME")
    private String profielName;
    /**
     * profile_name
     */
    @TableField("PROFIEL_DESC")
    private String profielDesc;
    @TableField("SCHEMA_NAME")
    private String schemaName;
    @TableField("TABLE_NAME")
    private String tableName;
    @TableField("CONDITION")
    private String condition;

    @TableField("USER_ID")
    private Long userId;
    /**
     * 1:表示TABLE_NAME为表名 2：TABLE_NAME为sql
     */
    @TableField("TABLE_NAME_TAG")
    private Integer tableNameTag;

    /**
     *字段选择信息
     */
    @TableField(exist = false)
    private List<ProfileTableColumn> profileTableColumns;

    @Override
    protected Serializable pkVal() {
        return this.profileTableId;
    }

}
