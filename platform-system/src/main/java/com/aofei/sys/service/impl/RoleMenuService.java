package com.aofei.sys.service.impl;

import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.sys.entity.RoleMenu;
import com.aofei.sys.mapper.RoleMenuMapper;
import com.aofei.sys.service.IRoleMenuService;
import com.aofei.utils.Utils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色与菜单对应关系 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Service
public class RoleMenuService extends BaseService<RoleMenuMapper, RoleMenu> implements IRoleMenuService {

    @Transactional
    @Log(module = "系统角色", description = "修改角色权限")
    @Override
    public int changeRolePermission(Long roleId, List<Long> resources) {

        if (!Utils.isEmpty(roleId)) {

            super.delete(new EntityWrapper<RoleMenu>().eq("ROLE_ID", roleId));

            if (!Utils.isEmpty(resources)) {
                List<RoleMenu> roleResources = new ArrayList<>();

                for (Long menuId : resources) {
                    RoleMenu roleResource = new RoleMenu();
                    roleResource.setRoleId(roleId);
                    roleResource.setMenuId(menuId);
                    roleResources.add(roleResource);
                }

                super.insertBatch(roleResources);

                return 1;
            }
        }
        return -1;
    }
}
