package com.aofei.mdm.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.mdm.entity.DataClean;
import com.aofei.mdm.mapper.DataCleanMapper;
import com.aofei.mdm.model.request.DataCleanRequest;
import com.aofei.mdm.model.response.DataCleanResponse;
import com.aofei.mdm.service.IDataCleanService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据映射 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class DataCleanService extends BaseService<DataCleanMapper, DataClean> implements IDataCleanService {

    @Override
    public Page<DataCleanResponse> getPage(Page<DataClean> page, DataCleanRequest request) {
        List<DataClean> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, DataCleanResponse.class);
    }

    @Override
    public List<DataCleanResponse> getDataCleans(DataCleanRequest request) {
        List<DataClean> list = baseMapper.findList(request);
        return BeanCopier.copy(list,DataCleanResponse.class);
    }

    @Log(module = "主数据管理",description = "新建数据映射信息")
    @Override
    public DataCleanResponse save(DataCleanRequest request) {
        DataClean dataClean = BeanCopier.copy(request, DataClean.class);
        dataClean.preInsert();
        super.insert(dataClean);
        return BeanCopier.copy(dataClean, DataCleanResponse.class);
    }

    @Log(module = "主数据管理",description = "修改数据映射信息")
    @Override
    public DataCleanResponse update(DataCleanRequest request) {
        DataClean existing = selectById(request.getId());
        if (existing != null) {
            existing.setModelId(request.getModelId());
            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, DataCleanResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "主数据管理",description = "删除数据映射信息")
    @Override
    public int del(Long deptId) {
        DataClean existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public DataCleanResponse get(Long deptId) {
        DataClean existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, DataCleanResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
