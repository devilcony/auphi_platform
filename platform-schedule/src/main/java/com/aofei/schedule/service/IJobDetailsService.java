package com.aofei.schedule.service;

import com.aofei.schedule.entity.JobDetails;
import com.aofei.schedule.model.request.JobDetailsRequest;
import com.aofei.schedule.model.response.JobDetailsResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
public interface IJobDetailsService extends IService<JobDetails> {

    /**
     * 周期调度
     * @param page
     * @param request
     * @return
     */
    Page<JobDetailsResponse> getPage(Page<JobDetails> page, JobDetailsRequest request);
}
