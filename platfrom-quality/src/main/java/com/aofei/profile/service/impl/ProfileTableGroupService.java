package com.aofei.profile.service.impl;

import com.aofei.profile.entity.ProfileTableGroup;
import com.aofei.profile.mapper.ProfileTableGroupMapper;
import com.aofei.profile.model.request.ProfileTableGroupRequest;
import com.aofei.profile.model.response.ProfileTableGroupResponse;
import com.aofei.profile.service.IProfileTableGroupService;
import com.aofei.base.service.impl.BaseService;
import com.aofei.utils.BeanCopier;
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
public class ProfileTableGroupService extends BaseService<ProfileTableGroupMapper, ProfileTableGroup> implements IProfileTableGroupService {

    @Override
    public List<ProfileTableGroupResponse> getProfileTableGroups(ProfileTableGroupRequest request) {
        List<ProfileTableGroup> list = baseMapper.findList(request);
        return BeanCopier.copy(list,ProfileTableGroupResponse.class);
    }

    @Override
    public ProfileTableGroupResponse save(ProfileTableGroupRequest request) {

        return null;
    }

    @Override
    public ProfileTableGroupResponse update(ProfileTableGroupRequest request) {
        return null;
    }

    @Override
    public int del(Long id) {
        return 0;
    }

    @Override
    public ProfileTableGroupResponse get(Long id) {
        return null;
    }
}
