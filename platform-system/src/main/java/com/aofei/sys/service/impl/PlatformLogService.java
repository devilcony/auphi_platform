package com.aofei.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.aofei.base.entity.BaseEntity;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.aop.LogPoint;
import com.aofei.sys.entity.PlatformLog;
import com.aofei.sys.mapper.PlatformLogMapper;
import com.aofei.sys.model.request.PlatformLogRequest;
import com.aofei.sys.model.response.PlatformLogResponse;
import com.aofei.sys.service.IPlatformLogService;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统日志 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-18
 */
@Service
public class PlatformLogService extends BaseService<PlatformLogMapper, PlatformLog> implements IPlatformLogService, LogPoint {

    private static Logger logger = LoggerFactory.getLogger(PlatformLogService.class);

    @Override
    public Page<PlatformLogResponse> getPage(Page<PlatformLog> page, PlatformLogRequest request) {
        List<PlatformLog> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, PlatformLogResponse.class);
    }

    @Override
    public void save(CurrentUserResponse userResponse, ProceedingJoinPoint joinPoint, String methodName, String module, String description) {
        try {
            //请求的参数
            Object[] args = joinPoint.getArgs();
            String params = JSON.toJSONString(args[0]);
            PlatformLog platformLog = new PlatformLog();
            platformLog.setCreateDate(new Date());
            platformLog.setDelFlag(BaseEntity.DEL_FLAG_NORMAL);
            platformLog.setIp(StringUtils.getRemoteAddr());
            platformLog.setMethod(methodName);
            platformLog.setModule(module);
            platformLog.setOperation(description);
            platformLog.setParams(params);
            if(userResponse!=null){
                platformLog.setUsername(userResponse.getUsername());
            }
            super.insert(platformLog);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

    }
}
