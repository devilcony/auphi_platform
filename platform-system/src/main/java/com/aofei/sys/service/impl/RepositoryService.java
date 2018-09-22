package com.aofei.sys.service.impl;

import com.aofei.base.common.Const;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.sys.entity.Repository;
import com.aofei.sys.mapper.RepositoryMapper;
import com.aofei.sys.model.request.RepositoryRequest;
import com.aofei.sys.model.response.RepositoryResponse;
import com.aofei.sys.service.IRepositoryService;
import com.aofei.utils.BeanCopier;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 资源库管理 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Service
public class RepositoryService extends BaseService<RepositoryMapper, Repository> implements IRepositoryService {

    /**
     * 分页查询
     * @param page
     * @param request
     * @return
     */
    @Override
    public Page<RepositoryResponse> getPage(Page<Repository> page, RepositoryRequest request) {
        List<Repository> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, RepositoryResponse.class);
    }

    /**
     * 查询所有列表
     * @param request
     * @return
     */
    @Override
    public List<RepositoryResponse> getRepositorys(RepositoryRequest request) {
        List<Repository> list = baseMapper.findList(request);
        return BeanCopier.copy(list,RepositoryResponse.class);
    }

    /**
     * 新增资源库信息
     * @param request
     * @return
     */
    @Override
    public RepositoryResponse save(RepositoryRequest request){
        Repository existing = baseMapper.findByRepositoryName(request.getRepositoryName());
        if(existing != null){
            throw new ApplicationException(StatusCode.CONFLICT.getCode(), StringUtils.getMessage("System.Error.RepositorynameExist"));
        }
        Repository repository = BeanCopier.copy(request, Repository.class);
        if(existing.getIsDefault()==null){
            existing.setIsDefault(Const.NO);
        }
        repository.preInsert();
        super.insert(repository);
        return BeanCopier.copy(repository, RepositoryResponse.class);
    }

    /**
     * 更新资源库信息
     * @param request
     * @return
     */
    @Override
    public RepositoryResponse update(RepositoryRequest request){
        Repository existing = baseMapper.selectById(request.getRepositoryId());
        if (existing != null) {
            Repository repository = baseMapper.findByRepositoryName(request.getRepositoryName());

            if(repository != null && !existing.getRepositoryId().equals(request.getRepositoryId())){
                throw new ApplicationException(StatusCode.CONFLICT.getCode(), StringUtils.getMessage("System.Error.RepositorynameExist"));
            }
            existing.setRepositoryDatabaseId(request.getRepositoryDatabaseId());
            existing.setRepositoryName(request.getRepositoryName());
            existing.setIsDefault(request.getIsDefault());
            if(existing.getIsDefault()==null){
                existing.setIsDefault(Const.NO);
            }
            if(existing.getIsDefault() == Const.YES){
                baseMapper.cancelAllDefault();
            }
            baseMapper.updateById(existing);
            return BeanCopier.copy(existing,RepositoryResponse.class);
        }else{
            //资源库不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }

    }


    /**
     * 根据ID删除资源库信息
     * @param repositoryId
     * @return
     */
    @Override
    public int del(Long repositoryId){
        return baseMapper.deleteById(repositoryId);
    }

    /**
     * 根据ID查询资源库信息
     * @param repositoryId
     * @return
     */
    @Override
    public RepositoryResponse get(Long repositoryId){
        Repository existing = baseMapper.selectById(repositoryId);
        if(existing!=null){
            return BeanCopier.copy(existing, RepositoryResponse.class);
        }else{
            //资源库信息不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
