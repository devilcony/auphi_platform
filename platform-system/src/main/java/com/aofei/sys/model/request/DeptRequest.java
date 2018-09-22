package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 部门管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class DeptRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门ID")
    private Long deptId;
    /**
     * 上级部门ID，一级部门为0
     */
    @ApiModelProperty(value = "上级部门ID，一级部门为0")
    private Long parentId;
    /**
     * 部门名称
     */
    @ApiModelProperty(value = "部门名称")
    private String name;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer orderNum;





}
