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
 * 资源库
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_REPOSITORY_DATABASE")
public class RepositoryDatabase extends DataEntity<RepositoryDatabase> {

    private static final long serialVersionUID = 1L;

    /**
     * 资源数据库链接ID
     */
    @TableId(value = "REPOSITORY_CONNECTION_ID", type = IdType.ID_WORKER)
    private Long repositoryConnectionId;
    /**
     * 资源数据库链接名称
     */
    @TableField("REPOSITORY_CONNECTION_NAME")
    private String repositoryConnectionName;

    /**
     * 数据库类型
     */
    @TableField("DATABASE_TYPE")
    private String databaseType;


    /**
     * 数据库连接方式
     */
    @TableField("DATABASE_CONTYPE")
    private Integer databaseContype;



    /**
     * 主机名
     */
    @TableField("HOST_NAME")
    private String hostName;
    /**
     * 数据库名
     */
    @TableField("DATABASE_NAME")
    private String databaseName;
    /**
     * 端口
     */
    @TableField("PORT")
    private String port;
    /**
     * 用户名
     */
    @TableField("USERNAME")
    private String username;
    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;
    /**
     * 服务器名
     */
    @TableField("SERVERNAME")
    private String servername;
    /**
     * 数据表空间
     */
    @TableField("DATA_TBS")
    private String dataTbs;
    /**
     * 索引表空间
     */
    @TableField("INDEX_TBS")
    private String indexTbs;



    @Override
    protected Serializable pkVal() {
        return this.repositoryConnectionId;
    }

}
