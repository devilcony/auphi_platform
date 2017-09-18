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

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;

/**
 * Template411, 数据集市到数据库
 *
 */
public class Template411 extends BaseTemplate4 implements Template {

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
	public Template411(String repName, String middlePath, Date date, boolean isReload) throws Exception{
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
		
		String sourceFields=generateOracelFieldsStr(fieldMappingList,"\"");
		if(fastConfigView.getSourceSeperator()==null)
		{
			System.out.println( "fastConfigView.getSourceSeperator() is null");
		}
		String[] arguments = new String[10];  
		arguments[0] = (fastConfigView.getSourceSeperator()==null || "".equals(fastConfigView.getSourceSeperator()))?"|":fastConfigView.getSourceSeperator();  //field seperater for text file
		arguments[1] = sourceFields; 
		arguments[2] = TemplateUtil.replaceVariable(fastConfigView.getSourceSchenaName() + "." + fastConfigView.getSourceTableName(), date, isReload);
		arguments[3] = TemplateUtil.replaceVariable(fastConfigView.getSourceCondition(), date, isReload);
		arguments[4] = "application/json";
		arguments[5] = fastConfigView.getDestSchenaName();
		arguments[6] = TemplateUtil.replaceVariable(fastConfigView.getDestTableName(), date, isReload);
		arguments[7] = fieldMappingJson;
		arguments[8] = String.valueOf(fastConfigView.getIdDestDatabase());
		arguments[9] = new Integer(fastConfigView.getLoadType()).toString();
		
		jobMeta.setArguments(arguments);
		
		for(int i=0;i<arguments.length;i++)
			System.out.println("arguments["+i+"]="+arguments[i]);
		
//		List<JobEntryCopy> transEntryCopys = jobMeta.getJobCopies();
//		for(JobEntryCopy jobEntryCopy : transEntryCopys){
//			
//			JobEntryInterface jobEntry = jobEntryCopy.getEntry();
//			
//			if("向接口机发送命令".equals(jobEntry.getName())){//set input step
//				JobEntryTrans transEntry = (JobEntryTrans)jobEntry;
//				TransMeta transMeta = transEntry.getTransMeta(rep, transEntry);
//				StepMeta inputStep = transMeta.findStep("input");
//				RowGeneratorMeta inputStepMeta = (RowGeneratorMeta)inputStep.getStepMetaInterface();
//				
//				String[] fields = inputStepMeta.getFieldName(); 
//				String[] values = inputStepMeta.getValue();
//				
//				for (int i=0;i<fields.length;i++){
//					if(fields[i].equals(TEMPLATE_FIELDS)){
//						values[i] = fieldMappingJson;
//					}else if(fields[i].equals(TEMPLATE_TABLE_NAME)){
//						values[i] = fastConfigView.getSourceTableName();
//					}else if(fields[i].equals(TEMPLATE_CONDITIONS)){
//						values[i] = fastConfigView.getSourceCondition();
//					}
//				}
//				
//				inputStepMeta.setValue(values);
//			}else if("load_to_target".equals(jobEntry.getName())){//set text file input and database output
//				JobEntryTrans transEntry = (JobEntryTrans)jobEntry;
//				TransMeta transMeta = transEntry.getTransMeta(rep, transEntry);
//				
//				//bind text file input
//				StepMeta textFileInputStepMeta = transMeta.findStep("文本文件输入");
//				TextFileInputMeta textFileInputMeta = (TextFileInputMeta)textFileInputStepMeta.getStepMetaInterface();
//				
//				TextFileInputField[] inputFields = new TextFileInputField[fieldMappingList.size()];
//				for(int i=0;i<fieldMappingList.size();i++){
//					TextFileInputField textFileInputField = new TextFileInputField();
//					FieldMappingView_2 fieldMappingView =  fieldMappingList.get(i);
//					
//					textFileInputField.setName(fieldMappingView.getSourceColumnName());
//					textFileInputField.setType(ValueMeta.getType(fieldMappingView.getSourceColumnType()));
//					
//					inputFields[i] = textFileInputField;
//				}
//				
//				textFileInputMeta.setInputFields(inputFields);
//				
//				//bind table output
//				StepMeta tableOutputStepMeta = transMeta.findStep("表输出");
//				TableOutputMeta tableOutputMeta = (TableOutputMeta)tableOutputStepMeta.getStepMetaInterface();
//				
//				ObjectId databaseId = new LongObjectId(fastConfigView.getIdDestDatabase());
//				tableOutputMeta.setDatabaseMeta(rep.loadDatabaseMeta(databaseId, ""));
//				tableOutputMeta.setTablename(fastConfigView.getDestTableName());
//			}
//		}
	}

	@Override
	public boolean execute(int monitorId, int execType, String remoteServer, String ha) throws Exception{
		boolean success = false;

		KettleEngineImpl4_3 kettleEngine = new KettleEngineImpl4_3();
		
		//createTable(destTableName, idDestDatabase, fieldMappingList);
		kettleEngine.executeJob(jobMeta, rep, null, null, monitorId, execType, remoteServer, ha);
		
		return success;
	}

	/**
	 * create table when table is not exist
	 * @param destTableName table name to create
	 * @param idDestDatabase datasource id
	 * @param fieldMappingList fields
	 * @throws Exception
	 */
//	private void createTable(String destTableName, int idDestDatabase, List<FieldMappingView> fieldMappingList) {
//		Database database = null;
//		try{
//			ObjectId id_datasource = new LongObjectId(idDestDatabase);
//			DatabaseMeta databaseMeta =  rep.loadDatabaseMeta(id_datasource, null);
//			database = new Database(jobMeta, databaseMeta);
//			database.connect();
//			if(!database.checkTableExists(destTableName)){
//				RowMetaInterface rowMeta = new RowMeta();
//				
//				for(int i=0;i<fieldMappingList.size();i++){
//					FieldMappingView fieldMapping = fieldMappingList.get(i);
//					int destColumnType = getTypeId(fieldMapping.getDestColumnType());
//					ValueMetaInterface valueMeta = new ValueMeta(fieldMapping.getDestColumuName(), destColumnType);
//					
//					if(fieldMapping.getDestLength()!=null && !"".equals(fieldMapping.getDestLength())){
//						valueMeta.setLength(Integer.parseInt(fieldMapping.getDestLength()));
//					}
//					
//					rowMeta.addValueMeta(valueMeta);
//				} 
//				
//				String createSQL = database.getCreateTableStatement(destTableName, rowMeta, null, false, null, true);
//			
//				database.execStatement(createSQL);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}finally {
//			if(database != null){
//				database.disconnect();
//			}
//		}
//	}
	
	/**
	 * get field type id from name
	 * @param typeName type name in kettle
	 * @return
	 */
//	private int getTypeId(String typeName){
//		int typeId = 0;
//		String[] types = ValueMetaInterface.typeCodes;
//        for (int i=0;i<types.length;i++){
//			if(typeName.equals(types[i])){
//				typeId = i;
//			}
//		}  
//        
//        return typeId;
//	}
}
