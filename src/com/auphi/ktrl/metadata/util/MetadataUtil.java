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
package com.auphi.ktrl.metadata.util;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.auphi.ktrl.metadata.bean.JobTransTreeNodeBean;
import com.auphi.ktrl.metadata.bean.JsFlowBean;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.metadata.bean.MetaDataSourceBean;
import com.auphi.ktrl.metadata.tree.Tree;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.util.ClassLoaderUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.FileUtil;

public class MetadataUtil 
{
	private static Logger logger = Logger.getLogger(MetadataUtil.class);
	
    private static String kettlehome = System.getProperty("KETTLE_HOME");
    private static String userhome = System.getProperty("user.home");
    public static String repositoriesPath;
    
    public static List<MetaDataSourceBean> MetaDataSourceBeanList = new ArrayList<MetaDataSourceBean>();
    public static List<MetaDataConnBean> MetaDataConnBeanList = new ArrayList<MetaDataConnBean>();
    
    public static List<MetaDataConnBean> DataSourcesBeanList = new ArrayList<MetaDataConnBean>();
    
    public static ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil();
    
    private static List<JobTransTreeNodeBean> jobAndTransTreeList = new ArrayList<JobTransTreeNodeBean>();
    
    private static String userName = Constants.get("LoginUser");
    private static String password = Constants.get("LoginPassword");
    
    public static final String FileType_PDF = "PDF";
    public static final String FileType_HTML = "HTML";
    
    public static List<String> searchResourceStore = new ArrayList<String>();
    
    public static KettleUtil kettleUtil;
    
    public static DatabaseUtil databaseUtil;
    
    public static DatabaseUtil getDatabaseUtil() {
		return databaseUtil;
	}
	public static void setDatabaseUtil(DatabaseUtil databaseUtil) {
		MetadataUtil.databaseUtil = databaseUtil;
	}
	public static KettleUtil getKettleUtil() {
		return kettleUtil;
	}
	public static void setKettleUtil(KettleUtil kettleUtil) {
		MetadataUtil.kettleUtil = kettleUtil;
	}
	public static void init() {
    	if(databaseUtil == null){
    		databaseUtil = new DatabaseUtilImpl();
    	}
		if(kettleUtil == null){
    		kettleUtil = new KettleUtilImpl();
    	}
		classLoaderUtil.loadJarPath("kettle/4.3/");
		//Thread.currentThread().setContextClassLoader(classLoaderUtil);
		try{
			Class<?> kttleEnvironmentClass = Class.forName("org.pentaho.di.core.KettleEnvironment", true, classLoaderUtil);
			Method init = kttleEnvironmentClass.getDeclaredMethod("init");
			init.invoke(kttleEnvironmentClass);	
			
//			if (ClassicEngineBoot.getInstance().isBootDone() == false){
//				LibLoaderBoot.getInstance().start();
//		        LibFontBoot.getInstance().start();
//				ClassicEngineBoot.getInstance().start();
//			}
			
			Class<?> classicEngineBootClass = Class.forName("org.pentaho.reporting.engine.classic.core.ClassicEngineBoot", true, classLoaderUtil);
			Class<?> libFontBootClass = Class.forName("org.pentaho.reporting.libraries.fonts.LibFontBoot", true, classLoaderUtil);
			Class<?> libLoaderBootClass = Class.forName("org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot", true, classLoaderUtil);
			
			Method getInstance_engineBoot = classicEngineBootClass.getDeclaredMethod("getInstance");
			Object classicEngineBoot = getInstance_engineBoot.invoke(classicEngineBootClass);
			Class<?> abstractBootClass = Class.forName("org.pentaho.reporting.libraries.base.boot.AbstractBoot", true, classLoaderUtil);
			Method isBootDone_method = abstractBootClass.getDeclaredMethod("isBootDone");
			boolean isBootDone = (Boolean)isBootDone_method.invoke(classicEngineBoot);
			if (isBootDone == false){
				Method getInstance_libLoaderBoot = libLoaderBootClass.getDeclaredMethod("getInstance");
				Object libLoaderBoot = getInstance_libLoaderBoot.invoke(libLoaderBootClass);
				Method start = abstractBootClass.getDeclaredMethod("start");
				start.invoke(libLoaderBoot);
				
				Method getInstance_libFontBoot = libFontBootClass.getDeclaredMethod("getInstance");
				Object libFontBoot = getInstance_libFontBoot.invoke(libFontBootClass);
				start.invoke(libFontBoot);
				
				start.invoke(classicEngineBoot);
			}
//			//LibLoaderBoot.getInstance().start();
//			Class<?> LibLoaderBootClazz = Class.forName("org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot", true, classLoaderUtil);
//			Method LibLoaderBootgetInstanceMethod = LibLoaderBootClazz.getMethod("getInstance");
//			Object LibLoaderBootInstance = LibLoaderBootgetInstanceMethod.invoke(null);
//			
//			Method LibLoaderBootstartMethod=LibLoaderBootClazz.getMethod("start");
//			LibLoaderBootstartMethod.invoke(LibLoaderBootInstance);
//			
//	        //LibFontBoot.getInstance().start();
//			Class<?> LibFontBootClazz = Class.forName("org.pentaho.reporting.libraries.fonts.LibFontBoot", true, classLoaderUtil);
//			Method LibFontBootgetInstanceMethod = LibFontBootClazz.getMethod("getInstance");
//			Object LibFontBootInstance = LibFontBootgetInstanceMethod.invoke(null);
//			
//			Method LibFontBootstartMethod=LibFontBootClazz.getMethod("start");
//			LibFontBootstartMethod.invoke(LibFontBootInstance);
//			
//			//ClassicEngineBoot.getInstance().start();
//			Class<?> ClassicEngineBootClazz = Class.forName("org.pentaho.reporting.libraries.fonts.LibFontBoot", true, classLoaderUtil);
//			Method ClassicEngineBootgetInstanceMethod = ClassicEngineBootClazz.getMethod("getInstance");
//			Object ClassicEngineBootInstance = ClassicEngineBootgetInstanceMethod.invoke(null);
//			
//			Method ClassicEngineBootstartMethod=ClassicEngineBootClazz.getMethod("start");
//			ClassicEngineBootstartMethod.invoke(ClassicEngineBootInstance);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
		}
		
//		if (kettlehome == null || kettlehome.length() == 0)
//			kettlehome = userhome;
//	 	repositoriesPath = kettlehome+File.separator+".kettle" + File.separator + "repositories.xml";
//	 	
//		 String[] repositoryXpaths = {"/repositories/repository/connection",
//			  						  "/repositories/repository/description"};
//		 
//		 String[] datasourcexpaths = {"/repositories/connection/name",
//						 			  "/repositories/connection/type",
//						 			  "/repositories/connection/access",
//						 			  "/repositories/connection/server",
//						 			  "/repositories/connection/database",
//						 			  "/repositories/connection/port",
//						 			  "/repositories/connection/username",
//						 			  "/repositories/connection/password"};
//
//		 List<List<String>> resourceValuesList = FileUtil.getElementValueXML(repositoriesPath, repositoryXpaths);
//		 List<List<String>> datasourceValuesList = FileUtil.getElementValueXML(repositoriesPath, datasourcexpaths);
//		 
//		 for(List<String> resource:resourceValuesList)
//		 {
//			 MetaDataSourceBean bean = new MetaDataSourceBean();
//			 bean.setConnection(resource.get(0));
//			 bean.setDescription(resource.get(1));
//			 MetaDataSourceBeanList.add(bean);
//		 }
//		 for(List<String> datasource:datasourceValuesList)
//		 {
//			 MetaDataConnBean bean = new MetaDataConnBean();
//			 bean.setName(datasource.get(0));
//			 bean.setType(datasource.get(1));
//			 bean.setAccess(datasource.get(2));
//			 bean.setServer(datasource.get(3));
//			 bean.setDatabase(datasource.get(4));
//			 bean.setPort(datasource.get(5));
//			 bean.setUsername(datasource.get(6));
//			 
//			 String[] parametersValue = new String[1];
//			 parametersValue[0] = datasource.get(7);
//			 parametersValue[0] = parametersValue[0].replace("Encrypted","").replace(" ", "");
//			 List decryption = getInstance("org.pentaho.di.core.encryption.Encr","decryptPassword",parametersValue);
//			 
//			 bean.setPassword(decryption.get(0).toString());
//			 MetaDataConnBeanList.add(bean);
//		 }
		 
		
		 
    }
    /**
     * for excute the static method
     * @param className
     * @param methodName
     * @param parametersValue
     * @return
     */
	public static List getInstance(String className, String methodName,String[] parametersValue) {
		List list = new ArrayList();;
		try {
//			ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil();
//			classLoaderUtil.loadJarPath("kettle/4.3/");

			Class clazz = Class.forName(className,true, classLoaderUtil);
			Class[] parameterTypes = new Class[parametersValue.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				parameterTypes[i] = parametersValue[i].getClass();
			}

			Method method = clazz.getMethod(methodName, parameterTypes);
			Object object = method.invoke(null, parametersValue);
			list = Arrays.asList(object);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return list;
	}	
	
	
	
	public static List<MetaDataSourceBean> getResourceList()
	{
//		List<String> resourceList = new ArrayList<String>();
//		 for(int i=0;i<resourceValuesList.size();i++)
//		 {
//			 List<String> elementsValueList = resourceValuesList.get(i);
//			 if(elementsValueList.size() == 2)
//			 {
//				 for(int j=0;j<elementsValueList.size();j++)
//				 {
//					 resourceList.add(elementsValueList.get(1));
//				 }
//			 }
//		 }
		//获取资源库列表，并获取相应的连接信息列表MetaDataConnBeanList
		MetaDataSourceBeanList = new ArrayList<MetaDataSourceBean>();
		MetaDataConnBeanList = new ArrayList<MetaDataConnBean>();
		 List<RepositoryBean> repositoryBeanList = RepositoryUtil.getAllRepositories();
		 for(RepositoryBean bean:repositoryBeanList){
			 MetaDataSourceBean mataDataSourceBean = new MetaDataSourceBean();
			 mataDataSourceBean.setConnection(bean.getRepositoryName());
			 mataDataSourceBean.setDescription(bean.getRepositoryName());
			 MetaDataSourceBeanList.add(mataDataSourceBean);
			 
			 
			 MetaDataConnBean MetaDataConnBean = new MetaDataConnBean();
			 MetaDataConnBean.setName(bean.getRepositoryName());
			 MetaDataConnBean.setType(bean.getDbType());
			 MetaDataConnBean.setAccess(bean.getDbAccess());
			 MetaDataConnBean.setServer(bean.getDbHost());
			 MetaDataConnBean.setDatabase(bean.getDbName());
			 MetaDataConnBean.setPort(bean.getDbPort());
			 MetaDataConnBean.setUsername(bean.getUserName());
			 MetaDataConnBean.setPassword(bean.getPassword());
			 
			 MetaDataConnBeanList.add(MetaDataConnBean);
			 
		 }
		return MetaDataSourceBeanList;
	}
	
	public static MetaDataConnBean getResourceBean(String resource){
		getResourceList();
		MetaDataConnBean retConnBean = null;
		for(MetaDataConnBean connBean:MetaDataConnBeanList)
		{
			if(resource.equals(connBean.getName())){
				retConnBean = connBean;
				break;
			}
		}
		return retConnBean;
	}
	/**
	 * 作业报告中点击资源库获取资源库的所有的作业及转换
	 * @param resource
	 * @return
	 */
	public static String getResourceReportTree(String resource)
	{
		StringBuffer sb = new StringBuffer();
		
		for(MetaDataConnBean connBean:MetaDataConnBeanList)
		{
			if(resource.equals(connBean.getName()))
			{
				//String password = databaseUtil.getDataSourcePassword(connBean,userName,classLoaderUtil);
				Object[] object = kettleUtil.getJobTransTree(connBean, classLoaderUtil, userName, password);
				List<JobTransTreeNodeBean> jobTransTreeNodeList = (List<JobTransTreeNodeBean>)object[0];
				List<String> dirList = (ArrayList<String>)object[1];
				jobAndTransTreeList = jobTransTreeNodeList;
				jobTransTreeNodeList = adjustTreeNode(jobTransTreeNodeList,dirList);
				
				
				Tree r = new Tree(jobTransTreeNodeList);    
				r.recursionFn(r.nodeList, new JobTransTreeNodeBean(1,0,"/","","",""));    
				sb.append(r.modifyStr(r.returnStr.toString()));
			}
		}
		
		
		
//			  sb.append("{")
//				.append("text: '目录1',").append("expanded: true, ")
//				.append("children: [")
//				.append("     { text: '作业1', leaf: true },")
//				.append("   { text: '作业2', leaf: true },")
//				.append(" { text: '目录2', expanded: true, children: [")
//				.append("    { text: '作业3', leaf: true },")
//				.append("  { text: '作业4', leaf: true}")
//				.append(" ] },           ")
//				.append(" { text: '作业5', leaf: true },")
//				.append(" { text: '作业6', leaf: true }").append("]")
//				.append(" }");
		
		return sb.toString();
	}
	
	public static List<JobTransTreeNodeBean> adjustTreeNode(List<JobTransTreeNodeBean> jobTransTreeNodeList,List<String> dirList){
		String dirFlag = "/";
		JobTransTreeNodeBean treeNodeBean = null;
		List<JobTransTreeNodeBean> treeNodeList = new ArrayList<JobTransTreeNodeBean>();
		
		treeNodeBean = new JobTransTreeNodeBean(1,0,dirFlag,"","","");
		treeNodeList.add(treeNodeBean);
		//目录
		for(int i=0;i<dirList.size();i++){
			String nodeParentPath = null;
			String folders = dirList.get(i);
			
			if(folders.equals(dirFlag)){
				continue;
			}
			nodeParentPath = getParentNode(folders,dirFlag);
			for(int folderIdx = 0;folderIdx < treeNodeList.size();folderIdx++){
				String tempNodePath = treeNodeList.get(folderIdx).getNodePath();
				int tempNodeId = treeNodeList.get(folderIdx).getId();
				if(nodeParentPath.equals(tempNodePath)){
					tempNodePath = folders.substring(folders.lastIndexOf(dirFlag)+1);
					treeNodeBean = new JobTransTreeNodeBean(i+1,tempNodeId,tempNodePath,nodeParentPath,"",tempNodePath);
					treeNodeList.add(treeNodeBean);
					break;
				}
			}
		}
		//作业与转换
		String nodePath = null;
		String fullParentPath = null;
		String parentPath = null;
		int parentId = 1;
		for(int i=0;i<jobTransTreeNodeList.size();i++){
			treeNodeBean = jobTransTreeNodeList.get(i);
			nodePath = treeNodeBean.getNodePath();
			fullParentPath = getFullParentNode(nodePath,dirFlag);
			for(int k=0;k<dirList.size();k++){
				if(dirList.get(k).equals(nodePath)){
					parentId = k+1;
					parentPath = getParentNode(nodePath,dirFlag);
					break;
				}
			}
			
			nodePath = nodePath.substring(nodePath.lastIndexOf(dirFlag)+1);
			treeNodeBean = new JobTransTreeNodeBean(treeNodeBean.getId(),parentId,nodePath,parentPath,treeNodeBean.getNodeType(),treeNodeBean.getNodeName());
			treeNodeList.add(treeNodeBean);
		}
		
		return treeNodeList;
	}
	public static String getFullParentNode(String folders,String dirFlag){
		String fullParentfolder = null;
		if(folders.equals(dirFlag)){
			fullParentfolder = dirFlag;
		}else{
			fullParentfolder = folders.substring(0,folders.lastIndexOf(dirFlag));
			if(fullParentfolder.equals("")){
				fullParentfolder = dirFlag;
			}
		}
		return fullParentfolder;
	}
	public static String getParentNode(String folders,String dirFlag){
		String parentfolder = null;
		String[] folderList = folders.split(dirFlag);
		if(folderList != null && folderList.length != 0){
			if(folderList.length >= 2){
				if(folderList[folderList.length-2].equals("")){
					parentfolder = dirFlag;
				}else{
					parentfolder = folderList[folderList.length-2];
				}
			}
		}else{
			parentfolder = dirFlag;
		}
		return parentfolder;
	}
	/**
	 * 根据选择的作业或转换生成作业报告
	 * @param resource
	 * @param selectIndex
	 * @return
	 */
	public static String getResourceReport(String resource,int selectIndex,String outPutTypeStr, String targetFilename)
	{
		StringBuffer sb = new StringBuffer();
		for(MetaDataConnBean connBean:MetaDataConnBeanList)
		{
			if(resource.equals(connBean.getName()))
			{
				String fileDir=null;
				String fileName=null;
				String fileType=null; 
				
				for(int i=0;i<jobAndTransTreeList.size();i++)
				{
					JobTransTreeNodeBean nodeBean = jobAndTransTreeList.get(i);
					if(nodeBean.getId() == selectIndex){
						fileDir=nodeBean.getNodePath();
						fileName=nodeBean.getNodeName();
						fileType=nodeBean.getNodeType();
						break;
					}
				}
				boolean isSuccess = false;
			    if(fileDir != null && fileName != null && fileType != null)
			    {
			    	//System.out.println("fileDir:"+fileDir);
			    	//System.out.println("fileName:"+fileName);
			    	//System.out.println("fileType:"+fileType);
			    	//System.out.println("outPutTypeStr:"+outPutTypeStr);
			    	//System.out.println("targetFilename:"+targetFilename);
			    	//System.out.println("connBean:"+connBean);
			    	//String password = databaseUtil.getDataSourcePassword(connBean,userName,classLoaderUtil);
			    	
					isSuccess = kettleUtil.createAutoDoc(fileDir, fileName, fileType, 
															outPutTypeStr, targetFilename, 
															connBean,userName, password, classLoaderUtil);
					
					if(outPutTypeStr.toUpperCase().equals(FileType_HTML)){
						String outputFileName = targetFilename.substring(targetFilename.lastIndexOf(File.separator)+1);
						String report = FileUtil.readFile(targetFilename);
						int styleIdx = report.indexOf("rel=\"stylesheet\" href=\"");
						String lastStyleStr = report.substring(styleIdx+23);
						String styleName = lastStyleStr.substring(0, lastStyleStr.indexOf("\""));
						
						int imgIdx = report.indexOf("src=\"");
						String lastImgStr = report.substring(imgIdx+5);
						String imgName = lastImgStr.substring(0, lastImgStr.indexOf("\""));
						
						FileUtil.deletefileOther(targetFilename, "output", ".html",outputFileName,10);
			        	FileUtil.deletefileOther(targetFilename, "picture", ".png",imgName,10);
			        	FileUtil.deletefileOther(targetFilename, "style", ".css",styleName,10);
					}else if(outPutTypeStr.toUpperCase().equals(FileType_PDF)){
						File file = new File(targetFilename);
						boolean isExists = FileUtil.findfileExists(file);
						if(isExists){
							FileUtil.deletefileOther(targetFilename, "report_", ".PDF",file.getName(),10);
						}
					}
			    }
				if(isSuccess){
					sb.append("success");
				}else{
					sb.append("fail");
				}
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 获取数据源数据
	 * @param connectionname
	 * @return
	 */
	public static String getDatasources(String connectionname)
	{
		StringBuffer sb = new StringBuffer();
		for(MetaDataConnBean connBean:MetaDataConnBeanList)
		{
			if(connectionname.equals(connBean.getName()))
			{
				//String password = databaseUtil.getDataSourcePassword(connBean,userName,classLoaderUtil);
				List<MetaDataConnBean> dataSourceList = databaseUtil.getDataSources(connBean, classLoaderUtil, userName, password);
				DataSourcesBeanList = dataSourceList;
				for(MetaDataConnBean bean:dataSourceList){
					sb.append("{'cname':'"+bean.getName()+"','id':'"+bean.getName()+"'},");
				}
			}
		}
		
		//example
		//sb.append("{'cname':'数据源1','id':'110000'},")
		//.append("{'cname':'数据源2','id':'150000'},");
		return adjustString(sb);
	}
	/**
	 * 检查是否存在数据源
	 * @param connectionname
	 * @param textValue
	 * @return
	 */
	public static String checkDatasources(String connectionname,String textValue)
	{
		String retValue = "fail";
		for(MetaDataConnBean connBean:MetaDataConnBeanList){
			//System.out.println("checkDatasources connBean.getName()="+connBean.getName());
			//System.out.println("checkDatasources connBean.getServer()="+connBean.getServer());
			if(connectionname.equals(connBean.getName())){
				//String password = databaseUtil.getDataSourcePassword(connBean,userName,classLoaderUtil);
				List<MetaDataConnBean> dataSourceList = databaseUtil.getDataSources(connBean, classLoaderUtil, userName, password);
				for(MetaDataConnBean bean:dataSourceList){
					if(bean.getName().equals(textValue)){
						retValue = "success";
						break;
					}
				}
				
			}
		}
		return retValue;
	}
	/**
	 * 获取模式名数据
	 * @param param 连接名
	 * @return
	 */
	public static String getSchemas(String resource) //throws Exception
	{
		StringBuffer sb = new StringBuffer();
		for(MetaDataConnBean connBean:DataSourcesBeanList)
		{
			if(resource.equals(connBean.getName()))
			{
				List<String> schemaList = databaseUtil.getSchemas(connBean,classLoaderUtil);
				for(String schemaName:schemaList)
				{
					sb.append("{'cname':'"+schemaName+"','id':'"+schemaName+"'},"); 
				}
				break;
			}
		}
		
//		example data 
//		if("110000".equals(param))
//		{
//		sb.append("[")
//		.append("{'cname':'schema1-1','id':'110100'},")
//		.append("{'cname':'schema1-2','id':'110200'}")
//		.append("]");
//		}
//		else if("150000".equals(param))
//		{
//			sb.append("[")
//			.append("{'cname':'schema2-1','id':'150100'},")
//			.append("{'cname':'schema2-1','id':'150200'}")
//			.append("]");
//		}

		return adjustString(sb);
	}
	/**
	 * 检查是否存在模式名
	 * @param connectionname
	 * @param textValue
	 * @return
	 */
	public static String checkSchemas(String connectionname,String textValue) //throws Exception
	{
		String returnValue = "fail";
		for(MetaDataConnBean connBean:DataSourcesBeanList)
		{
			if(connectionname.equals(connBean.getName()))
			{
				List<String> schemaList = databaseUtil.getSchemas(connBean,classLoaderUtil);
				for(String schemaName:schemaList){
					if(schemaName.equals(textValue)){
						returnValue = "success";
					}
				}
				break;
			}
		}
		return returnValue;
	}
	/**
	 * 获取表名
	 * @param resource
	 * @param schemaName
	 * @return
	 */
	public static String getTables(String resource,String schemaName)
	{
		StringBuffer sb = new StringBuffer();
		for(MetaDataConnBean connBean:DataSourcesBeanList)
		{
			if(resource.equals(connBean.getName()))
			{
				List<String> tableList = databaseUtil.getTables(connBean,classLoaderUtil, schemaName);
				for(String tableName:tableList)
				{
					sb.append("{'cname':'"+tableName+"','id':'"+tableName+"'},"); 
				}
				break;
			}
		}

//		if("110100".equals(param))
//		{
//		sb.append("[")
//		.append("{'cname':'table1-1-1','id':'110101'},")
//		.append("{'cname':'table1-1-2','id':'110202'}")
//		.append("]");
//		}
//		else if("110200".equals(param))
//		{
//			sb.append("[")
//			.append("{'cname':'table1-2-1','id':'110201'},")
//			.append("{'cname':'table1-2-2','id':'110202'}")
//			.append("]");
//		}
//		else if("150100".equals(param))
//		{
//		sb.append("[")
//		.append("{'cname':'table2-1-1','id':'110101'},")
//		.append("{'cname':'table2-1-2','id':'110202'}")
//		.append("]");
//		}
//		else if("150200".equals(param))
//		{
//			sb.append("[")
//			.append("{'cname':'table2-2-1','id':'110201'},")
//			.append("{'cname':'table2-2-2','id':'110202'}")
//			.append("]");
//		}		
		return adjustString(sb);
	}
	/**
	 * 检查是否存在表名
	 * @param connectionname
	 * @param schemaName
	 * @param textValue
	 * @return
	 */
	public static String checkTables(String connectionname,String schemaName,String textValue)
	{
		String returnValue = "fail";
		for(MetaDataConnBean connBean:DataSourcesBeanList)
		{
			if(connectionname.equals(connBean.getName())){
				List<String> tableList = databaseUtil.getTables(connBean,classLoaderUtil, schemaName);
				for(String tableName:tableList){
					if(tableName.equals(textValue)){
						returnValue = "success";
						break;
					}
				}
				break;
			}
		}
		return returnValue;
	}
	/**
	 * 获取字段名
	 * @param resource
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	public static String getFields(String resource,String schemaName,String tableName)
	{
		//System.out.println(param);
		StringBuffer sb = new StringBuffer();
		for(MetaDataConnBean connBean:DataSourcesBeanList)
		{
			if(resource.equals(connBean.getName()))
			{
				List<String> fieldList = databaseUtil.getFields(connBean, classLoaderUtil, schemaName, tableName);
				for(String fieldName:fieldList)
				{
					sb.append("{'cname':'"+fieldName+"','id':'"+fieldName+"'},"); 
				}
				break;
			}
		}
		
		
//		if("110101".equals(param))
//		{
//		sb.append("[")
//		.append("{'cname':'field1-1-1-1','id':'1101011'},")
//		.append("{'cname':'field1-1-1-2','id':'1101012'}")
//		.append("]");
//		}
//		else if("110202".equals(param))
//		{
//			sb.append("[")
//			.append("{'cname':'field1-2-1-1','id':'1102021'},")
//			.append("{'cname':'field1-2-1-2','id':'1102022'}")
//			.append("]");
//		}
		return adjustString(sb);
	}
	/**
	 * 检查是否存在字段名
	 * @param connectionname
	 * @param schemaName
	 * @param tableName
	 * @param textValue
	 * @return
	 */
	public static String checkFields(String connectionname,String schemaName,String tableName,String textValue)
	{
		String returnValue = "fail";
		for(MetaDataConnBean connBean:DataSourcesBeanList){
			if(connectionname.equals(connBean.getName())){
				List<String> fieldList = databaseUtil.getFields(connBean, classLoaderUtil, schemaName, tableName);
				for(String fieldName:fieldList){
					if(fieldName.equals(textValue)){
						returnValue = "success";
					}
				}
				break;
			}
		}
		return returnValue;
	}	
	
	public static String adjustString(StringBuffer sb)
	{
		String returnsb = "";
		if(sb.toString().length() > 0)
			returnsb = sb.toString().substring(0,sb.toString().length()-1);
		//System.out.println(returnsb);
		return "["+returnsb+"]";
	}
	/**
	 * 影响分析-影响分析按钮
	 * @param resource
	 * @return
	 */
	public static String getResourceInfluenceReportTree(String resource)
	{
//		StringBuffer sb = new StringBuffer();
//		sb.append("{")
//				.append("text: 'root',").append("expanded: true, ")
//				.append("children: [")
//				.append(" { text: '作业一', expanded: true, children: [")
//				.append("    { text: '转换A', leaf: true },")
//				.append("    { text: '转换B', leaf: true}")
//				.append(" ] },")
//				.append(" { text: '作业二', expanded: true, children: [")
//				.append("    { text: '转换A', leaf: true }")
//				.append(" ] },")
//				.append(" { text: '作业三', expanded: true, children: [")
//				.append("    { text: '转换B', leaf: true },")
//				.append("    { text: '转换C', leaf: true}")
//				.append(" ] }")				
//				.append("]")
//				.append(" }");
//		
//		return sb.toString();
		
		StringBuffer sb = new StringBuffer();
		List<JobTransTreeNodeBean> treeNodeList = new ArrayList<JobTransTreeNodeBean>();
		JobTransTreeNodeBean treeNodeBean = new JobTransTreeNodeBean(1,0,"/","","","");
		treeNodeList.add(treeNodeBean);
		treeNodeBean = new JobTransTreeNodeBean(2,1,"TEST","/"	,"","TEST");
		treeNodeList.add(treeNodeBean);
		treeNodeBean = new JobTransTreeNodeBean(3,2,"test1","TEST","","test1");
		treeNodeList.add(treeNodeBean);
		treeNodeBean = new JobTransTreeNodeBean(4,1,"testjob","/","","testjob");
		treeNodeList.add(treeNodeBean);
		treeNodeBean = new JobTransTreeNodeBean(5,1,"","/","transformation","automatic");
		treeNodeList.add(treeNodeBean);
		
		
		Tree r = new Tree(treeNodeList);    
		r.recursionFn(r.nodeList, new JobTransTreeNodeBean(1,0,"/","","",""));    
		sb.append(r.modifyStr(r.returnStr.toString()));
		return sb.toString();
	}
	/**
	 * 影响分析结果树节点的节点详细信息
	 * @param params
	 * @return
	 */
	public static String getNodeReport(String params)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("作业/转换名称： "+params+"\n")
				.append("作业/转换描述： 从表A 读取字段，并加载到Oracle 库中。\n")
				.append("创建日期：      2012-02-22\n")
				.append("最后修改日期：  2012-02-24 \n")
				.append("创建人：        admin\n")
				.append("注释：          转换的注释\n")
				.append("版本号：        1.1");
		
		return sb.toString();
	}
	public static String getResourceDescentReportTree(String resource)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("{")
				.append("text: 'root',").append("expanded: true, ")
				.append("children: [")
				.append(" { text: '作业一', expanded: true, children: [")
				.append("    { text: '转换A', leaf: true },")
				.append("    { text: '转换B', leaf: true}")
				.append(" ] },")
				.append(" { text: '作业二', expanded: true, children: [")
				.append("    { text: '转换A', leaf: true }")
				.append(" ] },")
				.append(" { text: '作业三', expanded: true, children: [")
				.append("    { text: '转换B', leaf: true },")
				.append("    { text: '转换C', leaf: true}")
				.append(" ] }")				
				.append("]")
				.append(" }");
		
		return sb.toString();
	}
	/**
	 * 根据数据整理流程图数据
	 * @param datasource
	 * @param schemas
	 * @param tables
	 * @param fields
	 * @param list
	 * @return
	 */
	public static String getJsFlow(List<JsFlowBean> list)
	{
		String noshowStyle = "isShow=\"0\" style=\"padding:0px;width:0px;height:0px;border:0px solid black\"";
		//List<JsFlowBean> list = getJsFlowBeanList();
		
		StringBuffer sb = new StringBuffer();
		sb.append("<div id=\"spanBefore\">");
		
		String beginNext = "";
		for(JsFlowBean bean:list){
			if(bean.getBegin() == -1 ){
				beginNext = beginNext + bean.getId()+",";
			}
		}
		String[] beginNexts = beginNext.split(",");
		if(beginNexts.length > 1){
			beginNext = beginNext.substring(0,beginNext.length()-1);
			sb.append("<div class=\"before\" begin=\"-1\" id=\"0\" next=\""+beginNext+"\" "+noshowStyle+"></div>");
		}
		for(JsFlowBean bean:list){
			if(beginNexts.length == 1 && bean.getBegin() == -1){
				sb.append("<div class=\"before\" begin=\"-1\" id=\""+bean.getId()+"\" next=\""+bean.getNext()+"\">"+
								bean.getJobTranName()+"<br>"+bean.getTitleName()+"<br>字段"+bean.getColumnName()+
							"</div>");
			}
			sb.append("<div class=\"before\" id=\""+bean.getId()+"\" next=\""+bean.getNext()+"\">"+
								bean.getJobTranName()+"<br>"+bean.getTitleName()+"<br>"+bean.getColumnName()+"" +
					  "</div>");
		}
		
		sb.append("</div>");
		sb.append("<div style=\"padding:0px;border:0px dotted black;\">");
		sb.append("	<div style=\"position:relative; width:0px; height:0px;\" id=\"draw\"></div>");
		sb.append("</div>");
//		sb.append("<div id=\"spanBefore\">");
//		sb.append("	<div class=\"before\" begin=\"-1\" id=\"0\" next=\"2,3\""+noshowStyle+"></div>");
//		sb.append("	<div class=\"before\" id=\"2\" next=\"4\">表输入1<br>【字段A】</div>");
//		sb.append("	<div class=\"before\" id=\"3\" next=\"4\">表输入1<br>【字段B】</div>");
//		sb.append("	<div class=\"before\" id=\"4\" next=\"5\">计算器<br>【字段C】</div>");
//		sb.append("	<div class=\"before\" id=\"5\" next=\"6\">插入/更新<br>【字段C】</div>");
//		sb.append("</div>");
//		sb.append("<div style=\"padding:30px;border:1px dotted black;\">");
//		sb.append("	<div style=\"position:relative; width:00px; height:0px;\" id=\"draw\"></div>");
//		sb.append("</div>");
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	/**
	 * 影响分析流程图数据
	 * @return
	 */
	public static List<JsFlowBean> getInfluenceJsFlowBeanList(String datasource,String schemas,String tables,String fields){
		List<JsFlowBean> returnList = new ArrayList<JsFlowBean>();
		JsFlowBean bean = new JsFlowBean();
		
		bean.setBegin(-1);
		bean.setId(1);
		bean.setNext("2,3");
		bean.setJobTranName("转换a");
		bean.setTitleName("表输入1");
		bean.setColumnName(fields);
		returnList.add(bean);
		
		bean = new JsFlowBean();
		bean.setId(2);
		bean.setNext("4");
		bean.setJobTranName("转换a");
		bean.setTitleName("表输入2");
		bean.setColumnName("B");
		returnList.add(bean);
		
		bean = new JsFlowBean();
		bean.setId(3);
		bean.setNext("4");
		bean.setJobTranName("转换a");
		bean.setTitleName("计算器");
		bean.setColumnName("C");
		returnList.add(bean);
		
		returnList.add(bean);
		
		return returnList;
	}	
	
	/**
	 * 血统分析流程图数据
	 * @return
	 */
	public static List<JsFlowBean> getJsFlowBeanList(String datasource,String schemas,String tables,String fields){
		List<JsFlowBean> returnList = new ArrayList<JsFlowBean>();
		JsFlowBean bean = new JsFlowBean();
		
		bean.setBegin(-1);
		bean.setId(1);
		bean.setNext("3");
		bean.setJobTranName("转换a");
		bean.setTitleName("表输入1");
		bean.setColumnName(fields);
		returnList.add(bean);
		
		bean = new JsFlowBean();
		bean.setBegin(-1);
		bean.setId(2);
		bean.setNext("3");
		bean.setJobTranName("转换a");
		bean.setTitleName("表输入2");
		bean.setColumnName("B");
		returnList.add(bean);
		
		bean = new JsFlowBean();
		bean.setId(3);
		bean.setNext("4");
		bean.setJobTranName("转换a");
		bean.setTitleName("计算器");
		bean.setColumnName("C");
		returnList.add(bean);
		
		bean = new JsFlowBean();
		bean.setId(4);
		bean.setNext("-1");
		bean.setJobTranName("转换a");
		bean.setTitleName("插入/更新");
		bean.setColumnName("D");
		returnList.add(bean);
		
//		bean.setBegin(-1);
//		bean.setId(1);
//		bean.setNext("2,3");
//		bean.setJobTranName("转换a");
//		bean.setTitleName("表输入1");
//		bean.setColumnName("A");
//		returnList.add(bean);
//		
//		bean = new JsFlowBean();
//		bean.setId(2);
//		bean.setNext("4");
//		bean.setJobTranName("转换a");
//		bean.setTitleName("表输入2");
//		bean.setColumnName("B");
//		returnList.add(bean);
//		
//		bean = new JsFlowBean();
//		bean.setId(3);
//		bean.setNext("4");
//		bean.setJobTranName("转换a");
//		bean.setTitleName("计算器");
//		bean.setColumnName("C");
//		returnList.add(bean);
		

		return returnList;
	}
	/**
	 * 元数据搜索
	 * @param resource
	 * @param query
	 * @param searchKey
	 * @return
	 */
	public static String queryMetadata(int start,int limit,String resource,String query,String searchKey){
		searchResourceStore = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		Map<Integer,String> dirMap = new HashMap<Integer,String>();
		int count = 0;
		try{
			String fileType = "JOB";
			if(query.indexOf("R_TRANSFORMATION") != -1){
				fileType = "TRANSFORMATION";
			}
			
			StringBuffer sb_temp = new StringBuffer();
			for(MetaDataConnBean connBean:MetaDataConnBeanList)
			{
				if(resource.equals(connBean.getName()))
				{
					conn = databaseUtil.getConnection(connBean,classLoaderUtil);
					break;
				}
			}
			if(conn != null){
				dirMap = getDirMap(conn);
				stmt = conn.createStatement();
				
				rs = stmt.executeQuery(query.replace("metadataSearchKey", " '%"+searchKey.trim()) + "%'");
				while(rs.next()){
					count++;
					int dirId = rs.getInt(1);
					String name = rs.getString(2);
					String dir = getFullDirPath(dirMap,dirId);
					if(dir.length()>1){
						dir = dir.substring(0, dir.length()-1);
					}
					if(rs.getObject(1) != null && count > start && count < start + limit)
					sb_temp.append("{" +
									"\"main_id\":\""+count+"\"," +
								    "\"post_id\":\""+dir+"\"," +
									"\"topic_title\":\""+name+"\"," +
									"\"topic_type\":\""+fileType+"\"," +
									"\"topic_resources\":\""+resource+
									"\"},");
					searchResourceStore.add(count+";"+dir+";"+name+";"+fileType+";"+resource);
				}
				sb.append("{\"totalCount\":\""+count+"\",\"topics\":[");
				if(sb_temp.length() >0){
					sb.append(sb_temp.substring(0, sb_temp.length()-1));
				}
				sb.append("]}");
			}
		}
		catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			try{
			if(conn != null)
				if(!conn.isClosed()){
					conn.close();
				}
			}catch(Exception e){
				logger.error(e.getMessage(),e);
			}
		}
		return sb.toString();
	}
	/**
	 * 获取所有目录结构
	 * @param conn
	 * @return
	 */
	public static Map<Integer,String> getDirMap(Connection conn){
		Map<Integer,String> dirMap = new HashMap<Integer,String>();
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT ID_DIRECTORY,ID_DIRECTORY_PARENT,DIRECTORY_NAME FROM R_DIRECTORY";
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				dirMap.put(rs.getInt("ID_DIRECTORY"), rs.getString("DIRECTORY_NAME")+";"+rs.getInt("ID_DIRECTORY_PARENT"));
			}
			dirMap.put(0, "/");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return dirMap;
	}
	/**
	 * 根据当前的目录id获取完整的目录路径
	 * @param dirMap
	 * @param dirId
	 * @return
	 */
	public static String getFullDirPath(Map<Integer,String> dirMap,int dirId){
		String fullDirPath = "";
		Iterator it = dirMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>)it.next();
			if(dirId == entry.getKey()){
				String value = dirMap.get(entry.getKey());
				String[] values = value.split(";");
				if(values.length == 2){
					fullDirPath = getFullDirPath(dirMap,Integer.parseInt(values[1])) + values[0] + "/";
				}else{
					fullDirPath = values[0];
				}
			}
		}
		return fullDirPath;
	}
	/**
	 * 根据搜索结果点击作业或转换项生成html报告
	 * @param fileDir
	 * @param fileName
	 * @param fileType
	 * @param outPutTypeStr
	 * @param targetFilename
	 * @param resource
	 * @return
	 */
	public static boolean createReport4Search(String fileDir,String fileName,String fileType, 
			String outPutTypeStr,String targetFilename,
			String resource){
		
		try{
			for(MetaDataConnBean connBean:MetaDataConnBeanList)
			{
				if(resource.equals(connBean.getName()))
				{
					//String password = databaseUtil.getDataSourcePassword(connBean,userName,classLoaderUtil);
					
					kettleUtil.createAutoDoc(fileDir, fileName, fileType, 
							outPutTypeStr, targetFilename, 
							connBean,userName, password, classLoaderUtil);
					break;
				}
			}
			return true;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return false;
		}
	}
	
	public static void main(String[] args)
	{
		//test1
		//System.out.println(getResourceReportTree("mysql-test"));
		String password = "admin";
        MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
        
//        connBean.setName("11Kettle4rep");
//        connBean.setType("MYSQL");
//        connBean.setAccess("Native");
//        connBean.setServer("192.168.7.11");
//		connBean.setDatabase("kettle4rep");
//		connBean.setPort("3306");
//		connBean.setUsername("root");
//		connBean.setPassword("123456");
		
		//test2
//        String fileName = "Set arguments on a transformation";
//        String fileDir = "/testjob";
//        String fileType = "JOB";
//        String outPutTypeStr = "HTML";
//        String targetFilename = "d:\\test\\ETL Documenation.html";
//		kettleUtil.createAutoDoc(fileDir, fileName, fileType, 
//				outPutTypeStr, targetFilename, 
//				connBean,userName, password, classLoaderUtil);
		//test3
		Object[] object = kettleUtil.getJobTransTree(connBean, classLoaderUtil, userName, password);
		List<JobTransTreeNodeBean> jobTransTreeNodeList = (List<JobTransTreeNodeBean>)object[0];
		List<String> dirList = (ArrayList<String>)object[1];
		
		jobTransTreeNodeList = adjustTreeNode(jobTransTreeNodeList,dirList);
		
		Tree r = new Tree(jobTransTreeNodeList);    
		r.recursionFn(r.nodeList, new JobTransTreeNodeBean(1,0,"/","","",""));    
		//System.out.println(r.modifyStr(r.returnStr.toString()));
		
		//test4
//		try{
//		List<String> schemaList = kettleUtil.getSchemas(connBean,classLoaderUtil);
//		for(String schemaName:schemaList)
//		{
//			System.out.println(schemaName); 
//		}
//		}catch(Exception e){
//			logger.error(e.getMessage(),e);
//		}
		
	}
}
