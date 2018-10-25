package com.aofei.sys.service;

import com.aofei.sys.entity.RepositoryDatabase;
import com.aofei.sys.model.request.RepositoryDatabaseRequest;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 资源库 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
public interface IRepositoryDatabaseService extends IService<RepositoryDatabase> {

    /**
     * 根据ID获取资源库连接信息
     * @param repositoryConnectionId
     * @return
     */
    RepositoryDatabaseResponse get(Long repositoryConnectionId);

    RepositoryDatabaseResponse getByConnectionName(String repositoryConnectionName);

    RepositoryDatabaseResponse save(RepositoryDatabaseRequest databaseRequest);

    List<RepositoryDatabaseResponse> getRepositoryDatabases(RepositoryDatabaseRequest request);
}
