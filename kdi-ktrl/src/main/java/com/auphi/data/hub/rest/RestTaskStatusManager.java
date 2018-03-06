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
package com.auphi.data.hub.rest;

import java.util.HashMap;
import java.util.Map;

/**
 * Rest任务执行状态管理器
 * @author zhangfeng
 *
 */
public class RestTaskStatusManager {

	private Map<String,TaskStatus> status = new HashMap<String,TaskStatus>();
	
	private static RestTaskStatusManager manager;
	
	private RestTaskStatusManager(){
		
	}
	
	
	public static RestTaskStatusManager getInstance(){
		if(manager == null){
			manager = new RestTaskStatusManager();
		}
		return manager;
	}
	
	public void setTaskStatus(String key,TaskStatus status){
		this.status.put(key, status);
	}
	
	public TaskStatus getTaskStatus(String key){
		return this.status.get(key);
	}
	
	public void removeStatus(String key){
		this.status.remove(key);
	}
}
