package com.aofei.compare.service.impl;

import com.aofei.base.service.impl.BaseService;
import com.aofei.compare.entity.CompareSqlResult;
import com.aofei.compare.mapper.CompareSqlResultMapper;
import com.aofei.compare.model.request.CompareSqlResultRequest;
import com.aofei.compare.model.response.CompareSqlResponse;
import com.aofei.compare.model.response.CompareSqlResultResponse;
import com.aofei.compare.service.ICompareSqlResultService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Service
public class CompareSqlResultService extends BaseService<CompareSqlResultMapper, CompareSqlResult> implements ICompareSqlResultService {

    @Override
    public Page<CompareSqlResponse> getPage(Page<CompareSqlResult> page, CompareSqlResultRequest request) {
        List<CompareSqlResult> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, CompareSqlResponse.class);
    }

    @Override
    public CompareSqlResultResponse save(CompareSqlResultRequest request) {
        CompareSqlResult result = BeanCopier.copy(request, CompareSqlResult.class);
        result.preInsert();
        super.insert(result);
        return BeanCopier.copy(result, CompareSqlResultResponse.class);
    }
}
