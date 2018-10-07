package com.aofei.schedule.job;


import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * 执行调度
 * @auther Tony
 * @create 2018-10-02 20:44
 */
public class QuartzExecute extends QuartzJobBean {

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)  {
        System.out.println("===========开始执行==========");
        System.out.println("=====JobDetail======"+jobExecutionContext.getJobDetail().getKey().getName()+"==========");
        System.out.println("=====Trigger======"+jobExecutionContext.getTrigger().getKey().getName()+"==========");
    }
}
