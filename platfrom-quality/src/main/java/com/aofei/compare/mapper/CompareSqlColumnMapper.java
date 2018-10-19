package com.aofei.compare.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.compare.entity.CompareSqlColumn;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.compare.model.request.CompareSqlRequest;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@MyBatisMapper
public interface CompareSqlColumnMapper extends BaseMapper<CompareSqlColumn> {

    List<CompareSqlColumn> findResultList(CompareSqlRequest compareSqlRequest);
}
