package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.aofei.sys.entity.Repository;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 资源库管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
public class RepositoryRequest extends BaseRequest<Repository> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "资源库ID")
    private Long repositoryId;

    /**
     * 资源数据库链接ID
     */
    @ApiModelProperty(value = "资源数据库链接ID")
    private Long repositoryDatabaseId;

    /**
     * 资源库名称
     */
    @ApiModelProperty(value = "资源库名称")
    private String repositoryName;

    /**
     * 是否默认资源库 0:否 1:是
     */
    @ApiModelProperty(value = "是否默认资源库 0:否 1:是")
    private Integer isDefault;




}
