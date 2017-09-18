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
import java.util.ArrayList;
import java.util.concurrent.Future;

enum TaskStatus{
	ready,
	running,
	suspend,
	finished,
	failed
}

final class Task implements Comparable<Object>{
	private final String name ;	// unique name
	private final ArrayList<Task> dependentTasks ;
	private long starttime ;
	private long endtime ;
	private int monitorId  ;
	private Future<?> ft ;
	
	private TaskStatus status ;
	
	
	Task(String jobname){
		this.name = jobname ;
		this.status = TaskStatus.ready ;
		this.dependentTasks = new ArrayList<Task>() ;
	}
	
	
	public ArrayList<Task> getDependentTasks() {
		return dependentTasks;
	}

//	public String getJobname() {
//		return name;
//	}
	
	// 任务是否运行超时
	public boolean isTimeout(){
		return false ;
	}


	public int getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(int monitorId) {
		this.monitorId = monitorId;
	}

	public String getName() {
		return name;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public ArrayList<Task> getDependentJobs() {
		return dependentTasks;
	}

//	public void setDependentJobs(ArrayList<Job> dependentTasks) {
//		this.dependentJobs = dependentTasks;
//	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	@Override
	public int compareTo(Object o) {
		Task job = (Task)o ;
		return (this.name).compareTo(job.name) ;
	}
	
	public String toString(){
		return name ;
	}


	public void setFuture(Future<?> ft) {
		// TODO Auto-generated method stub
		this.ft = ft ;
	}
}