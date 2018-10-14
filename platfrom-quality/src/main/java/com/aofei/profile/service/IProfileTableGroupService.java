package com.aofei.profile.service;

import com.aofei.profile.entity.ProfileTableGroup;
import com.aofei.profile.model.request.ProfileTableGroupRequest;
import com.aofei.profile.model.response.ProfileTableGroupResponse;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
public interface IProfileTableGroupService extends IService<ProfileTableGroup> {

    List<ProfileTableGroupResponse> getProfileTableGroups(ProfileTableGroupRequest request);

    ProfileTableGroupResponse save(ProfileTableGroupRequest request);

    ProfileTableGroupResponse update(ProfileTableGroupRequest request);

    int del(Long id);

    ProfileTableGroupResponse get(Long id);
}
