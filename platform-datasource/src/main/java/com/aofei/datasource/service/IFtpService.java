package com.aofei.datasource.service;

import com.aofei.datasource.entity.Ftp;
import com.aofei.datasource.model.request.FtpRequest;
import com.aofei.datasource.model.response.FtpResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * FTP管理 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
public interface IFtpService extends IService<Ftp> {

    Page<FtpResponse> getPage(Page<Ftp> page, FtpRequest request);

    List<FtpResponse> getFtps(FtpRequest request);

    FtpResponse save(FtpRequest request);

    FtpResponse update(FtpRequest request);

    int del(Long id);

    FtpResponse get(Long id);
}
