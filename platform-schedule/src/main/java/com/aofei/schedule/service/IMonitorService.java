package com.aofei.schedule.service;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.schedule.entity.Monitor;
import com.aofei.schedule.model.request.MonitorRequest;
import com.aofei.schedule.model.response.DashboardResponse;
import com.aofei.schedule.model.response.MonitorResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import org.pentaho.di.core.exception.KettleException;

public interface IMonitorService extends IService<Monitor> {

    Page<MonitorResponse> getPage(Page<Monitor> page, MonitorRequest request);



    /**
     * 统计信息
     * @param user
     * @return
     */
    DashboardResponse getDashboardCount(CurrentUserResponse user) throws KettleException;
}
