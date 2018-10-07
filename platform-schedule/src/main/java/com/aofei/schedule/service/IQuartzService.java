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
package com.aofei.schedule.service;

import com.aofei.schedule.job.QuartzExecute;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import com.aofei.schedule.model.request.ParamRequest;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;

/**
 *
 * ${DESCRIPTION}
 *
 * @auther Tony
 * @create 2018-10-02 19:44
 */
public interface IQuartzService {

    void create(GeneralScheduleRequest request, String group, Class<? extends Job> jobExecClass) throws SchedulerException;


    /**
     * 根据调度名称获取调度详细信息
     * @param jobName 调度名称
     * @param groupName 调度分组
     * @return groupName 调度详细信息
     */
    JobDetail findByName(String jobName, String groupName);
    /**
     * 根据作业名删除作业
     * @param name
     * @param group
     * @return
     */
    boolean removeJob(String name, String group) throws SchedulerException;


    /**
     * 执行调度
     * @param jobname 调度名称
     * @param params
     */
    boolean execute(String jobname, String jobgroup, ParamRequest[] params) throws SchedulerException;

    /**
     * 暂停调度
     * @param name
     * @param jobgrou
     * @return
     */
    boolean pause(String name, String jobgrou) throws SchedulerException;

    /**
     * 还原调度
     * @param name
     * @param jobgrou
     * @return
     */
    boolean resume(String name, String jobgrou) throws SchedulerException;

    /**
     * 检查调度是否存在
     * @param jobName 调度名称
     * @return boolean 调度是否存在
     */
    boolean checkJobExist(String jobName, String jobgrou);

    /**
     * 修改调度信息
     * @param request
     * @param group
     * @param quartzExecuteClass
     * @throws SchedulerException
     */
    void update(GeneralScheduleRequest request, String group, Class<QuartzExecute> quartzExecuteClass) throws SchedulerException;
}
