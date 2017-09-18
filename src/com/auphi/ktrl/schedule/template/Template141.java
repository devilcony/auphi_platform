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

import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;

/**
 * Template141, 关系库和hive 到数据集市
 *
 */
public class Template141  extends BaseTemplate4 implements Template { 

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
	public Template141(String repName, String middlePath, Date date, boolean isReload) throws Exception{
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
		String sourceSchemaName = fastConfigView.getSourceSchenaName();
		String sourceTableName = fastConfigView.getSourceTableName();
		String condition = fastConfigView.getSourceCondition();
		Integer loadType = fastConfigView.getLoadType();
//		result_sep	命令行参数 1
//		source_connection	命令行参数 2
//		sql	命令行参数 3
//		dest_schema_name	命令行参数 4
//		dest_table_name	       命令行参数 5
//		field_name	命令行参数 6
//		field_type	命令行参数 7
//		field_length	命令行参数 8
//		field_scale	命令行参数 9
		DatabaseMeta data = ((KettleDatabaseRepository)this.rep).loadDatabaseMeta(new LongObjectId(fastConfigView.getIdSourceDatabase()),"4.4");
		String databaseDesc = data.getDatabaseTypeDesc();
		
		String[] arguments = new String[10];
		arguments[0] = fastConfigView.getSourceSeperator();  //field seperater for text file
		arguments[1] = new Integer(fastConfigView.getIdSourceDatabase()).toString();
		arguments[2] = TemplateUtil.replaceVariable(generateSelectSql(fieldMappingList, sourceSchemaName, sourceTableName, condition,databaseDesc), date, isReload);
		arguments[3] = TemplateUtil.replaceVariable(fastConfigView.getDestSchenaName(), date, isReload);
		arguments[4] = TemplateUtil.replaceVariable(fastConfigView.getDestTableName(), date, isReload);
		arguments[5] = generateDestFieldNames(fieldMappingList);
		arguments[6] = generateDestFieldTypes(fieldMappingList);
		arguments[7] = generateDestFieldLengths(fieldMappingList);
		arguments[8] = generateDestFieldScales(fieldMappingList);
		arguments[9] = loadType.toString(); 
		jobMeta.setArguments(arguments);
		for(int i=0;i<arguments.length;i++)
			System.out.println("arguments["+i+"]="+arguments[i]);
	}

	private String generateSelectSql(List<FieldMappingView> fieldMappingList,String sourceSchemaName,String sourceTableName,String condition, String databaseDesc) {
		StringBuffer sql = new StringBuffer();
		String sourceFields=generateSourceFieldNames(fieldMappingList,databaseDesc);
		sql.append("SELECT ");
		sql.append(sourceFields);
		sql.append(" FROM ");
		if(sourceSchemaName!=null&& sourceSchemaName.length()>0)
		{
			sql.append(sourceTableName).append(".");
		}
		sql.append(sourceTableName);
		if(condition!=null && condition.trim().length()>0)
		{
			sql.append(" WHERE ");
			sql.append(condition);
		}
		return sql.toString();		
	}

	@Override
	public boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception{
		boolean success = false;

		KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
		kettleEngine.executeJob(jobMeta, rep, null, null, monitorId, execType, remoteServer, ha);
		
		return success;
	}
}
