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
package com.auphi.data.hub.domain;

import java.util.HashMap;
import java.util.Map;

public class Constant {
	public static final String 	TRIGGERNAME = "triggerName";
	public static final String 	TRIGGERGROUP = "triggerGroup";
	public static final String STARTTIME = "startTime";
	public static final String ENDTIME = "endTime";
	public static final String REPEATCOUNT = "repeatCount";
	public static final String JOBNAME = "jobName";
	public static final String DESCRIPTION = "description";
	public static final String REPEATINTERVEL = "repeatInterval";
	
	public static final String TASKNAME = "TASKNAME";
	public static final String SECOND = "SECOND";
	public static final String MINUTE = "MINUTE";
	public static final String HOUR = "HOUR";
	public static final String VERYDAY = "VERYDAY";
	public static final String VERYMONTH = "VERYMONTH";
	public static final String VERYWEEK = "VERYWEEK";
	
	
	public static final Map<String,String> status = new HashMap<String,String>();
	
	static{
		status.put("ACQUIRED", "运行中");
		status.put("PAUSED", "暂停中");
		status.put("WAITING", "等待中");		
	}
}
