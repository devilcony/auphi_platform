package com.aofei.mdm.service;

import com.aofei.mdm.entity.ModelAttribute;
import com.aofei.mdm.model.request.ModelAttributeRequest;
import com.aofei.mdm.model.response.ModelAttributeResponse;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 主数据模型属性 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
public interface IModelAttributeService extends IService<ModelAttribute> {

    List<ModelAttributeResponse> getModelAttributes(ModelAttributeRequest request);

    ModelAttributeResponse save(ModelAttributeRequest request);

    ModelAttributeResponse update(ModelAttributeRequest request);

    int del(Long id);

    int delByModelId(Long modelId);

    ModelAttributeResponse get(Long id);
}
