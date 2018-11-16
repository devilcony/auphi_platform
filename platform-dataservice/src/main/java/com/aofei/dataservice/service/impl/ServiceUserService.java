package com.aofei.dataservice.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.dataservice.entity.ServiceUser;
import com.aofei.dataservice.mapper.ServiceUserMapper;
import com.aofei.dataservice.model.request.ServiceUserRequest;
import com.aofei.dataservice.model.response.ServiceUserResponse;
import com.aofei.dataservice.service.IServiceUserService;
import com.aofei.log.annotation.Log;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Service
public class ServiceUserService extends BaseService<ServiceUserMapper, ServiceUser> implements IServiceUserService {

    @Override
    public Page<ServiceUserResponse> getPage(Page<ServiceUser> page, ServiceUserRequest request) {
        List<ServiceUser> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, ServiceUserResponse.class);
    }

    @Override
    public List<ServiceUserResponse> getServiceUsers(ServiceUserRequest request) {
        List<ServiceUser> list = baseMapper.findList(request);
        return BeanCopier.copy(list,ServiceUserResponse.class);
    }

    @Log(module = "对外数据接出接口管理",description = "新建接口服务用户信息")
    @Override
    public ServiceUserResponse save(ServiceUserRequest request) {
        ServiceUser ServiceUser = BeanCopier.copy(request, ServiceUser.class);
        ServiceUser.preInsert();
        super.insert(ServiceUser);
        return BeanCopier.copy(ServiceUser, ServiceUserResponse.class);
    }

    @Log(module = "对外数据接出接口管理",description = "修改接口服务用户信息")
    @Override
    public ServiceUserResponse update(ServiceUserRequest request) {
        ServiceUser existing = selectById(request.getUserId());
        if (existing != null) {
            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, ServiceUserResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "对外数据接出接口管理",description = "删除接口服务用户信息")
    @Override
    public int del(Long deptId) {
        ServiceUser existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }


    @Override
    public ServiceUserResponse get(Long deptId) {
        ServiceUser existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, ServiceUserResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
