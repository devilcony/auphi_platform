package com.aofei.sys.service;

import com.aofei.sys.entity.UserRole;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 用户与角色对应关系 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IUserRoleService extends IService<UserRole> {

    Integer changeUserRole(Long userId, List<Long> roles);

    int deleteUserRole(Long userId, Long roleId);
}
