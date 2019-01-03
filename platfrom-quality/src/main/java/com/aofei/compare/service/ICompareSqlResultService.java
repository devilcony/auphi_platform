package com.aofei.compare.service;

import com.aofei.compare.entity.CompareSqlResult;
import com.aofei.compare.model.request.CompareSqlResultRequest;
import com.aofei.compare.model.response.CompareSqlResponse;
import com.aofei.compare.model.response.CompareSqlResultResponse;
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
public interface ICompareSqlResultService extends IService<CompareSqlResult> {

    Page<CompareSqlResponse> getPage(Page<CompareSqlResult> page, CompareSqlResultRequest request);

    CompareSqlResultResponse save(CompareSqlResultRequest compareSqlResult);
}
