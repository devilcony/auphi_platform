package com.aofei.sys.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.sys.entity.RepositoryDatabaseAttribute;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 资源库链接属性 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@MyBatisMapper
public interface RepositoryDatabaseAttributeMapper extends BaseMapper<RepositoryDatabaseAttribute> {

    int deleteByDatabaseId(@Param("repositoryConnectionId") Long repositoryConnectionId);
}
