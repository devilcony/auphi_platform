package com.aofei.mdm.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.mdm.entity.Table;
import com.aofei.mdm.mapper.TableMapper;
import com.aofei.mdm.model.request.TableRequest;
import com.aofei.mdm.model.response.TableResponse;
import com.aofei.mdm.service.ITableService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 主数据表 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class TableService extends BaseService<TableMapper, Table> implements ITableService {

    @Override
    public Page<TableResponse> getPage(Page<Table> page, TableRequest request) {
        List<Table> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, TableResponse.class);
    }

    @Override
    public List<TableResponse> getTables(TableRequest request) {
        List<Table> list = baseMapper.findList(request);
        return BeanCopier.copy(list,TableResponse.class);
    }

    @Log(module = "主数据管理",description = "新建主数据表信息")
    @Override
    public TableResponse save(TableRequest request) {
        Table Table = BeanCopier.copy(request, Table.class);
        Table.preInsert();
        super.insert(Table);
        return BeanCopier.copy(Table, TableResponse.class);
    }

    @Log(module = "主数据管理",description = "修改主数据表信息")
    @Override
    public TableResponse update(TableRequest request) {
        Table existing = selectById(request.getTableId());
        if (existing != null) {
            existing.setTableName(request.getTableName());
            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, TableResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "主数据管理",description = "删除主数据表信息")
    @Override
    public int del(Long deptId) {
        Table existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }


    @Override
    public TableResponse get(Long deptId) {
        Table existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, TableResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
