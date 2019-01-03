package com.aofei.sys.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther Tony
 * @create 2018-09-18 14:23
 */
@Getter
@Setter
@ApiModel(description = "角色响应体")
public class RoleResponse {


    private Long roleId;
    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "组织ID")
    private Long organizerId;

    private String organizerName;
    /**
     * 设计器权限
     */
    @ApiModelProperty(value = "设计器权限")
    private Long priviledges;
    /**
     * 是否是系统保留权限
     */
    @ApiModelProperty(value = "是否是系统保留权限(1:是 0:否 ),系统保留不可更改")
    private Integer isSystemRole;


}
