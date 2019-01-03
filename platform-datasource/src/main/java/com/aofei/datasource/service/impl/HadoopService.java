package com.aofei.datasource.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.datasource.entity.Hadoop;
import com.aofei.datasource.mapper.HadoopMapper;
import com.aofei.datasource.model.request.HadoopRequest;
import com.aofei.datasource.model.response.HadoopResponse;
import com.aofei.datasource.service.IHadoopService;
import com.aofei.log.annotation.Log;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * HADOOP管理 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class HadoopService extends BaseService<HadoopMapper, Hadoop> implements IHadoopService {


    @Override
    public Page<HadoopResponse> getPage(Page<Hadoop> page, HadoopRequest request) {
        List<Hadoop> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, HadoopResponse.class);
    }

    @Override
    public List<HadoopResponse> getHadoops(HadoopRequest request) {
        List<Hadoop> list = baseMapper.findList(request);
        return BeanCopier.copy(list,HadoopResponse.class);
    }

    @Override
    @Log(module = "数据源管理", description = "新建Hadoop服务信息")
    public HadoopResponse save(HadoopRequest request) {
        Hadoop menu = BeanCopier.copy(request, Hadoop.class);
        menu.preInsert();
        super.insert(menu);
        return BeanCopier.copy(menu, HadoopResponse.class);
    }

    @Override
    @Log(module = "数据源管理", description = "修改Hadoop服务信息")
    public HadoopResponse update(HadoopRequest request) {
        Hadoop existing = selectById(request.getId());
        if (existing != null) {
            existing.setServer(request.getServer());
            existing.setPassword(request.getPassword());
            existing.setPort(request.getPort());

            existing.preUpdate();

            super.insertOrUpdate(existing);

            return BeanCopier.copy(existing, HadoopResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    @Log(module = "数据源管理", description = "删除Hadoop服务信息")
    public int del(Long id) {
        Hadoop existing = selectById(id);
        if (existing != null) {
            super.deleteById(id);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public HadoopResponse get(Long id) {

        Hadoop existing = selectById(id);
        if(existing!=null){
            return BeanCopier.copy(existing, HadoopResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
