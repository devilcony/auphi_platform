package com.aofei.sys.service;

import com.aofei.sys.entity.PlatformLog;
import com.aofei.sys.model.request.PlatformLogRequest;
import com.aofei.sys.model.response.PlatformLogResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 系统日志 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-18
 */
public interface IPlatformLogService extends IService<PlatformLog> {


    /**
     * 获取 系统日志 列表
     * @param page
     * @param request
     * @return
     */
    Page<PlatformLogResponse> getPage(Page<PlatformLog> page, PlatformLogRequest request);
}
