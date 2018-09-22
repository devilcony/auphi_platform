package com.aofei.sys.service;

import com.aofei.sys.entity.Role;
import com.aofei.sys.model.request.RoleRequest;
import com.aofei.sys.model.response.RoleResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IRoleService extends IService<Role> {

    /**
     * 获取 Role 列表
     * @param page
     * @param request
     * @return
     */
    Page<RoleResponse> getPage(Page<Role> page, RoleRequest request);

    /**
     * 角色列表
     * @param request
     * @return
     */
    List<RoleResponse> getRoles(RoleRequest request);

    /**
     * 保存 Role 信息
     * @param request
     * @return
     */
    RoleResponse save(RoleRequest request);

    /**
     * 更新 Role 信息
     * @param request
     * @return
     */
    RoleResponse update(RoleRequest request);

    /**
     * 根据Id 查询 Role
     * @param roleId
     * @return
     */
    RoleResponse get(Long roleId);
    /**
     * 根据Id 删除 Role
     * @param roleId
     * @return
     */
    int del(Long roleId);

    /**
     * 查询用户拥有的角色列表
     * @param userId
     * @return
     */
    List<RoleResponse> getRolesByUser(String userId);


}
