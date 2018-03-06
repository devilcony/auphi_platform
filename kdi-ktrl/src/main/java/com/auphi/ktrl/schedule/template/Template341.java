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
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.trans.JobEntryTrans;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;

/**
 * Template341, hadoop到数据集市
 *
 */
public class Template341 implements Template {

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
	public Template341(String repName, String middlePath, Date date, boolean isReload) throws Exception{
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
		
		List<JobEntryCopy> transEntryCopys = jobMeta.getJobCopies();
		for(JobEntryCopy jobEntryCopy : transEntryCopys){
			
			JobEntryInterface jobEntry = jobEntryCopy.getEntry();
			
			if("向接口机发送加载命令".equals(jobEntry.getName())){//set input step
				JobEntryTrans transEntry = (JobEntryTrans)jobEntry;
				TransMeta transMeta = transEntry.getTransMeta(rep, transEntry);
				StepMeta inputStep = transMeta.findStep("input");
				RowGeneratorMeta inputStepMeta = (RowGeneratorMeta)inputStep.getStepMetaInterface();
				
				String[] fields = inputStepMeta.getFieldName(); 
				String[] values = inputStepMeta.getValue();
				
				for (int i=0;i<fields.length;i++){
					if(fields[i].equals(TEMPLATE_FIELDS)){
						values[i] = fieldMappingJson;
					}else if(fields[i].equals(TEMPLATE_TABLE_NAME)){
						values[i] = fastConfigView.getDestTableName();
					}else if(fields[i].equals(TEMPLATE_CONDITIONS)){
						values[i] = fastConfigView.getSourceCondition();
					}
				}
				
				inputStepMeta.setValue(values);
			}else if("从Hive到文件".equals(jobEntry.getName())){//set database input
				JobEntryTrans transEntry = (JobEntryTrans)jobEntry;
				TransMeta transMeta = transEntry.getTransMeta(rep, transEntry);
				
				StepMeta tableInputStepMeta = transMeta.findStep("表输入");
				TableInputMeta tableInputMeta = (TableInputMeta)tableInputStepMeta.getStepMetaInterface();
				
				//bind databasemeta name
				ObjectId databaseId = new LongObjectId(fastConfigView.getIdSourceDatabase());
				DatabaseMeta databaseMeta = rep.loadDatabaseMeta(databaseId, "");
				tableInputMeta.setDatabaseMeta(databaseMeta);
				
				//create sql and bind
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT ");
				for(int i=0;i<fieldMappingList.size();i++){
					FieldMappingView fieldMappingView =  fieldMappingList.get(i);
					
					if(i == 0){
						sql.append(fieldMappingView.getSourceColumnName());
					}else {
						sql.append(", ")
						   .append(fieldMappingView.getSourceColumnName());
					}
				}
				sql.append(" FROM ")
				   .append(fastConfigView.getSourceTableName())
				   .append(" WHERE ")
				   .append(fastConfigView.getSourceCondition());
				
				tableInputMeta.setSQL(sql.toString());
			}
		}
	}

	@Override
	public boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception{
		boolean success = false;

		KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
		kettleEngine.executeJob(jobMeta, rep, null, null, monitorId, execType, remoteServer, ha);
		
		return success;
	}

}
