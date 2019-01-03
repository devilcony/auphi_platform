package com.aofei.compare.model.response;

import com.aofei.base.model.response.BaseResponse;
import com.aofei.compare.entity.CompareSqlColumn;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("COMPARE_SQL")
public class CompareSqlResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Long compareSqlId;


    /**
     * 资源库名称
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
    List<CompareSqlColumn> compareSqlColumns;


}
