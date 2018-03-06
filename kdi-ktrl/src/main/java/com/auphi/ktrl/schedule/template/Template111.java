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
package com.auphi.ktrl.schedule.template;

import java.util.Date;
import java.util.List;

import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;

/**
 * Template111, 数据库到数据库
 *
 */
public class Template111 implements Template { 

	private String middlePath;
	private JobMeta jobMeta;
	private Repository rep;
	private Date date;
	private boolean isReload;
	
	/**
	 * init repository and jobMeta from repName and middlePath(same to templateClassName)
	 * @param middlePath
	 * @param repName
	 * @param date
	 * @param isReload
	 */
	public Template111(String repName, String middlePath, Date date, boolean isReload) throws Exception{
		this.middlePath = middlePath;
		this.date = date;
		this.isReload = isReload;
		//instantiation the repository and jobMeta by jobName and repName
		KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
		this.rep = (Repository)kettleEngine.getRep(repName);
		RepositoryDirectoryInterface directory = (RepositoryDirectoryInterface)kettleEngine.getDirectory(rep, TEMPLATE_PATH + "/" + middlePath);
		this.jobMeta = rep.loadJob(JOB_NAME, directory, null, null);
	}
	
	public String getTemplateClassName(){
		return middlePath;
	}
	
	@Override
	public void bind(String fastConfigJson, String fieldMappingJson)  throws Exception{
		FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class);
		List<FieldMappingView> fieldMappingList = JSON.parseArray(fieldMappingJson, FieldMappingView.class);
		
		String[] arguments = new String[7];
		arguments[0] = String.valueOf(fastConfigView.getIdSourceDatabase());
		arguments[1] = TemplateUtil.replaceVariable(generateSourceSQL(fastConfigView.getSourceTableName(), fieldMappingList, fastConfigView.getSourceCondition()), date, isReload);
		arguments[2] = String.valueOf(fastConfigView.getIdDestDatabase());
		arguments[3] = fastConfigView.getDestSchenaName();
		arguments[4] = TemplateUtil.replaceVariable(fastConfigView.getDestTableName(), date, isReload);
		arguments[5] = fieldMappingJson;
		arguments[6] = new Integer(fastConfigView.getLoadType()).toString();
		jobMeta.setArguments(arguments);
		
		for(int i=0;i<arguments.length;i++)
			System.out.println("arguments["+i+"]="+arguments[i]);
	}

	@Override
	public boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception{
		boolean success = false;

		KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
		kettleEngine.executeJob(jobMeta, rep, null, null, monitorId, execType, remoteServer, ha);
		
		return success;
	}
	
	private String generateSourceSQL(String sourceTableName, List<FieldMappingView> fieldMappingList, String conditions) {
		StringBuffer bf = new StringBuffer();
		bf.append("SELECT ");
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			bf.append(fieldMappingList.get(i).getSourceColumnName());    
			if(i != fieldMappingList.size()-1)
				bf.append(",");
		}
		bf.append(" FROM ");
		bf.append(sourceTableName);
		if(conditions!=null && conditions.length()>0)
			bf.append(" WHERE (").append(conditions).append(")");
		return bf.toString();
	}
}
