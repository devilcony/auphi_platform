package com.aofei.mdm.service;

import com.aofei.mdm.entity.Model;
import com.aofei.mdm.model.request.ModelRequest;
import com.aofei.mdm.model.response.ModelResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 主数据模型 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
public interface IModelService extends IService<Model> {

    Page<ModelResponse> getPage(Page<Model> page, ModelRequest request);

    List<ModelResponse> getModels(ModelRequest request);

    ModelResponse save(ModelRequest request);

    ModelResponse update(ModelRequest request);

    int del(Long id);

    ModelResponse get(Long id);
}
