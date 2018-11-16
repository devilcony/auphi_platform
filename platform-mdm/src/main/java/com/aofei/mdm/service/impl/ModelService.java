package com.aofei.mdm.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.mdm.entity.Model;
import com.aofei.mdm.entity.ModelAttribute;
import com.aofei.mdm.entity.ModelConstaint;
import com.aofei.mdm.mapper.ModelAttributeMapper;
import com.aofei.mdm.mapper.ModelConstaintMapper;
import com.aofei.mdm.mapper.ModelMapper;
import com.aofei.mdm.model.request.ModelAttributeRequest;
import com.aofei.mdm.model.request.ModelConstaintRequest;
import com.aofei.mdm.model.request.ModelRequest;
import com.aofei.mdm.model.response.ModelResponse;
import com.aofei.mdm.service.IModelService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 主数据模型 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class ModelService extends BaseService<ModelMapper, Model> implements IModelService {

    @Autowired
    private ModelAttributeMapper modelAttributeMapper;

    @Autowired
    private ModelConstaintMapper modelConstaintMapper;

    @Override
    public Page<ModelResponse> getPage(Page<Model> page, ModelRequest request) {
        List<Model> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, ModelResponse.class);
    }

    @Override
    public List<ModelResponse> getModels(ModelRequest request) {
        List<Model> list = baseMapper.findList(request);
        return BeanCopier.copy(list,ModelResponse.class);
    }

    @Log(module = "主数据管理",description = "新建主数据模型信息")
    @Override
    public ModelResponse save(ModelRequest request) {
        Model model = BeanCopier.copy(request, Model.class);
        model.preInsert();
        super.insert(model);
        for(ModelAttributeRequest attributeRequest : request.getAttributes()){
            ModelAttribute modelAttribute = BeanCopier.copy(attributeRequest,ModelAttribute.class);
            modelAttribute.preInsert();
            modelAttribute.setModelId(model.getModelId());
            modelAttributeMapper.insert(modelAttribute);
            for(ModelConstaintRequest constaintRequest : attributeRequest.getConstaints()){
                ModelConstaint modelConstaint = BeanCopier.copy(constaintRequest,ModelConstaint.class);
                modelConstaint.preInsert();
                modelConstaint.setAttributeId(modelAttribute.getAttributeId());
                modelConstaintMapper.insert(modelConstaint);
            }
        }

        return BeanCopier.copy(model, ModelResponse.class);
    }

    @Log(module = "主数据管理",description = "修改主数据模型信息")
    @Override
    public ModelResponse update(ModelRequest request) {
        Model existing = selectById(request.getModelId());

        if (existing != null) {
            List<ModelAttribute> attributes = modelAttributeMapper.findListByModelId(existing.getModelId());

            for(ModelAttribute attribute : attributes){
                modelConstaintMapper.deleteByAttributeId(attribute.getAttributeId());
                modelAttributeMapper.deleteById(attribute.getAttributeId());
            }


            existing.setModelName(request.getModelName());
            super.insertOrUpdate(existing);

            for(ModelAttributeRequest attributeRequest : request.getAttributes()){
                ModelAttribute modelAttribute = BeanCopier.copy(attributeRequest,ModelAttribute.class);
                modelAttribute.preInsert();
                modelAttribute.setModelId(existing.getModelId());
                modelAttributeMapper.insert(modelAttribute);
                for(ModelConstaintRequest constaintRequest : attributeRequest.getConstaints()){
                    ModelConstaint modelConstaint = BeanCopier.copy(constaintRequest,ModelConstaint.class);
                    modelConstaint.preInsert();
                    modelConstaint.setAttributeId(modelAttribute.getAttributeId());
                    modelConstaintMapper.insert(modelConstaint);
                }
            }

            return BeanCopier.copy(existing, ModelResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "主数据管理",description = "删除主数据模型信息")
    @Override
    public int del(Long deptId) {
        Model existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }


    @Override
    public ModelResponse get(Long deptId) {
        Model existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, ModelResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
