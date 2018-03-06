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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.steps.fuzzymatch.FuzzyMatchMeta;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;

import com.auphi.data.hub.core.util.SpringBeanLoader;
import com.auphi.data.hub.service.MyKettleDatabaseRepositoryMeta;

public class WebserviceTaskRun {

	private static final String TEMPLATE_DIR = "/Template/Template151";
	private static final String TEMPLATE_FILE_NAME = "trans";
	
	private String EMPLATE_PATH;
	private String TEMPLATE_NAME;
	
	private MyKettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta;

	public WebserviceTaskRun(){
		kettleDatabaseRepositoryMeta = (MyKettleDatabaseRepositoryMeta) SpringBeanLoader.getInstance().getSpringBean(MyKettleDatabaseRepositoryMeta.class);
	}
	
	public WebserviceTaskRun(String path,String name){
		kettleDatabaseRepositoryMeta = (MyKettleDatabaseRepositoryMeta) SpringBeanLoader.getInstance().getSpringBean(MyKettleDatabaseRepositoryMeta.class);
		this.EMPLATE_PATH = path;
		this.TEMPLATE_NAME = name;
	}

	public List<RowMetaAndData> runTransTask(String[] cmd,String userName,String password){
		List<RowMetaAndData> list = null;
		try {
				TransExecutionConfiguration config = createRemoteTransExecutionConfiguration();
			KettleDatabaseRepository repository = new KettleDatabaseRepository();
			repository.init(kettleDatabaseRepositoryMeta);
			// 连接资源库
			repository.connect(userName,password);
			RepositoryDirectoryInterface directoryInterface = repository.loadRepositoryDirectoryTree();
     		RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
			directory = directory.findDirectory(EMPLATE_PATH.toString());
			TransMeta transMeta = null;
			if (directory != null) {
				transMeta = repository.loadTransformation(TEMPLATE_NAME, directory, null, true, null); // reads
			}
			Trans trans = new Trans(transMeta);
			for(String s: cmd){
				System.out.println(s);
			}
			
			TableInputMeta mdmInputMeta = (TableInputMeta)trans.getTransMeta().findStep("mdm").getStepMetaInterface();
			mdmInputMeta.setSQL(cmd[2]);
			ObjectId conId =new LongObjectId(Long.valueOf(cmd[0]));
			DatabaseMeta database = mdmInputMeta.getParentStepMeta().getParentTransMeta().getRepository().loadDatabaseMeta(conId,null);
			mdmInputMeta.setDatabaseMeta(database);	
			
			TableInputMeta mapingInputMeta = (TableInputMeta)trans.getTransMeta().findStep("maping").getStepMetaInterface();
			mapingInputMeta.setSQL(cmd[5]);
			ObjectId conId2 =new LongObjectId(Long.valueOf(cmd[3]));
			DatabaseMeta database2 = mapingInputMeta.getParentStepMeta().getParentTransMeta().getRepository().loadDatabaseMeta(conId2,null);
			mapingInputMeta.setDatabaseMeta(database2);	
			
			
			FuzzyMatchMeta fuzzyMatchMeta = (FuzzyMatchMeta) trans.getTransMeta().findStep("fuzzyMatchMeta").getStepMetaInterface();
			fuzzyMatchMeta.setLookupField(cmd[1]);//匹配字段
			fuzzyMatchMeta.setMainStreamField(cmd[4]);//主要流字段
			String[] values = {cmd[6]};
			String[] valueNames = {cmd[6]};
			fuzzyMatchMeta.setValue(values);
			fuzzyMatchMeta.setValueName(valueNames);
			trans.execute(null);
			
			//trans.execute(cmd);//通过数组传给获取系统信息
			trans.waitUntilFinished(); 
			list =  trans.getResult().getRows();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	public Map<String,Object> runTransTask(int connID,String SQL,String slave,String transParams,String userName,String password){
		try{
			TransExecutionConfiguration config = createRemoteTransExecutionConfiguration();
			
			KettleDatabaseRepository repository = new KettleDatabaseRepository();
			repository.init(kettleDatabaseRepositoryMeta);
			// 连接资源库
			repository.connect(userName,password);
			RepositoryDirectoryInterface directoryInterface = repository.loadRepositoryDirectoryTree();
     		RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
			directory = directory.findDirectory(TEMPLATE_DIR.toString());
			TransMeta transMeta = null;
			if (directory != null) {
			transMeta = repository.loadTransformation(TEMPLATE_FILE_NAME, directory, null, true, null); // reads
			}
			//StepMetaInterface sm = transMeta.findStep("table_input");
			
			//TransMeta transMeta = repository.loadTransformation(ts[0], directoryInterface, null, false, null);
			Trans trans = new Trans(transMeta);
//			ObjectMapper mapper = new ObjectMapper();
//			if(transParams != null && !transParams.equals("")){
//				Map<String,String> params = mapper.readValue(transParams,HashMap.class);
//				config.getVariables().putAll(params);				
//			}
			TableInputMeta tableInputMeta = (TableInputMeta)trans.getTransMeta().findStep("table_input").getStepMetaInterface();
			tableInputMeta.setSQL(SQL);
			ObjectId conId =new LongObjectId(connID);
			DatabaseMeta database = tableInputMeta.getParentStepMeta().getParentTransMeta().getRepository().loadDatabaseMeta(conId,null);
			tableInputMeta.setDatabaseMeta(database);			
			
			trans.execute(new String[]{});
			trans.waitUntilFinished(); 
			
			StepInterface si = trans.getStepInterface("result", 0);
			Map<String,Object> resultMap = new HashMap<String,Object>();
			
			//xnren start
			//List<RowMetaAndData> list =  trans.getResultRows();
			
			List<RowMetaAndData> list =  trans.getResult().getRows();
			List<String> resultList = new ArrayList<String>();
			String fieldNames = "";
			for(RowMetaAndData obj : list){
				Object[] objs = obj.getData();
				String result = "";
				for(Object res : objs){
					if(res != null){
						result += res +",";
					}
				}
				if(result.endsWith(",")){
					result = result.substring(0,result.lastIndexOf(","));
				}
				resultList.add(result);
				if(fieldNames != null && !fieldNames.endsWith(",")){
					String[] fds = obj.getRowMeta().getFieldNames();
					for(String field : fds){
						fieldNames += field + ",";
					}
				}
			}
			if(fieldNames.endsWith(",")){
				fieldNames = fieldNames.substring(0,fieldNames.lastIndexOf(","));
			}
			resultMap.put("result", resultList);
			resultMap.put("field", fieldNames);
			return resultMap;
		} catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	
//	public Map<String,Object> runTransTask(String transName,String slave,String transParams){
//		try{
//			String[] ts = transName.split("@");
//			TransExecutionConfiguration config = createRemoteTransExecutionConfiguration();
//			KettleDatabaseRepository repository = new KettleDatabaseRepository();
//			repository.init(kettleDatabaseRepositoryMeta);
//			// 连接资源库
//			repository.connect("admin", "admin");
//			RepositoryDirectoryInterface directoryInterface = repository.loadRepositoryDirectoryTree();
//			TransMeta transMeta = repository.loadTransformation(ts[0], directoryInterface, null, false, null);
//			Trans trans = new Trans(transMeta);
//			ObjectMapper mapper = new ObjectMapper();
//			if(transParams != null && !transParams.equals("")){
//				Map<String,String> params = mapper.readValue(transParams,HashMap.class);
//				config.getVariables().putAll(params);				
//			}
//			trans.execute(new String[]{});
//			trans.waitUntilFinished(); 
//			StepInterface si = trans.getStepInterface("result", 0);
//			Map<String,Object> resultMap = new HashMap<String,Object>();
//			//xnren start
//			//List<RowMetaAndData> list =  trans.getResultRows();
//			List<RowMetaAndData> list =  trans.getResult().getRows();
//			List<String> resultList = new ArrayList<String>();
//			String fieldNames = "";
//			for(RowMetaAndData obj : list){
//				Object[] objs = obj.getData();
//				String result = "";
//				for(Object res : objs){
//					if(res != null){
//						result += res +",";
//					}
//				}
//				if(result.endsWith(",")){
//					result = result.substring(0,result.lastIndexOf(","));
//				}
//				resultList.add(result);
//				if(fieldNames != null && !fieldNames.endsWith(",")){
//					String[] fds = obj.getRowMeta().getFieldNames();
//					for(String field : fds){
//						fieldNames += field + ",";
//					}
//				}
//			}
//			if(fieldNames.endsWith(",")){
//				fieldNames = fieldNames.substring(0,fieldNames.lastIndexOf(","));
//			}
//			resultMap.put("result", resultList);
//			resultMap.put("field", fieldNames);
//			return resultMap;
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//		return null;
//	}
	

	/**
	 * 创建远程执行配置
	 * @return
	 */
	public static TransExecutionConfiguration createRemoteTransExecutionConfiguration() {
		TransExecutionConfiguration config = new TransExecutionConfiguration();
		config.setExecutingClustered(false);//是否是集群执行，是写true，否写false
		config.setExecutingLocally(true);//是否是本地执行，是写true，否写false
		config.setExecutingRemotely(false);//是否是远程执行，是写true，否写false
		config.setClusterPosting(false);//如果是集群执行，填写true，否则填false
		config.setClusterPreparing(false);//如果是集群执行，填写true，否则填写false
		config.setClusterStarting(false);//如果是集群执行，填写true，否则填写false
		return config;
	}
	
}
