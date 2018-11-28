package com.aofei.schedule.service;

import com.aofei.schedule.entity.Monitor;
import com.aofei.schedule.model.request.MonitorRequest;
import com.aofei.schedule.model.response.MonitorResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

public interface IMonitorService extends IService<Monitor> {

    Page<MonitorResponse> getPage(Page<Monitor> page, MonitorRequest request);
}
