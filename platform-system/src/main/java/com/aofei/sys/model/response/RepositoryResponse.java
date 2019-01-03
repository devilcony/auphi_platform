package com.aofei.sys.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 资源库管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
public class RepositoryResponse  {

    private static final long serialVersionUID = 1L;

    /**
     * 资源库ID
     */
    @ApiModelProperty(value = "资源库ID")
    private Long repositoryId;

    /**
     * 资源库名称
     */
    @ApiModelProperty(value = "资源库名称")
    private String repositoryName;

    /**
     *资源库描述
     */
    @ApiModelProperty(value = "资源库描述")
    private String description;

    /**
     * 资源数据库链接ID
     */
    @ApiModelProperty(value = "资源数据库链接ID")
    private Long repositoryConnectionId;

    /**
     * 资源数据库链接名称
     */
    @ApiModelProperty(value = "资源数据库链接名称")
    private String repositoryConnectionName;

    /**
     * 主机名
     */
    @ApiModelProperty(value = "主机名")
    private String hostName;

    /**
     * 数据库类型
     */
    @ApiModelProperty(value = "数据库类型")
    private String databaseTypeName;



    /**
     * 是否默认资源库 0:否 1:是
     */
    @ApiModelProperty(value = "是否默认资源库 0:否 1:是")
    private Integer isDefault;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;




}
