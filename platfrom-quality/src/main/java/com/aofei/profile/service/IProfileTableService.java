package com.aofei.profile.service;

import com.aofei.profile.entity.ProfileTable;
import com.aofei.profile.model.request.ProfileTableRequest;
import com.aofei.profile.model.response.ProfileTableResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
public interface IProfileTableService extends IService<ProfileTable> {

    ProfileTableResponse update(ProfileTableRequest request);

    ProfileTableResponse get(Long id);

    int del(Long id);

    ProfileTableResponse save(ProfileTableRequest request);

    Page<ProfileTableResponse> getPage(Page<ProfileTable> page, ProfileTableRequest request);
}
