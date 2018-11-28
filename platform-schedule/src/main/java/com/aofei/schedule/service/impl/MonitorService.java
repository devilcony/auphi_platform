package com.aofei.schedule.service.impl;

import com.aofei.base.service.impl.BaseService;
import com.aofei.schedule.entity.Monitor;
import com.aofei.schedule.mapper.MonitorMapper;
import com.aofei.schedule.model.request.MonitorRequest;
import com.aofei.schedule.model.response.MonitorResponse;
import com.aofei.schedule.service.IMonitorService;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorService extends BaseService<MonitorMapper, Monitor> implements IMonitorService {

    @Override
    public Page<MonitorResponse> getPage(Page<Monitor> page, MonitorRequest request) {
        List<Monitor> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, MonitorResponse.class);
    }
}
