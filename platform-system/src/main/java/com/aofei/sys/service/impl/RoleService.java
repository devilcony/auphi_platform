package com.aofei.sys.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.sys.entity.Role;
import com.aofei.sys.mapper.RoleMapper;
import com.aofei.sys.model.request.RoleRequest;
import com.aofei.sys.model.response.RoleResponse;
import com.aofei.sys.service.IRoleService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Service
public class RoleService extends BaseService<RoleMapper, Role> implements IRoleService {

    @Override
    public Page<RoleResponse> getPage(Page<Role> page, RoleRequest request) {
        List<Role> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, RoleResponse.class);
    }

    @Override
    public List<RoleResponse> getRoles(RoleRequest request) {
        List<Role> list = baseMapper.findList(request);
        return BeanCopier.copy(list,RoleResponse.class);
    }

    @Override
    @Log(module = "系统角色", description = "新建角色信息")
    public RoleResponse save(RoleRequest request) {
        Role Role = BeanCopier.copy(request, Role.class);
        Role.preInsert();
        super.insert(Role);
        return BeanCopier.copy(Role, RoleResponse.class);
    }

    @Override
    @Transactional
    @Log(module = "系统角色", description = "修改角色信息")
    public RoleResponse update(RoleRequest request) {
        Role existing = selectById(request.getRoleId());
        if (existing != null) {
            existing.setRoleName(request.getRoleName());
            existing.preUpdate();

            super.insertOrUpdate(existing);

            return BeanCopier.copy(existing, RoleResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public RoleResponse get(Long roleId) {
        Role existing = selectById(roleId);
        if(existing!=null){
            return BeanCopier.copy(existing, RoleResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    @Log(module = "系统角色", description = "删除角色信息")
    public int del(Long roleId) {
        Role existing = selectById(roleId);
        if (existing != null) {
            super.deleteById(roleId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public List<RoleResponse> getRolesByUser(Long userId) {
        List<Role> roles = baseMapper.findRoleByUserId(userId);
        List<RoleResponse> responses = BeanCopier.copy(roles, RoleResponse.class);
        return responses;
    }
}
