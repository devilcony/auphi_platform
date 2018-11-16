package com.aofei.log.aop;

import com.aofei.base.model.response.CurrentUserResponse;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 日志切入点接口
 *
 * @author Hao
 * @create 2017-03-29
 */
public interface LogPoint {
    /**
     * @param userResponse
     * @param joinPoint
     * @param methodName    方法名称
     * @param module        模块
     * @param description   描述
     */
    void save(CurrentUserResponse userResponse, ProceedingJoinPoint joinPoint, String methodName, String module, String description);
}
