package com.aofei.compare.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
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
public class CompareSqlRequest extends BaseRequest<CompareSqlRequest> {

    private static final long serialVersionUID = 1L;

    private Long compareSqlId;

    /**
     * 资源库管理
     */
    @ApiModelProperty(value = "资源库名称")
    private String repositoryName;

    /**
     * ID in r_databsae table
     */
    @ApiModelProperty(value = "数据源")
    private String databaseId;
    /**
     * 参考sql数据库ID
     */
    @ApiModelProperty(value = "参考sql数据源")
    private String referenceDbId;
    /**
     * ID in profile_table_group
     */
    @ApiModelProperty(value = "分组ID")
    private Integer compareTableGroupId;
    /**
     * profile_name
     */
    @ApiModelProperty(value = "名称")
    private String compareName;
    /**
     * compare desc
     */
    @ApiModelProperty(value = "描述")
    private String compareDesc;
    /**
     * 1 for one value compare, 2 for multi-value compare, default 1
     */
    @ApiModelProperty(value = "1 for one value compare, 2 for multi-value compare, default 1")
    private Integer compareType;

    @ApiModelProperty(value = "sql语句")
    private String sql;

    @ApiModelProperty(value = "参照sql")
    private String referenceSql;

    @ApiModelProperty(value = "参照字段")
    List<CompareSqlColumnRequest> compareSqlColumns;


}
