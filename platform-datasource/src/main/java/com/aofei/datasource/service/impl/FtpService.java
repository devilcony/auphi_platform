package com.aofei.datasource.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.datasource.entity.Ftp;
import com.aofei.datasource.mapper.FtpMapper;
import com.aofei.datasource.model.request.FtpRequest;
import com.aofei.datasource.model.response.FtpResponse;
import com.aofei.datasource.service.IFtpService;
import com.aofei.log.annotation.Log;
import com.aofei.utils.BeanCopier;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * FTP管理 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Service
public class FtpService extends BaseService<FtpMapper, Ftp> implements IFtpService {

    @Override
    public Page<FtpResponse> getPage(Page<Ftp> page, FtpRequest request) {
        List<Ftp> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, FtpResponse.class);
    }

    @Override
    public List<FtpResponse> getFtps(FtpRequest request) {
        List<Ftp> list = baseMapper.findList(request);
        return BeanCopier.copy(list,FtpResponse.class);
    }

    @Override
    @Log(module = "数据源管理", description = "新建TTP服务器")
    public FtpResponse save(FtpRequest request) {
        Ftp menu = BeanCopier.copy(request, Ftp.class);
        menu.preInsert();
        super.insert(menu);
        return BeanCopier.copy(menu, FtpResponse.class);
    }

    @Override
    @Log(module = "数据源管理", description = "修改TTP服务器")
    public FtpResponse update(FtpRequest request) {
        Ftp existing = selectById(request.getFtpId());
        if (existing != null) {
            existing.setName(request.getName());
            existing.setHostName(request.getHostName());
            existing.setPort(request.getPort());
            existing.setUsername(request.getUsername());
            if(!StringUtils.isEmpty(request.getPassword())){
                existing.setPassword(request.getPassword());
            }
            existing.preUpdate();

            super.insertOrUpdate(existing);

            return BeanCopier.copy(existing, FtpResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    @Log(module = "数据源管理", description = "删除TTP服务器信息")
    public int del(Long id) {
        Ftp existing = selectById(id);
        if (existing != null) {
            super.deleteById(id);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public FtpResponse get(Long id) {

        Ftp existing = selectById(id);
        if(existing!=null){
            return BeanCopier.copy(existing, FtpResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
