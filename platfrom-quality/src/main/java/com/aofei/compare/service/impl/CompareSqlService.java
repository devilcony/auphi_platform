package com.aofei.compare.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.compare.entity.CompareSql;
import com.aofei.compare.entity.CompareSqlColumn;
import com.aofei.compare.mapper.CompareSqlColumnMapper;
import com.aofei.compare.mapper.CompareSqlMapper;
import com.aofei.compare.model.request.CompareSqlColumnRequest;
import com.aofei.compare.model.request.CompareSqlRequest;
import com.aofei.compare.model.response.CompareSqlResponse;
import com.aofei.compare.service.ICompareSqlService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CompareSqlService extends BaseService<CompareSqlMapper, CompareSql> implements ICompareSqlService {


    @Autowired
    CompareSqlColumnMapper compareSqlColumnMapper;

    @Override
    public int del(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public CompareSqlResponse update(CompareSqlRequest request) {

        CompareSql existing = selectById(request.getCompareSqlId());
        if (existing != null) {
            existing.setCompareName(request.getCompareName());
            existing.setCompareDesc(request.getCompareDesc());
            existing.preUpdate();

            super.insertOrUpdate(existing);

            compareSqlColumnMapper.delete(new EntityWrapper<CompareSqlColumn>().eq("ID_COMPARE_SQL",existing.getCompareSqlId()));

            for(CompareSqlColumnRequest columnRequest : request.getCompareSqlColumn()){
                CompareSqlColumn compareSqlColumn = BeanCopier.copy(columnRequest,CompareSqlColumn.class);
                compareSqlColumn.preInsert();
                compareSqlColumn.setCompareSqlId(existing.getCompareSqlId());
                compareSqlColumnMapper.insert(compareSqlColumn);
            }

            return BeanCopier.copy(existing, CompareSqlResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public CompareSqlResponse get(Long id) {

        CompareSql existing = baseMapper.selectById(id);
        if(existing!=null){
            CompareSqlRequest compareSqlRequest = new CompareSqlRequest();
            compareSqlRequest.setCompareSqlId(existing.getCompareSqlId());
            List<CompareSqlColumn> compareSqlColumns = compareSqlColumnMapper.findList(compareSqlRequest);
            existing.setCompareSqlColumn(compareSqlColumns);
            return BeanCopier.copy(existing, CompareSqlResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public CompareSqlResponse save(CompareSqlRequest request) {
        CompareSql compareSql = BeanCopier.copy(request, CompareSql.class);
        compareSql.preInsert();
        super.insert(compareSql);

        for(CompareSqlColumnRequest columnRequest : request.getCompareSqlColumn()){
            CompareSqlColumn compareSqlColumn = BeanCopier.copy(columnRequest,CompareSqlColumn.class);
            compareSqlColumn.preInsert();
            compareSqlColumn.setCompareSqlId(compareSql.getCompareSqlId());
            compareSqlColumnMapper.insert(compareSqlColumn);
        }

        return BeanCopier.copy(compareSql, CompareSqlResponse.class);
    }

    @Override
    public Page<CompareSqlResponse> getPage(Page<CompareSql> page, CompareSqlRequest request) {
        List<CompareSql> list = baseMapper.findList(page, request);
        for(CompareSql compareSql : list){
            CompareSqlRequest compareSqlRequest = new CompareSqlRequest();
            compareSqlRequest.setCompareSqlId(compareSql.getCompareSqlId());
            List<CompareSqlColumn> compareSqlColumns = compareSqlColumnMapper.findResultList(compareSqlRequest);
            compareSql.setCompareSqlColumn(compareSqlColumns);
        }
        page.setRecords(list);
        return convert(page, CompareSqlResponse.class);
    }
}
