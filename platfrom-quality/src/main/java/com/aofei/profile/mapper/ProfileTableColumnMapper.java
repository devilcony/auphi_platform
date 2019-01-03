package com.aofei.profile.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.profile.entity.ProfileTableColumn;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.profile.model.request.ProfileTableRequest;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@MyBatisMapper
public interface ProfileTableColumnMapper extends BaseMapper<ProfileTableColumn> {

    List<ProfileTableColumn> findResultList(ProfileTableRequest profileTableRequest);
}
