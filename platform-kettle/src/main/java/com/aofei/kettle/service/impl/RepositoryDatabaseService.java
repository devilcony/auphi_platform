package com.aofei.kettle.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.kettle.entity.RepositoryDatabase;
import com.aofei.kettle.entity.RepositoryDatabaseAttribute;
import com.aofei.kettle.mapper.RepositoryDatabaseAttributeMapper;
import com.aofei.kettle.mapper.RepositoryDatabaseMapper;
import com.aofei.kettle.model.request.RepositoryDatabaseAttributeRequest;
import com.aofei.kettle.model.request.RepositoryDatabaseRequest;
import com.aofei.kettle.model.response.RepositoryDatabaseAttributeResponse;
import com.aofei.kettle.model.response.RepositoryDatabaseResponse;
import com.aofei.kettle.service.IRepositoryDatabaseService;
import com.aofei.utils.BeanCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 资源库 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Service
public class RepositoryDatabaseService extends BaseService<RepositoryDatabaseMapper, RepositoryDatabase> implements IRepositoryDatabaseService {

    @Autowired
    RepositoryDatabaseAttributeMapper repositoryDatabaseAttributeMapper;
    /**
     * 根据ID获取资源库连接信息
     * @param repositoryConnectionId
     * @return
     */
    @Override
    public RepositoryDatabaseResponse get(Long repositoryConnectionId) {

        RepositoryDatabase existing = selectById(repositoryConnectionId);
        if(existing!=null){
            RepositoryDatabaseResponse response = BeanCopier.copy(existing, RepositoryDatabaseResponse.class);
            //属性列表
            RepositoryDatabaseAttributeRequest attributeRequest = new RepositoryDatabaseAttributeRequest();
            attributeRequest.setRepositoryConnectionId(repositoryConnectionId);
            List<RepositoryDatabaseAttribute> repositoryDatabaseAttributes = repositoryDatabaseAttributeMapper.findList(attributeRequest);
            if(repositoryDatabaseAttributes!=null){
                response.setAttrs(BeanCopier.copy(repositoryDatabaseAttributes, RepositoryDatabaseAttributeResponse.class));
            }

            return response;
        }else{
            //用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }

    }

    @Override
    public RepositoryDatabaseResponse getByConnectionName(String repositoryConnectionName) {

        RepositoryDatabase existing = baseMapper.findByConnectionName(repositoryConnectionName);
        if(existing!=null){
            RepositoryDatabaseResponse response = BeanCopier.copy(existing, RepositoryDatabaseResponse.class);
            //属性列表
            RepositoryDatabaseAttributeRequest attributeRequest = new RepositoryDatabaseAttributeRequest();
            attributeRequest.setRepositoryConnectionId(existing.getRepositoryConnectionId());
            List<RepositoryDatabaseAttribute> repositoryDatabaseAttributes = repositoryDatabaseAttributeMapper.findList(attributeRequest);
            if(repositoryDatabaseAttributes!=null){
                response.setAttrs(BeanCopier.copy(repositoryDatabaseAttributes, RepositoryDatabaseAttributeResponse.class));
            }

            return response;
        }else{
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public RepositoryDatabaseResponse save(RepositoryDatabaseRequest request) {

        RepositoryDatabase existing = baseMapper.findByConnectionName(request.getRepositoryConnectionName());
        RepositoryDatabase repositoryDatabase = null;
        if(existing != null){
            repositoryDatabase = BeanCopier.copy(request, RepositoryDatabase.class);
            repositoryDatabase.setRepositoryConnectionId(existing.getRepositoryConnectionId());
            super.updateById(repositoryDatabase);

        }else{
            repositoryDatabase = BeanCopier.copy(request, RepositoryDatabase.class);
            repositoryDatabase.preInsert();
            super.insert(repositoryDatabase);

        }
        if(repositoryDatabase!=null && repositoryDatabase.getRepositoryConnectionId()!=null ){
            //属性信息 先删除 在重新插入
            repositoryDatabaseAttributeMapper.deleteByDatabaseId(repositoryDatabase.getRepositoryConnectionId());
            for(RepositoryDatabaseAttributeRequest attributeRequest : request.getAttrs()){
                RepositoryDatabaseAttribute attribute = BeanCopier.copy(attributeRequest,RepositoryDatabaseAttribute.class);
                attribute.setRepositoryConnectionId(repositoryDatabase.getRepositoryConnectionId());
                repositoryDatabaseAttributeMapper.insert(attribute);
            }


        }




        return BeanCopier.copy(repositoryDatabase, RepositoryDatabaseResponse.class);

    }

    @Override
    public List<RepositoryDatabaseResponse> getRepositoryDatabases(RepositoryDatabaseRequest request) {
        List<RepositoryDatabase> list = baseMapper.findList(request);
        return BeanCopier.copy(list,RepositoryDatabaseResponse.class);
    }
}
