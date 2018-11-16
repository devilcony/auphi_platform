package com.aofei.mdm.service;

import com.aofei.mdm.entity.Table;
import com.aofei.mdm.model.request.TableRequest;
import com.aofei.mdm.model.response.TableResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 主数据表 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
public interface ITableService extends IService<Table> {

    Page<TableResponse> getPage(Page<Table> page, TableRequest request);

    List<TableResponse> getTables(TableRequest request);

    TableResponse save(TableRequest request);

    TableResponse update(TableRequest request);

    int del(Long id);

    TableResponse get(Long id);
}
