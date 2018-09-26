package com.aofei.sys.service;

import com.aofei.sys.entity.Repository;
import com.aofei.sys.model.request.RepositoryRequest;
import com.aofei.sys.model.response.RepositoryResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 资源库管理 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
public interface IRepositoryService extends IService<Repository> {

    /**
     * 分页查询
     * @param page
     * @param request
     * @return
     */
    Page<RepositoryResponse> getPage(Page<Repository> page, RepositoryRequest request);

    /**
     * 查询所有列表
     * @param request
     * @return
     */
    List<RepositoryResponse> getRepositorys(RepositoryRequest request);

    /**
     * 新增资源库信息
     * @param request
     * @return
     */
    RepositoryResponse save(RepositoryRequest request);

    /**
     * 更新资源库信息
     * @param request
     * @return
     */
    RepositoryResponse update(RepositoryRequest request);


    /**
     * 根据ID删除资源库信息
     * @param repositoryId
     * @return
     */
    int del(Long repositoryId);

    /**
     * 根据ID查询资源库信息
     * @param repositoryId
     * @return
     */
    RepositoryResponse get(Long repositoryId);
    /**
     * 删除
     * @param repositoryName
     * @return
     */
    int del(String repositoryName);
}
