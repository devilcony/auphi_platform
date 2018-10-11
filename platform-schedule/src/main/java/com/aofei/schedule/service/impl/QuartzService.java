/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.aofei.schedule.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.log.annotation.Log;
import com.aofei.schedule.i18n.Messages;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import com.aofei.schedule.model.request.ParamRequest;
import com.aofei.schedule.service.IQuartzService;
import com.aofei.schedule.util.QuartzUtil;
import com.aofei.utils.DateUtils;
import org.joda.time.DateTime;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @auther Tony
 * @create 2018-10-02 19:45
 */
@Service
public class QuartzService implements IQuartzService {

    private  Logger logger = LoggerFactory.getLogger(QuartzService.class);

    @Autowired
    private Scheduler quartzScheduler;


    /**
     * 创建调度
     * @param request
     * @param group
     * @param jobExecClass
     * @throws SchedulerException
     */
    @Log(module = "普通调度",description = "创建调度信息")
    @Override
    public  void create(GeneralScheduleRequest request, String group,  Class<? extends Job> jobExecClass) throws SchedulerException {
        String jobName = request.getJobName();

        if(!checkJobExist(jobName,group)){
            // 获取调度器
            Scheduler sched = quartzScheduler;

            // 创建一项作业
            JobDetail jobDetail = JobBuilder.newJob(jobExecClass)
                    .withIdentity(jobName, group).build();


            JobDataMap data = jobDetail.getJobDataMap();

            data.put("request", JSONObject.toJSONString(request));

            data.put("isFastConfig", false);


            data.put("background_action_name", "");
            data.put("processId", QuartzUtil.class.getName()); //$NON-NLS-1$
            data.put("background_user_name", "");
            //data.put("background_output_location", "background/" + StringUtil.createNumberString(16)); //$NON-NLS-1$
            data.put("background_submit_time", DateUtils.toYmd(DateTime.now().toDate()));

            // This tells our execution component (QuartzExecute) that we're running
            // a background job instead of
            // a standard quartz execution.
            data.put("backgroundExecution", "true"); //$NON-NLS-1$

            Trigger trigger = QuartzUtil.getTrigger(request,group);

            // 告诉调度器使用该触发器来安排作业
            sched.scheduleJob(jobDetail, trigger);

        }else{
            throw new ApplicationException(StatusCode.CONFLICT.getCode(), Messages.getString("Schedule.Error.JobNameExist",jobName));
        }

    }



    /**
     * 根据调度名称获取调度详细信息
     * @param jobName 调度名称
     * @return JobDetail 调度详细信息
     */
    @Override
    public JobDetail findByName(String jobName, String groupName){
        try {
            JobKey tk = JobKey.jobKey(jobName, groupName);
            return quartzScheduler.getJobDetail(tk);
        } catch (SchedulerException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
    }

    /**
     * 根据作业名删除作业
     * @param name
     * @param group
     * @return
     */
    @Log(module = "普通调度",description = "删除调度信息")
    @Override
    public boolean removeJob(String name, String group) throws SchedulerException {
        TriggerKey tk = TriggerKey.triggerKey(name, group);
        quartzScheduler.pauseTrigger(tk);//停止触发器  
        quartzScheduler.unscheduleJob(tk);//移除触发器
        JobKey jobKey = JobKey.jobKey(name, group);
        quartzScheduler.deleteJob(jobKey);//删除作业
        logger.info("删除作业=> [作业名称：" + name + " 作业组：" + group + "] ");
        return true;
    }




    /**
     * 执行调度
     * @param jobname 调度名称
     * @param jobgroup
     * @param params
     * @return
     */
    @Override
    public  boolean execute(String jobname, String jobgroup, ParamRequest[] params) throws SchedulerException {

        logger.info("执行作业=> [作业名称：" + jobname + " 作业组：" + jobgroup + "] ");
        JobKey jk = JobKey.jobKey(jobname,jobgroup);
        quartzScheduler.triggerJob(jk) ;
        return true;

    }

    /**
     * 暂停调度
     * @param name
     * @param jobgrou
     * @return
     */
    @Log(module = "普通调度",description = "暂停调度")
    @Override
    public  boolean pause(String name, String jobgrou) throws SchedulerException {
        JobKey jk = JobKey.jobKey(name,jobgrou);
        quartzScheduler.pauseJob(jk);
        logger.info("暂停调度=> [作业名称：" + name + " 作业组：" + jobgrou + "] ");
        return true;
    }

    /**
     * 还原调度
     * @param name
     * @param jobgrou
     * @return
     * @throws SchedulerException
     */
    @Log(module = "普通调度",description = "还原调度")
    @Override
    public  boolean resume(String name, String jobgrou) throws SchedulerException {
        JobKey jk = JobKey.jobKey(name,jobgrou);
        quartzScheduler.resumeJob(jk);

        return true;
    }

    /**
     * 检查调度是否存在
     * @param jobName 调度名称
     * @return boolean 调度是否存在
     */
    @Override
    public  boolean checkJobExist(String jobName, String jobgrou){
        try{
            JobKey jk = JobKey.jobKey(jobName,jobgrou);
            JobDetail job = quartzScheduler.getJobDetail(jk);
            return job!=null;
        }catch(Exception e){
            logger.error(e.getMessage(),e);
            return false;
        }
    }

    /**
     * 更新调度信息
     * @param request
     * @param group
     * @param quartzExecuteClass
     * @throws SchedulerException
     */
    @Log(module = "普通调度",description = "更新调度信息")
    @Override
    public void update(GeneralScheduleRequest request, String group, Class<Job> quartzExecuteClass) throws SchedulerException {
        if(checkJobExist(request.getJobName(),String.valueOf(group))){
            removeJob(request.getJobName(),String.valueOf(group));
            create(request,group,quartzExecuteClass);
        }else{
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }
}
