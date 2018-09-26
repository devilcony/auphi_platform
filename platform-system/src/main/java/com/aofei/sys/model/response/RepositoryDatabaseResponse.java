package com.aofei.sys.model.response;

import com.aofei.base.model.response.IgnoreResponse;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 资源库
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
@ApiModel(value = "资源库数据库响应体")
public class RepositoryDatabaseResponse extends IgnoreResponse {

    private static final long serialVersionUID = 1L;

    /**
     * 资源数据库链接ID
     */
    private Long repositoryConnectionId;
    /**
     * 资源数据库链接名称
     */
    private String repositoryConnectionName;

    /**
     * 数据库类型
     */
    private String databaseType;


    /**
     * 数据库连接方式
     */
    private Integer databaseContype;

    /**
     * 主机名
     */
    private String hostName;
    /**
     * 数据库名
     */
    private String databaseName;
    /**
     * 端口
     */
    private String port;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 服务器名
     */
    private String servername;
    /**
     * 数据表空间
     */
    private String dataTbs;
    /**
     * 索引表空间
     */
    private String indexTbs;

    /**
     * 属性列表
     */
    List<RepositoryDatabaseAttributeResponse> attrs = Lists.newArrayList();

}
