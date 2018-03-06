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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.metadata.bean.JobTransTreeNodeBean;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.util.ClassLoaderUtil;

public class KettleUtilImpl implements KettleUtil{
	private static Logger logger = Logger.getLogger(KettleUtilImpl.class);
	
	public static String dirFlag = "/";
	

	/**
	 * 获取资源库下的所有作业与转换
	 * @param connBean
	 * @param classLoaderUtil
	 * @param userName
	 * @param password
	 * @return
	 */
	public Object[] getJobTransTree(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil,String userName,String password)
	{
		List<JobTransTreeNodeBean> retList = new ArrayList<JobTransTreeNodeBean>();
		List<String> dirList = new ArrayList<String>();
		try
		{
			Class<?> databaseMetaClazz = Class.forName("org.pentaho.di.core.database.DatabaseMeta", true, classLoaderUtil);
			Constructor<?> databaseMetaConstructor = databaseMetaClazz.getConstructor(String.class, String.class, 
					String.class, String.class, String.class, String.class, String.class, String.class);
			Object databaseMetaInstance = databaseMetaConstructor.newInstance(connBean.getName(), connBean.getType(),connBean.getAccess(),connBean.getServer(),
					connBean.getDatabase(),connBean.getPort(),connBean.getUsername(),connBean.getPassword());
			//Object databaseMetaInstance = databaseMetaConstructor.newInstance("mysql", "MYSQL","Native","localhost","KDI","3306","root","");
			
			Class<?> KettleDatabaseRepositoryMetaClazz = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta", true, classLoaderUtil);
			Object KettleDatabaseRepositoryMetaInstance = KettleDatabaseRepositoryMetaClazz.newInstance();
			
	
			Method kettleDatabaseRepositoryMetaMethod = KettleDatabaseRepositoryMetaClazz.getMethod("setConnection",databaseMetaClazz);
			kettleDatabaseRepositoryMetaMethod.invoke(KettleDatabaseRepositoryMetaInstance,databaseMetaInstance);
			
			Class<?> KettleDatabaseRepositoryClazz = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepository", true, classLoaderUtil);
			Object KettleDatabaseRepositoryInstance = KettleDatabaseRepositoryClazz.newInstance();
	        //rep.init(repInfo);
			Method KettleDatabaseRepositoryInitMethod = KettleDatabaseRepositoryClazz.getMethod("init",KettleDatabaseRepositoryMetaClazz.getInterfaces());
			KettleDatabaseRepositoryInitMethod.invoke(KettleDatabaseRepositoryInstance,KettleDatabaseRepositoryMetaInstance);
			
			Method KettleDatabaseRepositoryConnectMethod = KettleDatabaseRepositoryClazz.getMethod("connect",String.class,String.class);
			KettleDatabaseRepositoryConnectMethod.invoke(KettleDatabaseRepositoryInstance,userName, password);
			
			Method loadRepositoryDirectoryTreeMethod = KettleDatabaseRepositoryClazz.getMethod("loadRepositoryDirectoryTree");
			Object directory = loadRepositoryDirectoryTreeMethod.invoke(KettleDatabaseRepositoryInstance);
			
			//Method loadRepositoryDirectoryTree = rep.getClass().getDeclaredMethod("loadRepositoryDirectoryTree");
	        //Object directory = loadRepositoryDirectoryTree.invoke(rep);// Default = root
			
	        dirList = getTreedeep(directory,dirList);
	        int leadNoid = dirList.size();
	        
	        for(int i=0;i<dirList.size();i++)
	        {
	        	String folderDir = dirList.get(i);
	        	//System.out.println("folderDir::"+folderDir);
	        	
				Method KettleDatabaseRepositoryGetRootDirectoryIDMethod = KettleDatabaseRepositoryClazz.getMethod("getRootDirectoryID");
				Object RootDirectoryID = KettleDatabaseRepositoryGetRootDirectoryIDMethod.invoke(KettleDatabaseRepositoryInstance);
				
				directory = loadRepositoryDirectoryTreeMethod.invoke(KettleDatabaseRepositoryInstance);
		        // Find the directory name if one is specified...
		        if (folderDir!=null && !"/".equals(folderDir) && !"".equals(folderDir)){
		            Method findDirectory = directory.getClass().getDeclaredMethod("findDirectory", String.class);
		            directory = findDirectory.invoke(directory, folderDir);
		        }
		        Class<?> longObjectIdClass = Class.forName("org.pentaho.di.repository.LongObjectId", true, classLoaderUtil);
	            //Method getId = longObjectIdClass.getDeclaredMethod("getId");
	            Method getObjectId = directory.getClass().getDeclaredMethod("getObjectId");
	            Object longObjectId = getObjectId.invoke(directory);
	            //Object directoryID = getId.invoke(longObjectId);
				
				Class<?> StringObjectIdClazz = Class.forName("org.pentaho.di.repository.StringObjectId", true, classLoaderUtil);
				Constructor StringObjectConstructor = StringObjectIdClazz.getConstructor(String.class);   
				Object stringObjectInstance = StringObjectConstructor.newInstance(longObjectId+"");   
				
				Class<?> ObjectIdClazz = Class.forName("org.pentaho.di.repository.ObjectId", true, classLoaderUtil);
				Method KettleDatabaseRepositorygetJobAndTransformationObjectsMethod = KettleDatabaseRepositoryClazz.getMethod("getJobAndTransformationObjects",ObjectIdClazz,boolean.class);
				List<Object> jobTransObjects = (List<Object>)KettleDatabaseRepositorygetJobAndTransformationObjectsMethod.invoke(KettleDatabaseRepositoryInstance,stringObjectInstance,false);
			
				
				String nodePath = null;
				String nodeName = null;
				String nodeType = null;
				String nodeParentPath = "";
				if(jobTransObjects != null && jobTransObjects.size() > 0)
				{
					int leadid = 0;
					for(Object jobTransObject:jobTransObjects){
						leadNoid ++;
						leadid++;
						Class<?> jobTransClazz = jobTransObject.getClass();
						
						Method getNameMethod=jobTransClazz.getMethod("getName");
						Object getNameObject = getNameMethod.invoke(jobTransObject);
						//System.out.println("Name="+getNameObject);
						
						Method getRepositoryDirectoryMethod=jobTransClazz.getMethod("getRepositoryDirectory");
						Object RepositoryDirectory = getRepositoryDirectoryMethod.invoke(jobTransObject);
						//System.out.println("Directory="+RepositoryDirectory);
						
						Class<?> RepositoryDirectoryClazz = RepositoryDirectory.getClass();
						Method getPathMethod=RepositoryDirectoryClazz.getMethod("getPath");
						Object getPathObject = getPathMethod.invoke(RepositoryDirectory);
						//System.out.println("Path="+getPathObject);
						
						//File file = new File(getPathObject.toString());
						//System.out.println("absolutePath="+file.getAbsolutePath());
						
						
						Method getObjectTypeMethod=jobTransClazz.getMethod("getObjectType");
						Object objectType = getObjectTypeMethod.invoke(jobTransObject);
						//System.out.println("Type="+objectType);
						
						nodePath = getPathObject+"";
						nodeType = objectType+"";
						nodeName = getNameObject+"";
						
						JobTransTreeNodeBean treeNodeBean = new JobTransTreeNodeBean(leadNoid,i+1,nodePath,nodeParentPath,nodeType,nodeName);
						
						retList.add(treeNodeBean);
					}
				}
			}
	        
//	        Method disconnectMethod = KettleDatabaseRepositoryClazz.getMethod("disconnect");
//	        disconnectMethod.invoke(KettleDatabaseRepositoryInstance);
	        closeConnect(KettleDatabaseRepositoryClazz,KettleDatabaseRepositoryInstance);
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
		}
		
		Object[] object = new Object[2];
		object[0] = retList;
		object[1] = dirList;
		
		return object;
	}
	/**
	 * 获取资源库目录
	 * @param directoryTree
	 * @param pathList
	 * @return
	 */
	public List<String> getTreedeep(Object directoryTree,List<String> pathList)
	{
		try
		{
			//System.out.println(directoryTree.getPath());
			Class<?> directoryTreeClazz = directoryTree.getClass();
			
			Method getPathMethod=directoryTreeClazz.getMethod("getPath");
    		Object pathObject = getPathMethod.invoke(directoryTree);
    		pathList.add(pathObject+"");
    		
			Method getNrSubdirectoriesMethod=directoryTreeClazz.getMethod("getNrSubdirectories");
			Object NrSubdirectoriesObject = getNrSubdirectoriesMethod.invoke(directoryTree);
			
			int nrSubdir = Integer.parseInt(NrSubdirectoriesObject+"");
	        for(int i=0;i<nrSubdir;i++)
	        {
	        	Method getSubdirectoryMethod=directoryTreeClazz.getMethod("getSubdirectory",int.class);
	    		Object SubdirectoriesObject = getSubdirectoryMethod.invoke(directoryTree,i);
	       	 	getTreedeep(SubdirectoriesObject,pathList);
	        }
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
		}
        return pathList;
	}
	/**
	 * 将数据源资源库中的作业及转换生成html等格式文件
	 * @param fileDir
	 * @param fileName
	 * @param fileType
	 * @param outPutTypeStr
	 * @param targetFilename
	 * @param userName
	 * @param password
	 * @param classLoaderUtil
	 */
	public synchronized boolean createAutoDoc(String fileDir,String fileName,String fileType,
									 String outPutTypeStr,String targetFilename,
									 MetaDataConnBean connBean,String userName,String password,ClassLoaderUtil classLoaderUtil)
	{
		//Thread.currentThread().setContextClassLoader(classLoaderUtil);
		boolean isSuccess = true;
		try{
		//DatabaseMeta dataMeta = new DatabaseMeta("mysql", "MYSQL","Native","localhost","KDI","3306","root","");
		Class<?> databaseMetaClazz = Class.forName("org.pentaho.di.core.database.DatabaseMeta", true, classLoaderUtil);
		Constructor<?> databaseMetaConstructor = databaseMetaClazz.getConstructor(String.class, String.class, 
				String.class, String.class, String.class, String.class, String.class, String.class);
		Object databaseMetaInstance = databaseMetaConstructor.newInstance(connBean.getName(), connBean.getType(),connBean.getAccess(),connBean.getServer(),
				connBean.getDatabase(),connBean.getPort(),connBean.getUsername(),connBean.getPassword());
		//Object databaseMetaInstance = databaseMetaConstructor.newInstance("mysql", "MYSQL","Native","localhost","KDI","3306","root","");
		
		
		//KettleDatabaseRepositoryMeta repInfo = new KettleDatabaseRepositoryMeta();  
		Class<?> KettleDatabaseRepositoryMetaClazz = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta", true, classLoaderUtil);
		Object KettleDatabaseRepositoryMetaInstance = KettleDatabaseRepositoryMetaClazz.newInstance();
		
		//repInfo.setConnection(dataMeta);
		Method kettleDatabaseRepositoryMetaMethod = KettleDatabaseRepositoryMetaClazz.getMethod("setConnection",databaseMetaClazz);
		kettleDatabaseRepositoryMetaMethod.invoke(KettleDatabaseRepositoryMetaInstance,databaseMetaInstance);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		//KettleDatabaseRepository rep = new KettleDatabaseRepository(); 
		Class<?> KettleDatabaseRepositoryClazz = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepository", true, classLoaderUtil);
		Object KettleDatabaseRepositoryInstance = KettleDatabaseRepositoryClazz.newInstance();
        //rep.init(repInfo);
		Method KettleDatabaseRepositoryInitMethod = KettleDatabaseRepositoryClazz.getMethod("init",KettleDatabaseRepositoryMetaClazz.getInterfaces());
		KettleDatabaseRepositoryInitMethod.invoke(KettleDatabaseRepositoryInstance,KettleDatabaseRepositoryMetaInstance);
		
		//rep.connect("admin", "admin");
		Method KettleDatabaseRepositoryConnectMethod = KettleDatabaseRepositoryClazz.getMethod("connect",String.class,String.class);
		KettleDatabaseRepositoryConnectMethod.invoke(KettleDatabaseRepositoryInstance,userName, password);
		
		//RepositoryDirectoryInterface directoryTree = rep.loadRepositoryDirectoryTree();
		Method loadRepositoryDirectoryTreeMethod = KettleDatabaseRepositoryClazz.getMethod("loadRepositoryDirectoryTree");
		Object RepositoryDirectoryTree = loadRepositoryDirectoryTreeMethod.invoke(KettleDatabaseRepositoryInstance);
		//RepositoryDirectoryInterface directory = directoryTree.findDirectory(fileDir);
		Class<?> RepositoryDirectoryClazz = RepositoryDirectoryTree.getClass();
		Method findDirectoryMethod=RepositoryDirectoryClazz.getMethod("findDirectory",String.class);
		Object directoryObject = findDirectoryMethod.invoke(RepositoryDirectoryTree,fileDir);
		
	     //RepositoryObjectType objectType = null;
	     //if(fileType.equals("TRANSFORMATION"))
	     //{
	     //	 objectType = RepositoryObjectType.TRANSFORMATION;
	     //}
	     //else if(fileType.equals("JOB"))
	     //{
	     //	 objectType = RepositoryObjectType.JOB;
	     //}
		Object objectType = null;
		Class<?> RepositoryObjectTypeClazz = Class.forName("org.pentaho.di.repository.RepositoryObjectType", true, classLoaderUtil);
		if(RepositoryObjectTypeClazz.isEnum())
		{
			for(Object obj:RepositoryObjectTypeClazz.getEnumConstants()){
				//System.out.println(((Enum)obj).name());
				if(((Enum)obj).name().equalsIgnoreCase(fileType)){
					objectType = obj;
					break;
				}
			}
		}
		 //TransformationInformation.init(rep);
		Class<?> TransformationInformationClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.TransformationInformation", true, classLoaderUtil);
		Method transformationInfInitMethod = TransformationInformationClazz.getMethod("init", KettleDatabaseRepositoryClazz.getInterfaces());
		transformationInfInitMethod.invoke(null, KettleDatabaseRepositoryInstance);
	     //JobInformation.init(rep);
		Class<?> JobInformationClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.JobInformation", true, classLoaderUtil);
		Method JobInformationInitMethod = JobInformationClazz.getMethod("init", KettleDatabaseRepositoryClazz.getInterfaces());
		JobInformationInitMethod.invoke(null, KettleDatabaseRepositoryInstance);
		
		//ReportSubjectLocation location = new ReportSubjectLocation(null, directory, fileName, objectType);
		//List<ReportSubjectLocation> locationList = new ArrayList<ReportSubjectLocation>();
		//locationList.add(location);
		Class<?> RepositoryDirectoryInterfaceClazz = Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil);
		Class<?> ReportSubjectLocationClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.ReportSubjectLocation", true, classLoaderUtil);
		Constructor<?> ReportSubjectLocationConstructor = ReportSubjectLocationClazz.getConstructor(String.class,RepositoryDirectoryInterfaceClazz,String.class,RepositoryObjectTypeClazz);
		Object ReportSubjectLocationInstance = ReportSubjectLocationConstructor.newInstance(null,directoryObject,fileName,objectType);
		List<Object> locationList = new ArrayList<Object>();
		locationList.add(ReportSubjectLocationInstance);
		
         //AutoDocMeta meta = new AutoDocMeta();
		Class<?> AutoDocMetaClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.AutoDocMeta", true, classLoaderUtil);
		Object AutoDocMetaClazzInstance = AutoDocMetaClazz.newInstance();
        //meta.setDefault();
		Method setDefaultMethod = AutoDocMetaClazz.getMethod("setDefault");
		setDefaultMethod.invoke(AutoDocMetaClazzInstance);
        //meta.setOutputType(OutputType.HTML);
		Object outputTypeObject=null;
		
		Class<?> OutputTypeEnum = Class.forName("org.pentaho.di.trans.steps.autodoc.KettleReportBuilder$OutputType", true, classLoaderUtil);
		if(OutputTypeEnum.isEnum())
		{
			for(Object obj:OutputTypeEnum.getEnumConstants()){
				if(((Enum)obj).name().equals(outPutTypeStr)){
					outputTypeObject = obj;
					break;
				}
			}
		}
		Method setOutputTypeMethod = AutoDocMetaClazz.getMethod("setOutputType",outputTypeObject.getClass());
		setOutputTypeMethod.invoke(AutoDocMetaClazzInstance, outputTypeObject);
		
//		classLoaderUtil = new ClassLoaderUtil();
//		classLoaderUtil.loadJarPath("reporting/");
		
		//KettleReportBuilder kettleReportBuilder = new KettleReportBuilder(null, locationList, targetFilename, meta);
		Class<?> KettleReportBuilderClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.KettleReportBuilder", true, classLoaderUtil);
		Class<?> LoggingObjectInterfaceClazz = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
		Class<?> AutoDocOptionsInterfaceClazz = Class.forName("org.pentaho.di.trans.steps.autodoc.AutoDocOptionsInterface", true, classLoaderUtil);
		Constructor<?> KettleReportBuilderConstructor = KettleReportBuilderClazz.getConstructor(LoggingObjectInterfaceClazz,List.class,String.class,AutoDocOptionsInterfaceClazz);
		Object KettleReportBuilderInstance = KettleReportBuilderConstructor.newInstance(null,locationList,targetFilename, AutoDocMetaClazzInstance);
//		if (ClassicEngineBoot.getInstance().isBootDone() == false){
//				LibLoaderBoot.getInstance().start();
//		        LibFontBoot.getInstance().start();
//				ClassicEngineBoot.getInstance().start();
//		}
		
//		//LibLoaderBoot.getInstance().start();
//		Class<?> LibLoaderBootClazz = Class.forName("org.pentaho.reporting.libraries.resourceloader.LibLoaderBoot", true, classLoaderUtil);
//		Method LibLoaderBootgetInstanceMethod = LibLoaderBootClazz.getMethod("getInstance");
//		Object LibLoaderBootInstance = LibLoaderBootgetInstanceMethod.invoke(null);
//		
//		Method LibLoaderBootstartMethod=LibLoaderBootClazz.getMethod("start");
//		LibLoaderBootstartMethod.invoke(LibLoaderBootInstance);
//		
//        //LibFontBoot.getInstance().start();
//		Class<?> LibFontBootClazz = Class.forName("org.pentaho.reporting.libraries.fonts.LibFontBoot", true, classLoaderUtil);
//		Method LibFontBootgetInstanceMethod = LibFontBootClazz.getMethod("getInstance");
//		Object LibFontBootInstance = LibFontBootgetInstanceMethod.invoke(null);
//		
//		Method LibFontBootstartMethod=LibFontBootClazz.getMethod("start");
//		LibFontBootstartMethod.invoke(LibFontBootInstance);
//		
//		//ClassicEngineBoot.getInstance().start();
//		Class<?> ClassicEngineBootClazz = Class.forName("org.pentaho.reporting.engine.classic.core.ClassicEngineBoot", true, classLoaderUtil);
//		Method ClassicEngineBootgetInstanceMethod = ClassicEngineBootClazz.getMethod("getInstance");
//		Object ClassicEngineBootInstance = ClassicEngineBootgetInstanceMethod.invoke(null);
//		
//		Method ClassicEngineBootstartMethod=ClassicEngineBootClazz.getMethod("start");
//		ClassicEngineBootstartMethod.invoke(ClassicEngineBootInstance);
		//kettleReportBuilder.createReport();
		Method createReportMethod = KettleReportBuilderClazz.getMethod("createReport");
		createReportMethod.invoke(KettleReportBuilderInstance);
		//kettleReportBuilder.render();
		Method renderMethod = KettleReportBuilderClazz.getMethod("render");
		renderMethod.invoke(KettleReportBuilderInstance);
		
		closeConnect(KettleDatabaseRepositoryClazz,KettleDatabaseRepositoryInstance);
		}catch(Exception e){
			isSuccess = false;
			logger.error(e.getMessage(),e);
		}
		return isSuccess;
	}
	
	public void closeConnect(Class<?> KettleDatabaseRepositoryClazz,Object KettleDatabaseRepositoryInstance){
		try {
			Method disconnectMethod = KettleDatabaseRepositoryClazz.getMethod("disconnect");
			disconnectMethod.invoke(KettleDatabaseRepositoryInstance);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} 
	}
}
