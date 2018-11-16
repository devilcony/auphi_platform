package com.aofei.translog.service.impl;

import com.aofei.translog.entity.LogTransStep;
import com.aofei.translog.mapper.LogTransStepMapper;
import com.aofei.translog.service.ILogTransStepService;
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
public class LogTransStepService extends BaseService<LogTransStepMapper, LogTransStep> implements ILogTransStepService {

}
