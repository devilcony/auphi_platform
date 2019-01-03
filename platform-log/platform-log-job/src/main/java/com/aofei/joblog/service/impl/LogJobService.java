package com.aofei.joblog.service.impl;

import com.aofei.joblog.entity.LogJob;
import com.aofei.joblog.mapper.LogJobMapper;
import com.aofei.joblog.service.ILogJobService;
import com.aofei.base.service.impl.BaseService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Service
public class LogJobService extends BaseService<LogJobMapper, LogJob> implements ILogJobService {

}
