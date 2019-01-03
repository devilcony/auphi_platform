package com.aofei.profile.model.response;

import lombok.Data;

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
public class ProfileTableResponse {

    private static final long serialVersionUID = 1L;

    private Long profileTableId;
    /**
     * 资源库名称
     */
    private String repositoryName;
    /**
     * ID in r_databsae table
     */
    private String databaseId;
    /**
     * ID in profile_table_group
     */
    private Long profielTableGroupId;
    /**
     * profile_name
     */
    private String profielName;
    /**
     * profile_name
     */
    private String profielDesc;

    private String schemaName;

    private String tableName;

    private String condition;


    /**
     * 1:表示TABLE_NAME为表名 2：TABLE_NAME为sql
     */
    private Integer tableNameTag;

    /**
     *字段选择信息
     */
    private List<ProfileTableColumnResponse> profileTableColumns;



}
