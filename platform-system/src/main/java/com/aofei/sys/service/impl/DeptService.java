package com.aofei.sys.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.sys.entity.Dept;
import com.aofei.sys.mapper.DeptMapper;
import com.aofei.sys.model.request.DeptRequest;
import com.aofei.sys.model.response.DeptResponse;
import com.aofei.sys.service.IDeptService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 部门管理 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Service
public class DeptService extends BaseService<DeptMapper, Dept> implements IDeptService {

    @Override
    public Page<DeptResponse> getPage(Page<Dept> page, DeptRequest request) {
        List<Dept> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, DeptResponse.class);
    }

    @Override
    public List<DeptResponse> getDepts(DeptRequest request) {
        List<Dept> list = baseMapper.findList(request);
        return BeanCopier.copy(list,DeptResponse.class);
    }

    @Log(module = "部门管理",description = "新建部门信息")
    @Override
    public DeptResponse save(DeptRequest request) {
        Dept Dept = BeanCopier.copy(request, Dept.class);
        Dept.preInsert();
        super.insert(Dept);
        return BeanCopier.copy(Dept, DeptResponse.class);
    }

    @Log(module = "部门管理",description = "修改部门信息")
    @Override
    public DeptResponse update(DeptRequest request) {
        Dept existing = selectById(request.getDeptId());
        if (existing != null) {
            existing.setName(request.getName());
            existing.setOrderNum(request.getOrderNum());
            existing.setParentId(request.getParentId());
            existing.preUpdate();

            super.insertOrUpdate(existing);

            return BeanCopier.copy(existing, DeptResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "部门管理",description = "删除部门信息")
    @Override
    public int del(Long deptId) {
        Dept existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }



    @Override
    public DeptResponse get(Long deptId) {
        Dept existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, DeptResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }


}
