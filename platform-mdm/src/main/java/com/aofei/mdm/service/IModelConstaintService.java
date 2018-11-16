package com.aofei.mdm.service;

import com.aofei.mdm.entity.ModelConstaint;
import com.aofei.mdm.model.request.ModelConstaintRequest;
import com.aofei.mdm.model.response.ModelConstaintResponse;
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
public interface IModelConstaintService extends IService<ModelConstaint> {

    List<ModelConstaintResponse> getListByModelId(Long modelId);

    List<ModelConstaintResponse> getModelConstaints(ModelConstaintRequest request);

    ModelConstaintResponse save(ModelConstaintRequest request);

    ModelConstaintResponse update(ModelConstaintRequest request);

    int del(Long id);

    int delByAttributeId(Long attributeId);

    ModelConstaintResponse get(Long id);
}
