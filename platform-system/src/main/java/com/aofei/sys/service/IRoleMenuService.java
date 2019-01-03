package com.aofei.sys.service;

import com.aofei.sys.entity.RoleMenu;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 角色与菜单对应关系 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IRoleMenuService extends IService<RoleMenu> {

    /**
     * 修改角色拥有的权限
     * @param roleId
     * @param resources
     * @return
     */
    int changeRolePermission(Long roleId, List<Long> resources);
}
