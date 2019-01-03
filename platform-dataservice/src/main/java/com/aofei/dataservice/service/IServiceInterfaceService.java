package com.aofei.dataservice.service;

import com.aofei.dataservice.entity.ServiceInterface;
import com.aofei.dataservice.model.request.ServiceInterfaceRequest;
import com.aofei.dataservice.model.response.ServiceInterfaceResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
public interface IServiceInterfaceService extends IService<ServiceInterface> {

    Page<ServiceInterfaceResponse> getPage(Page<ServiceInterface> page, ServiceInterfaceRequest request);

    List<ServiceInterfaceResponse> getServiceInterfaces(ServiceInterfaceRequest request);

    ServiceInterfaceResponse save(ServiceInterfaceRequest request);

    ServiceInterfaceResponse update(ServiceInterfaceRequest request);

    int del(Long id);

    ServiceInterfaceResponse get(Long id);
}
