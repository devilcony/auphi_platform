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

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.sql.JobEntrySQL;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;

import com.auphi.data.hub.domain.FTP;
import com.auphi.data.hub.domain.Hadoop;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;

/**
 * Template231, ftp到hadoop
 *
 */
public class Template231 implements Template { 

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
	public Template231(String repName, String middlePath, Date date, boolean isReload) throws Exception{
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
		int idSourceFTP = fastConfigView.getIdSourceFTP();
		FTP ftpConfig = ScheduleUtil.getFTPConfig(idSourceFTP);
		
		String fileName = null;
		String soureFileName = fastConfigView.getSourceFileName();
		if(soureFileName.indexOf(".")>-1)
			fileName = soureFileName.replaceAll("\\.","\\\\.");//change to regular exp.
		else
			fileName = soureFileName;
		
		int idHadoop = fastConfigView.getIdDestHadoop();
		Hadoop hadoopConfig = ScheduleUtil.getHadoopConfig(idHadoop);
		
		String destFilePath = fastConfigView.getDestFilePath();
		String path =null;
		String datasourceName = null;
		if(destFilePath.indexOf(":")>-1)
		{
			datasourceName = destFilePath.substring(0,destFilePath.indexOf(":"));
			path = destFilePath.substring(destFilePath.indexOf(":")+1,destFilePath.length());
		}
		else
		{
			path = destFilePath;
		}
		String destFile = "hdfs://" + hadoopConfig.getServer() + ":" + hadoopConfig.getPort() + 
			TemplateUtil.replaceVariable(path, date, isReload) + "/" + fastConfigView.getDestFileName();
		
		if(datasourceName!=null)
		{
			DatabaseMeta databaseMeta= DatabaseMeta.findDatabase(jobMeta.getDatabases(), datasourceName);
			if(databaseMeta!=null)
			{
				List<JobEntryCopy> entries = jobMeta.getJobCopies();
				for (int i=0;i<entries.size();i++)
				{
					JobEntryCopy entry = entries.get(i);
					if(entry.getName().equalsIgnoreCase("\u5efa\u8868SQL"))           //建表SQL
					{
						((JobEntrySQL)entry.getEntry()).setDatabase(databaseMeta);
					}
				}
			}else
			{
				throw new Exception("can't find the hive database:"+datasourceName);
			}
		}
		String[] arguments = new String[8];
		arguments[0] = ftpConfig.getHost_name();
		arguments[1] = String.valueOf(ftpConfig.getPort());
		arguments[2] = ftpConfig.getUsername();
		arguments[3] = ftpConfig.getPassword();
		arguments[4] = TemplateUtil.replaceVariable(fastConfigView.getSourceFilePath(), date, isReload);
		arguments[5] = TemplateUtil.replaceVariable(fileName, date, isReload);
		arguments[6] = TemplateUtil.replaceVariable(generateCreateHiveTableSQL(fastConfigView.getDestFileName(), fieldMappingList, fastConfigView.getSourceSeperator()), date, isReload);
		arguments[7] = TemplateUtil.replaceVariable(destFile, date, isReload);
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
	
	private String generateCreateHiveTableSQL(String destTableName,
			List<FieldMappingView> fieldMappingList, String sepChar) {
		StringBuffer bf = new StringBuffer();
		bf.append("CREATE TABLE IF NOT EXISTS ").append(destTableName);
		bf.append("(");
		for(int i = 0; i<fieldMappingList.size();i++)
		{
			String fieldName= fieldMappingList.get(i).getDestColumuName();
			bf.append(fieldName);    
			int type = ValueMetaInterface.TYPE_STRING;
			int length=0;
			int scale = 0;
			try{
				length = new Integer(fieldMappingList.get(i).getDestLength()).intValue();
				scale = new Integer(fieldMappingList.get(i).getDestScale()).intValue();
				type = ValueMeta.getType(fieldMappingList.get(i).getDestColumnType());
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}
			 
			bf.append(" "+TemplateUtil.getFieldDefinition(new ValueMeta(fieldName,type,length,scale)));		// public ValueMeta(String name, int type, int length, int precision)	
			
			
			if(i!=fieldMappingList.size()-1)
				bf.append(",");
		}
		bf.append(") row format delimited fields terminated by '")
		  .append(sepChar)
		  .append("';");	
		return bf.toString();
	}
	
	public static void main(String[] args)
	{
		String soureFileName = "aa.bb.cc";
		String fileName = soureFileName.replaceAll("\\.","\\\\.");
		System.out.print(fileName);
	}
}
