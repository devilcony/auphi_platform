package com.aofei.sys.entity;

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
 * 资源库管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_REPOSITORY")
public class Repository extends DataEntity<Repository> {

    private static final long serialVersionUID = 1L;

    /**
     * 资源库ID
     */
    @TableId(value = "REPOSITORY_ID", type = IdType.ID_WORKER)
    private Long repositoryId;
    /**
     * 资源数据库链接ID
     */
    @TableField("REPOSITORY_CONNECTION_ID")
    private Long repositoryConnectionId;

    /**
     * 资源数据库链接名称
     */
    @TableField(exist = false)
    private String repositoryConnectionName;

    /**
     * 描述
     */
    @TableField("DESCRIPTION")
    private String description;


    /**
     * 主机名
     */
    @TableField(exist = false)
    private String hostName;


    /**
     * 数据库类型
     */
    @TableField(exist = false)
    private String databaseTypeName;

    /**
     * 资源库名称
     */
    @TableField("REPOSITORY_NAME")
    private String repositoryName;
    /**
     * 是否默认资源库 0:否 1:是
     */
    @TableField("IS_DEFAULT")
    private Integer isDefault;

    /**
     * 资源库用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 资源库密码
     */
    @TableField(exist = false)
    private String password;




    @Override
    protected Serializable pkVal() {
        return this.repositoryId;
    }

}
