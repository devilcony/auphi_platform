package com.aofei.schedule.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.schedule.entity.Group;
import com.aofei.schedule.i18n.Messages;
import com.aofei.schedule.mapper.GroupMapper;
import com.aofei.schedule.model.request.GroupRequest;
import com.aofei.schedule.model.response.GroupResponse;
import com.aofei.schedule.service.IGroupService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 调度分组 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Service
public class GroupService extends BaseService<GroupMapper, Group> implements IGroupService {

    @Override
    public Page<GroupResponse> getPage(Page<Group> page, GroupRequest request) {
        List<Group> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, GroupResponse.class);
    }

    @Override
    public List<GroupResponse> getGroups(GroupRequest request) {
        List<Group> list = baseMapper.findList(request);
        return BeanCopier.copy(list,GroupResponse.class);
    }

    @Log(module = "调度分组管理",description = "新建调度分组信息")
    @Override
    public GroupResponse save(GroupRequest request) {
        int count =  baseMapper.selectCount(new EntityWrapper<Group>()
                .eq("GROUP_NAME",request.getGroupName())
                .eq("ORGANIZER_ID",request.getOrganizerId())
                .eq("DEL_FLAG",Group.DEL_FLAG_NORMAL));
        if(count == 0){
            Group Group = BeanCopier.copy(request, Group.class);
            Group.preInsert();
            super.insert(Group);
            return BeanCopier.copy(Group, GroupResponse.class);
        }else{
            throw new ApplicationException(StatusCode.CONFLICT.getCode(), Messages.getString("Schedule.Error.JobGroupExist",request.getGroupName()));
        }

    }

    @Log(module = "调度分组管理",description = "修改调度分组信息")
    @Override
    public GroupResponse update(GroupRequest request) {
        Group existing = selectById(request.getGroupId());
        if (existing != null) {
            existing.setGroupName(request.getGroupName());
            existing.setDescription(request.getDescription());
            existing.preUpdate();
            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, GroupResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
    @Log(module = "调度分组管理",description = "删除调度分组信息")
    @Override
    public int del(Long deptId) {
        Group existing = selectById(deptId);
        if (existing != null) {
            super.deleteById(deptId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }



    @Override
    public GroupResponse get(Long deptId) {
        Group existing = selectById(deptId);
        if(existing!=null){
            return BeanCopier.copy(existing, GroupResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
