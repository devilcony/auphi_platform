package com.aofei.datasource.service;

import com.aofei.datasource.entity.Hadoop;
import com.aofei.datasource.model.request.HadoopRequest;
import com.aofei.datasource.model.response.HadoopResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * HADOOP管理 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
public interface IHadoopService extends IService<Hadoop> {

    Page<HadoopResponse> getPage(Page<Hadoop> page, HadoopRequest request);

    List<HadoopResponse> getHadoops(HadoopRequest request);

    HadoopResponse save(HadoopRequest request);

    HadoopResponse update(HadoopRequest request);

    int del(Long id);

    HadoopResponse get(Long id);
}
