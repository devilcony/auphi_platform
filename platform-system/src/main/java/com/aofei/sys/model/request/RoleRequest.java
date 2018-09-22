package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther Tony
 * @create 2018-09-18 14:23
 */
@Getter
@Setter
public class RoleRequest extends BaseRequest {

    private Long roleId;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 部门ID
     */
    private Long deptId;
}
