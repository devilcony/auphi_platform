package com.aofei.schedule.service;

import com.aofei.schedule.entity.Group;
import com.aofei.schedule.model.request.GroupRequest;
import com.aofei.schedule.model.response.GroupResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 调度分组 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
public interface IGroupService extends IService<Group> {

    /**
     * 获取 Group 列表
     * @param page
     * @param request
     * @return
     */
    Page<GroupResponse> getPage(Page<Group> page, GroupRequest request);

    /**
     * 保存 Group 信息
     * @param request
     * @return
     */
    GroupResponse save(GroupRequest request);

    /**
     * 更新 Group 信息
     * @param request
     * @return
     */
    GroupResponse update(GroupRequest request);

    /**
     * 根据Id 查询 Group
     * @param deptId
     * @return
     */
    GroupResponse get(Long deptId);
    /**
     * 根据Id 删除 Group
     * @param deptId
     * @return
     */
    int del(Long deptId);

    /**
     * 查询列表
     * @param request
     * @return
     */
    List<GroupResponse> getGroups(GroupRequest request);
}
