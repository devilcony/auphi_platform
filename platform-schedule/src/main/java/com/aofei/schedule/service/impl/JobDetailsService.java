package com.aofei.schedule.service.impl;

import com.aofei.base.service.impl.BaseService;
import com.aofei.schedule.entity.JobDetails;
import com.aofei.schedule.mapper.JobDetailsMapper;
import com.aofei.schedule.model.request.JobDetailsRequest;
import com.aofei.schedule.model.response.JobDetailsResponse;
import com.aofei.schedule.service.IJobDetailsService;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
@Service
public class JobDetailsService extends BaseService<JobDetailsMapper, JobDetails> implements IJobDetailsService {


    @Override
    public Page<JobDetailsResponse> getPage(Page<JobDetails> page, JobDetailsRequest request) {
        List<JobDetails> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, JobDetailsResponse.class);
    }
}
