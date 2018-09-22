package com.aofei.sys.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.sys.entity.Repository;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 资源库管理 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@MyBatisMapper
public interface RepositoryMapper extends BaseMapper<Repository> {

    /**
     * 根据名称查询资源库信息
     * @param repositoryName
     * @return
     */
    Repository findByRepositoryName(@Param("repositoryName")String repositoryName);

    /**
     * 执行所有去掉Default
     */
    void cancelAllDefault();
}
