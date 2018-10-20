package com.aofei.profile.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;

import com.aofei.profile.entity.ProfileTable;
import com.aofei.profile.entity.ProfileTableColumn;
import com.aofei.profile.mapper.ProfileTableColumnMapper;
import com.aofei.profile.mapper.ProfileTableMapper;
import com.aofei.profile.model.request.ProfileTableColumnRequest;
import com.aofei.profile.model.request.ProfileTableRequest;
import com.aofei.profile.model.response.ProfileTableResponse;
import com.aofei.profile.service.IProfileTableService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProfileTableService extends BaseService<ProfileTableMapper, ProfileTable> implements IProfileTableService {

    @Autowired
    private ProfileTableColumnMapper profileTableColumnMapper;

    @Override
    public ProfileTableResponse update(ProfileTableRequest request) {
        ProfileTable existing = selectById(request.getProfileTableId());
        if (existing != null) {
            existing.setRepositoryName(request.getRepositoryName());
            existing.setCondition(request.getCondition());

            existing.preUpdate();

            super.insertOrUpdate(existing);

            profileTableColumnMapper.delete(new EntityWrapper<ProfileTableColumn>().eq("ID_COMPARE_SQL",existing.getProfileTableId()));

            for(ProfileTableColumnRequest columnRequest : request.getProfileTableColumns()){
                ProfileTableColumn profileTableColumn = BeanCopier.copy(columnRequest,ProfileTableColumn.class);
                profileTableColumn.preInsert();
                profileTableColumn.setProfileTableId(existing.getProfileTableId());
                profileTableColumnMapper.insert(profileTableColumn);
            }

            return BeanCopier.copy(existing, ProfileTableResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public ProfileTableResponse get(Long id) {
        ProfileTable existing = baseMapper.selectById(id);
        if(existing!=null){
            ProfileTableRequest profileTableRequest = new ProfileTableRequest();
            profileTableRequest.setProfileTableId(existing.getProfileTableId());
            List<ProfileTableColumn> profileTableColumns = profileTableColumnMapper.findList(profileTableRequest);
            existing.setProfileTableColumns(profileTableColumns);
            return BeanCopier.copy(existing, ProfileTableResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public int del(Long id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public ProfileTableResponse save(ProfileTableRequest request) {
        ProfileTable profileTable = BeanCopier.copy(request, ProfileTable.class);
        profileTable.preInsert();
        super.insert(profileTable);

        for(ProfileTableColumnRequest columnRequest : request.getProfileTableColumns()){
            ProfileTableColumn profileTableColumn = BeanCopier.copy(columnRequest,ProfileTableColumn.class);
            profileTableColumn.preInsert();
            profileTableColumn.setProfileTableId(profileTable.getProfileTableId());
            profileTableColumnMapper.insert(profileTableColumn);
        }

        return BeanCopier.copy(profileTable, ProfileTableResponse.class);
    }

    @Override
    public Page<ProfileTableResponse> getPage(Page<ProfileTable> page, ProfileTableRequest request) {
        List<ProfileTable> list = baseMapper.findList(page, request);
        for(ProfileTable profileTable : list){
            ProfileTableRequest profileTableRequest = new ProfileTableRequest();
            profileTableRequest.setProfileTableId(profileTable.getProfileTableId());
            List<ProfileTableColumn> profileTableColumns = profileTableColumnMapper.findResultList(profileTableRequest);
            profileTable.setProfileTableColumns(profileTableColumns);
        }
        page.setRecords(list);
        return convert(page, ProfileTableResponse.class);
    }
}
