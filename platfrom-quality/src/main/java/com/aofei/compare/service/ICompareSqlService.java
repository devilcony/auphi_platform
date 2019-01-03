package com.aofei.compare.service;

import com.aofei.compare.entity.CompareSql;
import com.aofei.compare.model.request.CompareSqlRequest;
import com.aofei.compare.model.response.CompareSqlResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
public interface ICompareSqlService extends IService<CompareSql> {

    int del(Long id);

    CompareSqlResponse update(CompareSqlRequest request);

    CompareSqlResponse get(Long id);

    CompareSqlResponse save(CompareSqlRequest request);

    Page<CompareSqlResponse> getPage(Page<CompareSql> pagination, CompareSqlRequest request);
}
