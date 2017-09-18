/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

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
package com.auphi.ktrl.schedule.dependency;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl2_3;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.system.mail.util.MailUtil;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.StringUtil;


/**
 * 具有依赖关系的作业调度，事件调度
 * 
 * 
 * **/
public class DSchedule implements Job{

	private static Logger logger = Logger.getLogger(DSchedule.class);
	// 静态共享线程池，如果改为实例独享的线程池，需要在execute中增加释放的处理代码
	static ExecutorService executor = Executors.newFixedThreadPool(10) ;
	public static List<DSchedule> runningDSchedules = new ArrayList<DSchedule>() ; 

	
	final long sleepTime = 15000L ;
	final int maxCountTaskThread = 5 ;
	final String notifyFlag = "" ;
	final boolean stopIfFailed = false ; 
	
	Map<Task,ArrayList<Task>> taskTrees ;
	private ArrayList<Task> tasksReady ;
	private ArrayList<Task> tasksRunning = new ArrayList<Task>();
	private ArrayList<Task> tasksFinished = new ArrayList<Task>();
	private ArrayList<Task> tasksFailed = new ArrayList<Task>();
	
	
	
	private void taskRunning(Task task){
		
		synchronized(this){
			int monitorId = Integer.parseInt(StringUtil.createNumberString(9)) ;
			task.setStatus(TaskStatus.running) ;
			task.setStarttime(System.currentTimeMillis()) ;
			task.setMonitorId(monitorId) ;
			tasksReady.remove(task) ;
			if(!tasksRunning.contains(task))
				tasksRunning.add(task) ;
			else
				System.err.println(task.getName()+" run twice!");
		}
	}
	
	private void taskFinished(Task task){
		
		synchronized(this){
			task.setStatus(TaskStatus.finished) ;
			task.setEndtime(System.currentTimeMillis()) ;
			tasksRunning.remove(task) ;
			if(!tasksFinished.contains(task))
				tasksFinished.add(task) ;
			else
				System.err.println(task.getName()+" run twice!");
		}
	}
	
	/**
	 * 任务失败：运行失败或因为存在失败的依赖任务导致无法运行
	 * 
	 * */
	private void taskFailed(Task task,boolean failedWhileExecute){
		synchronized(this){
			task.setStatus(TaskStatus.failed) ;
			task.setEndtime(System.currentTimeMillis()) ;
			if(failedWhileExecute)
				tasksRunning.remove(task) ;
			else
				tasksReady.remove(task) ;
			if(!tasksFailed.contains(task))
				tasksFailed.add(task) ;
		}
	}
	
	
	/**
	 * 任务已经成功执行
	 * 
	 * */
	private void taskAlreadyFinished(Task task){
		synchronized(this){
			task.setStatus(TaskStatus.finished) ;
			tasksReady.remove(task) ;
			if(!tasksFinished.contains(task))
				tasksFinished.add(task) ;
		}
	}
	
	private void init(Map<String, ArrayList<String>> trees ){
		taskTrees = new TreeMap<Task,ArrayList<Task>>() ;
		
		Map<String,Task> tempMaps =new HashMap<String,Task>() ;
		
		Set<String> keys = trees.keySet() ;
		for(String key:keys){
			Task Task = tempMaps.get(key) ;
			if(Task == null)
			{
				Task = new Task(key) ;
				tempMaps.put(key, Task) ;
			}
			
			ArrayList<String> Tasknames = trees.get(key) ;
			for(String Taskname:Tasknames){
				Task = tempMaps.get(Taskname) ;
				if(Task == null)
				{
					Task = new Task(Taskname) ;
					tempMaps.put(Taskname, Task) ;
				}
			}
		}
		
		for(String key: tempMaps.keySet()){
			ArrayList<String> dependentTasknames = trees.get(key) ;
			if(dependentTasknames == null)
				dependentTasknames = new ArrayList<String>() ;
			
			Task Task = tempMaps.get(key) ;
			ArrayList<Task> dependentTasks = Task.getDependentTasks() ;
			for(String Taskname:dependentTasknames){
				Task dependentTask = tempMaps.get(Taskname) ;
				dependentTasks.add(dependentTask) ;
			}
			
			taskTrees.put(Task, dependentTasks) ;
			
		}
	}
	
	public DSchedule(){

	}
	
	
	class TaskExec implements Runnable{
		final Task task ;
		TaskExec(final Task Task){
			this.task = Task ;
		}

		public void run()
		{
			String jobgroup = task.getName().split("\\.",2)[0] ;
			String jobname = task.getName().split("\\.",2)[1] ;
			
			int id = task.getMonitorId() ;
			
			if(DScheduleUtil.isDSchedule(jobname, jobgroup))
				executeDSchedule(jobname,jobgroup,id) ;
			else
				executeNormal(jobname,jobgroup,id) ;

			synchronized(notifyFlag){
				notifyFlag.notifyAll() ;
			}
		}
	}
	
	public static ArrayList<String> tSort(Map<String, ArrayList<String>> trees) throws Exception
	{
		TSortUtils.tSortFix(trees) ;
		return TSortUtils.tSort(trees) ;
	}
	
	/**
	 * 拓扑排序
	 * 
	 * */
	public static ArrayList<Task> taskSort(Map<Task, ArrayList<Task>> trees) throws Exception
	{
		TSortUtils.tSortFix(trees) ;
		return TSortUtils.tSort(trees) ;
	}
	
	/**
	 * 所有任务已经完成调度执行：成功结束或失败中止
	 * 
	 * **/
	private boolean isAllTaskScheduled(){
		return  this.tasksRunning.size() == 0
				&& this.tasksReady.size() == 0
				&& (this.tasksFinished.size()+this.tasksFailed.size() == this.taskTrees.size());
	}
	
	/**
	 * 判断否存在循环依赖
	 * @return 如果存在依赖，返回true，并在result保存循环的序列
	 * 
	 * **/
	public static boolean cycleExists(Map<String,ArrayList<String>> maps,StringBuffer result){
		try {
			tSort(maps) ;
		} catch (Exception e) {
			result.append(e.getMessage()) ;
			return true ;
		}
		return false ;
	}
	
	
	private TaskStatus queryTaskStatus(Task task)
	{
		int monitorid = task.getMonitorId() ;
		MonitorScheduleBean msb = MonitorUtil.getMonitorData(String.valueOf(monitorid)) ;
		String status = msb.getJobStatus() ;
		
		if(MonitorUtil.STATUS_ERROR.equals(status) && MonitorUtil.STATUS_STOPPED.equals(status))
			return TaskStatus.failed  ;
		else if (MonitorUtil.STATUS_FINISHED.equals(status))
			return TaskStatus.finished ;
		else if (MonitorUtil.STATUS_RUNNING.equals(status) || status == null)
			return TaskStatus.running ;
		
		// 其他视为异常
//		System.out.println(status) ;
		return TaskStatus.failed ;
		
	}
	
	/**
	 * 将今天已经成功执行过的调度移动至已完成队列
	 * 
	 * */
	private void removeTasksFinishedToday() throws Exception{
		
		for(int i = 0 ; i < tasksReady.size() ; i++)
		{
			Task task = tasksReady.get(i) ;
			
			String jobgroup = task.getName().split("\\.",2)[0];
			String jobname = task.getName().split("\\.",2)[1];
			
			String status = DScheduleUtil.getJobStatusToday(jobname, jobgroup) ;
			
			if(MonitorUtil.STATUS_FINISHED.equals(status))	// 已完成
			{
				taskAlreadyFinished(task) ;
				i -- ;
				continue ;
			} else if (status == null){// 无记录
				
			} else {// 其他异常状态 
				throw new Exception(task.getName()+" already failed to run today!") ;
			}
		}
	}
	
	/**
	 * 调度
	 * 
	 * **/
	private boolean schedule() throws Exception{
		
		tasksReady = taskSort(taskTrees) ;
		
		// 将今天已经执行完成的作业移动到
		removeTasksFinishedToday() ;
		
		boolean existsFailedTask = false ;
		while(true){
			
			// 等待执行的调度
			for(int i = 0 ; i < tasksReady.size() ; i++)
			{
				final Task task = tasksReady.get(i) ;
				if(tasksRunning.size() >= maxCountTaskThread)
					break ;
				if(isTaskReady(task)){
					taskRunning(task) ;
					executor.execute(new TaskExec(task)) ;
//					Future<?> ft = executor.submit(new TaskExec(task)) ;
//					task.setFuture(ft) ;
//					ft.cancel(mayInterruptIfRunning) ;
					
					i -- ;
				} else if (existsFailedDependentTask(task)){
					taskFailed(task,false) ;
					i -- ;
					System.err.println("Failed to start "+task.getName()+" because there exists failed dependent task!");
				}
				
			}
			// 正在运行的调度
			boolean hasNewFinishedJob = false ;
			for(int i = 0 ; i < tasksRunning.size() ; i++){
				Task task = tasksRunning.get(i) ;
				TaskStatus status = queryTaskStatus(task) ;
				if(status == TaskStatus.finished){
					taskFinished(task) ;
					hasNewFinishedJob = true ;
					i -- ;
				}
				else if(status == TaskStatus.failed)
				{
					taskFailed(task,true) ;
					existsFailedTask = true;
					i -- ;
					if(stopIfFailed)
						throw new Exception(task.getName()+" failed!") ;
				}
				
				//TODO:  任务超时处理
				if(task.isTimeout())
				{
				}
			}
			
			if(hasNewFinishedJob)
				continue ;
			

			if(isAllTaskScheduled())
				break ;

			synchronized(notifyFlag){
				notifyFlag.wait(sleepTime) ;
			}
			
		}
		return !existsFailedTask;
	}
	

	/**
	 * 检查执行顺序是否满足依赖
	 * 
	 * **/
	private boolean checkExecuteOrder(){
		for(Task task: tasksFinished){
			for(Task dTask:task.getDependentTasks())
			{
				if(task.getStarttime() <= dTask.getEndtime())
				{
					System.out.println(task.getName() +" : "+ dTask.getName()) ;
					return false ;
				}
			}
		}
		return true ;
	}
	
	/**
	 * 所有依赖的任务完成后，任务即处于就绪状态
	 * 
	 * **/
	private boolean isTaskReady(Task task){
		ArrayList<Task> dependentTasks = task.getDependentTasks() ;
		for(Task dTask: dependentTasks){
			if(!isTaskFinished(dTask))
				return false ;
		}
		return true ;
	}
	
	/**
	 * 存在失败的依赖任务
	 * 
	 * **/	
	private boolean existsFailedDependentTask(Task task){
		ArrayList<Task> dependentTasks = task.getDependentTasks() ;
		for(Task dTask: dependentTasks){
			if(isTaskFailed(dTask))
				return true ;
		}
		return false ;
	}
	
	private boolean isTaskFailed(Task task){
		return tasksFailed.contains(task) ;
	}
	
	private boolean isTaskFinished(Task task){
		return tasksFinished.contains(task) ;
	}
	
	
	public static void main(String []args) throws Exception{
		TreeMap<String, ArrayList<String>> mp = new TreeMap<String, ArrayList<String>>();
		String[] data, input = new String[] {
				"node01: node15 node13 node12 node01 node03 node02 node11 node14",
				"node02: node14 node02 node09 node10",
				"node03: node14 node03 node09",
				"node04: node15 node13 node09 node04 node03 node02 node14 node10",
				"node05: node05 node14 node02 node09 node10", 
				"node06: node06 node14 node09",
				"node07: node07 node14 node09",
				"node08: node14 node09",
				"node09: ",
				"node10: node14 node10", 
				"node11: node15 node14",
				"node12: node14 node12",
				"node13:" 
				};
 
		for (String str : input)
			mp.put((data = str.split(":"))[0], TSortUtils.aList(//
					data.length < 2 || data[1].trim().equals("")//
					? null : data[1].trim().split("\\s+")));
				
		DSchedule scheduler = new DSchedule() ;
		scheduler.init(mp) ;
				
		scheduler.schedule() ;
		
		System.out.println(scheduler.checkExecuteOrder()) ;
	}
	
	private static Map<String,ArrayList<String>> getDTree(String jobname,String jobgroup)
	{
		return DScheduleUtil.getDependencies(jobname, jobgroup) ;
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException 
	{
		Date start = new Date();
		JobDetail jd = arg0.getJobDetail() ;
		JobDataMap data = jd.getJobDataMap() ;
		
		int monitorid = Integer.parseInt(StringUtil.createNumberString(9));
		
		try
		{
			if(this.taskTrees == null){
				Map<String,ArrayList<String>> dtree = getDTree(jd.getName(),jd.getGroup()) ;
				init(dtree) ;
			}
			
			ScheduleBean scheduleBean = ScheduleUtil.getScheduleBeanByJobName(jd.getName(),jd.getGroup());
			MonitorUtil.addMonitorBeforeRun(monitorid, scheduleBean);
			
			if(schedule())
				MonitorUtil.updateMonitorAfterRun(monitorid, start, Arrays.toString(tasksFinished.toArray()), MonitorUtil.STATUS_FINISHED,
					0, 0, 0, 0, 0, 0, 0, 0, 0) ;
			else
				MonitorUtil.updateMonitorAfterError(monitorid,"Failed to execute " + Arrays.toString(tasksFailed.toArray()));
			
		}
		catch(Exception e){
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(monitorid, errMsg);

			String title = "[ScheduleError][" + StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "][" + jd.getFullName() + "]"; 
			String errorNoticeUserId = data.getString("errorNoticeUserId");
			String[] user_mails = UserUtil.getUserEmails(errorNoticeUserId);
			MonitorScheduleBean monitorData = MonitorUtil.getMonitorData(String.valueOf(monitorid));
			errMsg = monitorData.getLogMsg() + "\n" + errMsg;
			MailUtil.sendMail(user_mails, title, errMsg);
		}
	}
	
	/**
	 * 执行一个事件调度
	 * 
	 * */
	private static void executeDSchedule(String jobname, String jobgroup, int monitorid)
	{
		
		ScheduleBean sb = null ;
		try 
		{
			sb = ScheduleUtil.getScheduleBeanByJobName(jobname, jobgroup) ;
			if(sb == null || sb.getJobName() == null)
				MonitorUtil.addMonitorBeforeRun(monitorid, jobname, null, null, null, null);
			else
				MonitorUtil.addMonitorBeforeRun(monitorid, sb) ;
			
			if(sb == null || sb.getJobName() == null){
				throw new Exception(jobgroup+"."+jobname+" not found") ;
			}
		
			DSchedule djs = new DSchedule() ;
			Map<String,ArrayList<String>> dtree = getDTree(jobname,jobgroup) ;
			djs.init(dtree) ;
			Date start = new Date();
			
			if(djs.schedule())
				MonitorUtil.updateMonitorAfterRun(monitorid, start, Arrays.toString(djs.tasksFinished.toArray()), MonitorUtil.STATUS_FINISHED,
					0, 0, 0, 0, 0, 0, 0, 0, 0) ;
			else
				MonitorUtil.updateMonitorAfterError(monitorid, "Failed to execute " + Arrays.toString(djs.tasksFailed.toArray()));

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(monitorid, errMsg);
		}
	}
	
	/**
	 * 执行一个普通调度
	 * @param jobname 调度的名称
	 * @param jobgroup 调度的分组
	 * @param monitorid
	 * 
	 */
	private static boolean executeNormal(String jobname, String jobgroup, int monitorid){
		
		ScheduleBean sb = null ;
		if(monitorid <= 0)
			monitorid = Integer.parseInt(StringUtil.createNumberString(9));
		
		try
		{
			
			sb = ScheduleUtil.getScheduleBeanByJobName(jobname, jobgroup) ;
			
			if(sb == null || sb.getJobName() == null)
				MonitorUtil.addMonitorBeforeRun(monitorid, jobname, null, null, null, null);
			else
				MonitorUtil.addMonitorBeforeRun(monitorid, sb) ;
			
			
			if(sb == null || sb.getJobName() == null){
				throw new Exception(jobgroup+"."+jobname+" not found") ;
			}
			
			String version = sb.getVersion() ;
			String actionRef = sb.getActionRef() ;
			String actionPath = sb.getActionPath() ;
			String fileType = sb.getFileType() ;
			String repName = sb.getRepName();
			int execType = Integer.parseInt(sb.getExecType()==null?"1":sb.getExecType());
			String remoteServer = sb.getRemoteServer() ;
			String ha = sb.getHa() ;
			
			KettleEngine kettleEngine = null;
			
			//run kettle engine for different version
			if(KettleEngine.VERSION_2_3.equals(version)){
				kettleEngine = new KettleEngineImpl2_3();
			}else if(KettleEngine.VERSION_4_3.equals(version)){
				kettleEngine = new KettleEngineImpl4_3();
			}
			
			return kettleEngine.execute(repName, actionPath, actionRef, fileType, monitorid, execType, remoteServer, ha) ;
			
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String errMsg = sw.toString();
			
			logger.error(e.getMessage(), e);
			MonitorUtil.updateMonitorAfterError(monitorid, errMsg);
			
			String title = "[ScheduleError][" + StringUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm:ss") + "][" + jobgroup+"."+jobname + "]"; 
			String errorNoticeUserId = sb==null?"":sb.getErrorNoticeUserId() ;
			String[] user_mails = UserUtil.getUserEmails(errorNoticeUserId);
			MonitorScheduleBean monitorData = MonitorUtil.getMonitorData(String.valueOf(monitorid));
			errMsg = monitorData.getLogMsg() + "\n" + errMsg;
			MailUtil.sendMail(user_mails, title, errMsg);
			return false ;
		}
	}
}
