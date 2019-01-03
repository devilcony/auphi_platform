package com.aofei.mdm.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.mdm.entity.ModelConstaint;
import com.aofei.mdm.mapper.ModelConstaintMapper;
import com.aofei.mdm.model.request.ModelConstaintRequest;
import com.aofei.mdm.model.response.ModelConstaintResponse;
import com.aofei.mdm.service.IModelConstaintService;
import com.aofei.utils.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 主数据模型属性 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class ModelConstaintService extends BaseService<ModelConstaintMapper, ModelConstaint> implements IModelConstaintService {

    @Override
    public List<ModelConstaintResponse> getListByModelId(Long modelId) {
        List<ModelConstaint> list = baseMapper.findListByModelId(modelId);

        return BeanCopier.copy(list, ModelConstaintResponse.class);
    }

    @Override
    public List<ModelConstaintResponse> getModelConstaints(ModelConstaintRequest request) {
        List<ModelConstaint> list = baseMapper.findList(request);
        return BeanCopier.copy(list, ModelConstaintResponse.class);
    }

    @Override
    public ModelConstaintResponse save(ModelConstaintRequest request) {
        ModelConstaint modelConstaint = BeanCopier.copy(request, ModelConstaint.class);
        modelConstaint.preInsert();
        super.insert(modelConstaint);
        return BeanCopier.copy(modelConstaint, ModelConstaintResponse.class);
    }

    @Override
    public ModelConstaintResponse update(ModelConstaintRequest request) {
        ModelConstaint existing = selectById(request.getConstaintId());
        if (existing != null) {

            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, ModelConstaintResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public int del(Long id) {
        ModelConstaint existing = selectById(id);
        if (existing != null) {
            super.deleteById(id);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public int delByAttributeId(Long attributeId) {
        return baseMapper.deleteByAttributeId(attributeId);
    }

    @Override
    public ModelConstaintResponse get(Long id) {
        ModelConstaint existing = selectById(id);
        if(existing!=null){
            return BeanCopier.copy(existing, ModelConstaintResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
