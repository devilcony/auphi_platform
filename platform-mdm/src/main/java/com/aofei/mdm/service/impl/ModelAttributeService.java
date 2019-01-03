package com.aofei.mdm.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.mdm.entity.ModelAttribute;
import com.aofei.mdm.mapper.ModelAttributeMapper;
import com.aofei.mdm.model.request.ModelAttributeRequest;
import com.aofei.mdm.model.response.ModelAttributeResponse;
import com.aofei.mdm.service.IModelAttributeService;
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
public class ModelAttributeService extends BaseService<ModelAttributeMapper, ModelAttribute> implements IModelAttributeService {

    @Override
    public List<ModelAttributeResponse> getModelAttributes(ModelAttributeRequest request) {
        List<ModelAttribute> list = baseMapper.findList(request);
        return BeanCopier.copy(list, ModelAttributeResponse.class);
    }

    @Override
    public ModelAttributeResponse save(ModelAttributeRequest request) {
        ModelAttribute modelAttribute = BeanCopier.copy(request, ModelAttribute.class);
        modelAttribute.preInsert();
        super.insert(modelAttribute);
        return BeanCopier.copy(modelAttribute, ModelAttributeResponse.class);
    }

    @Override
    public ModelAttributeResponse update(ModelAttributeRequest request) {
        ModelAttribute existing = selectById(request.getAttributeId());
        if (existing != null) {

            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, ModelAttributeResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public int del(Long id) {
        ModelAttribute existing = selectById(id);
        if (existing != null) {
            super.deleteById(id);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public int delByModelId(Long modelId) {
        return baseMapper.deleteByModelId(modelId);
    }

    @Override
    public ModelAttributeResponse get(Long id) {

        ModelAttribute existing = selectById(id);
        if(existing!=null){
            return BeanCopier.copy(existing, ModelAttributeResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
