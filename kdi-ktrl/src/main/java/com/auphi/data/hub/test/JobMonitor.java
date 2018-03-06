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
package com.auphi.data.hub.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobMonitor implements Job{

	public JobMonitor(){super();}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int num =0;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		num++;
		System.out.println("执行我第 "+num+"次");
//		GenerateShell.createSQL("-", "", "emp", "E:\test", "");//"-", "", tableName, filePath, path
//		GenerateShell.createShell("", "E:\test");//path, filePath
//		GenerateShell.exectorShell("-", "", "emp", "E:\test", "");//colsep, fields, tableName, filePath, path
	}

}
