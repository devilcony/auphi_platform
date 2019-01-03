package com.aofei.translog.service.impl;

import com.aofei.translog.entity.LogTrans;
import com.aofei.translog.mapper.LogTransMapper;
import com.aofei.translog.service.ILogTransService;
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
public class LogTransService extends BaseService<LogTransMapper, LogTrans> implements ILogTransService {

}
