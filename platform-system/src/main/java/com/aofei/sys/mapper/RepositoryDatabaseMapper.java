package com.aofei.sys.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.sys.entity.RepositoryDatabase;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 资源库 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@MyBatisMapper
public interface RepositoryDatabaseMapper extends BaseMapper<RepositoryDatabase> {

    /**
     * 根据数据量连接的名字查询
     * @param connectionName
     * @return
     */
    RepositoryDatabase findByConnectionName(@Param("connectionName")String connectionName);
}
