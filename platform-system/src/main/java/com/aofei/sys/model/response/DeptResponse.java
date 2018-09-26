package com.aofei.sys.model.response;

import com.aofei.base.model.response.BaseResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther Tony
 * @create 2018-09-15 17:48
 */
@Getter
@Setter
@ApiModel(value = "部门相应体")
public class DeptResponse extends BaseResponse {


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
