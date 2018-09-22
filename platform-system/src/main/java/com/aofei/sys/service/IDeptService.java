package com.aofei.sys.service;

import com.aofei.base.service.IBaseService;
import com.aofei.sys.entity.Dept;
import com.aofei.sys.model.request.DeptRequest;
import com.aofei.sys.model.response.DeptResponse;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * <p>
 * 部门管理 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IDeptService extends IBaseService<Dept> {

    /**
     * 获取 Dept 列表
     * @param page
     * @param request
     * @return
     */
    Page<DeptResponse> getPage(Page<Dept> page, DeptRequest request);

    /**
     * 保存 Dept 信息
     * @param request
     * @return
     */
    DeptResponse save(DeptRequest request);

    /**
     * 更新 Dept 信息
     * @param request
     * @return
     */
    DeptResponse update(DeptRequest request);

    /**
     * 根据Id 查询 Dept
     * @param deptId
     * @return
     */
    DeptResponse get(Long deptId);
    /**
     * 根据Id 删除 Dept
     * @param deptId
     * @return
     */
    int del(Long deptId);

    /**
     * 查询列表
     * @param request
     * @return
     */
    List<DeptResponse> getDepts(DeptRequest request);
}
