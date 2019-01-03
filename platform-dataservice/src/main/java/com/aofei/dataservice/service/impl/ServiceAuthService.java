package com.aofei.dataservice.service.impl;

import com.aofei.dataservice.entity.ServiceAuth;
import com.aofei.dataservice.mapper.ServiceAuthMapper;
import com.aofei.dataservice.model.request.ServiceAuthRequest;
import com.aofei.dataservice.model.response.ServiceAuthResponse;
import com.aofei.dataservice.service.IServiceAuthService;
import com.aofei.base.service.impl.BaseService;
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
public class ServiceAuthService extends BaseService<ServiceAuthMapper, ServiceAuth> implements IServiceAuthService {

    @Override
    public Page<ServiceAuthResponse> getPage(Page<Object> pagination, ServiceAuthRequest request) {
        return null;
    }

    @Override
    public List<ServiceAuthResponse> getServiceAuths(ServiceAuthRequest request) {
        return null;
    }

    @Override
    public ServiceAuthResponse save(ServiceAuthRequest request) {
        return null;
    }

    @Override
    public ServiceAuthResponse update(ServiceAuthRequest request) {
        return null;
    }

    @Override
    public int del(Long id) {
        return 0;
    }

    @Override
    public Object get(Long id) {
        return null;
    }
}
