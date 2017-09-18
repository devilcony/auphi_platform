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
package com.auphi.ktrl.engine.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.ClassLoaderUtil;
import com.auphi.ktrl.util.Constants;

public class KettleEngineImpl2_3 implements KettleEngine {
	private static Logger logger = Logger.getLogger(KettleEngineImpl2_3.class);
	
	private static String parentThreadName = "";
	
	private static Class<?> logWriterClass = null;
	private static Object logWriter = null;
	
	private static Class<?> stepLoaderClass = null;
	private static Object stepLoader = null;
	
	private static Class<?> jobLoaderClass = null;
	private static Object jobLoader = null;
	
	private static ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil(); 
			
	public static void init() {
		try {
			classLoaderUtil.loadJarPath("kettle/2.3/");
			//init the engine
		    Class<?> envUtilClass = Class.forName("com.auphi.ketl.core.util.EnvUtil", true, classLoaderUtil);
		    Method evironmentInit = envUtilClass.getDeclaredMethod("environmentInit");
		    evironmentInit.invoke(envUtilClass);
			
		    //init logwriter
			logWriterClass = Class.forName("com.auphi.ketl.core.LogWriter", true, classLoaderUtil);
			Method getInstance_log = logWriterClass.getDeclaredMethod("getInstance", new Class[] {String.class, boolean.class, int.class});
			logWriter = getInstance_log.invoke(logWriterClass, new Object[] {Constants.get("LOG_PATH"), true,  logWriterClass.getField("LOG_LEVEL_BASIC").getInt(0)});
			
			//load step plugins
			stepLoaderClass = Class.forName("com.auphi.ketl.trans.StepLoader", true, classLoaderUtil); 
			Method getInstance_step = stepLoaderClass.getDeclaredMethod("getInstance");
			stepLoader = getInstance_step.invoke(stepLoaderClass);
			
			Method nrStepsWithType = stepLoaderClass.getDeclaredMethod("nrStepsWithType", int.class);
			Class<?> stepPluginClass = Class.forName("com.auphi.ketl.trans.StepPlugin", true, classLoaderUtil);
		    if((Integer)nrStepsWithType.invoke(stepLoader, stepPluginClass.getField("TYPE_NATIVE").getInt(0))==0){
			    Method read = stepLoaderClass.getDeclaredMethod("read");
		    	if (!(Boolean)read.invoke(stepLoader)){
			        System.exit(1);
			    }
		    }
		    
		    //load job plugins
		    jobLoaderClass = Class.forName("com.auphi.ketl.job.JobEntryLoader", true, classLoaderUtil);
		    Method getInstance_job = jobLoaderClass.getDeclaredMethod("getInstance");
		    jobLoader = getInstance_job.invoke(jobLoaderClass);
		    Method read = jobLoaderClass.getDeclaredMethod("read");
		    read.invoke(jobLoader);
		    
		    parentThreadName = Thread.currentThread().getName();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public synchronized boolean execute(String repName, String filePath, String fileName, String fileType, int monitor_id, int execType, String remoteServer, String ha) throws Exception{
		//Thread.currentThread().setContextClassLoader(classLoaderUtil);
		boolean success = false;
		boolean connected = false;
		
		Method disconnect = null;
		Object rep = null;
		try {
			rep = getRep(repName);
			connected = true;
			
			Object directory = getDirectory(rep, filePath);
			
			if(directory!=null){
				if(fileType.equalsIgnoreCase(TYPE_TRANS)){
					//execute trans
					Class<?> transMetaClass = Class.forName("com.auphi.ketl.trans.TransMeta", true, classLoaderUtil);
					Constructor<?> transMetaConstructor = transMetaClass.getConstructor(rep.getClass(), String.class, directory.getClass());
					Object transMeta = transMetaConstructor.newInstance(rep, fileName, directory);
					
					success = executeTrans(transMeta, null, null);
				}else if(fileType.equalsIgnoreCase(TYPE_JOB)){
					//execute job
					Class<?> jobMetaClass = Class.forName("com.auphi.ketl.job.JobMeta", true, classLoaderUtil);
					Constructor<?> JobMetaConstructor = jobMetaClass.getConstructor(logWriter.getClass(), rep.getClass(), String.class, directory.getClass());
					Object jobMeta = JobMetaConstructor.newInstance(logWriter, rep, fileName, directory);
					
					success = executeJob(jobMeta, rep, null, null);
				}
			}
			
			disconnect = rep.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(rep);
			connected = false;
		} catch (Exception e) {
			throw e;
		}finally {
			try {
				if(connected){
					disconnect = rep.getClass().getDeclaredMethod("disconnect");
					disconnect.invoke(rep);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} 
		} 
		
		return success;
	}
	
	@Override
	public String getRepTreeJSON(String repName, String user_id){
		//Thread.currentThread().setContextClassLoader(classLoaderUtil);
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		Method disconnect = null;
		Object rep = null;
		boolean connected = false;
		try{
			rep = getRep(repName);
			connected = true;
			
			Object rootDirectory = getDirectory(rep, "/");
			
			getRepTreeJSON(rep, rootDirectory, sb);
			
			disconnect = rep.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(rep);
			
			connected = false;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}finally {
			try {
				if(connected){
					disconnect = rep.getClass().getDeclaredMethod("disconnect");
					disconnect.invoke(rep);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			} 
		} 
		
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public int checkRepLogin(String repName){
		//Thread.currentThread().setContextClassLoader(classLoaderUtil);
		
		int success = 1;
		
		Method disconnect = null;
		Object rep = null;
		boolean connected = false;
		try{
			rep = getRep(repName);
			connected = true;
			success = 0;
			
			disconnect = rep.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(rep);
			connected = false;
		} catch (Exception e) {
			if(e.getCause() !=  null && e.getCause().getLocalizedMessage() != null){
				String errorMessage = e.getCause().getLocalizedMessage();
				if(errorMessage.indexOf("Incorrect password or login")>0){
					success = 1;
				}else if(errorMessage.indexOf("Error connecting to database")>0){
					success = 2;
				}else if(errorMessage.indexOf("No repository exists")>0){
					success = 3;
				}
			}
			
			logger.error(e.getMessage(),e);
		}finally {
			try {
				if(connected){
					disconnect = rep.getClass().getDeclaredMethod("disconnect");
					disconnect.invoke(rep);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error(e.getMessage(),e);
			} 
		} 
		
		return success;
	}
	
	@Override
	public void authResourcesToUser(String rep_name, String user_id,
			String resource_ids) {
		
	}

	@Override
	public String getResourceTreeJSON(String rep_name, String user_id) {
		return null;
	}
	
	@Override
    public String getXML(String repName, String fileType, String actionPath, String actionRef){
    	return "";
    }
	
	@Override
	public String getActiveDetails(String repName, String fileType, String actionPath, String actionRef, String id_logchannel){
		return "";
	}
	
	@Override
	public String[] getSlaveNames(String repName){
		return new String[]{""};
	}

	/**
	 * get repository tree write to json
	 * @param rep
	 * @param repDirectory
	 * @param sb
	 * @return
	 */
	private String getRepTreeJSON(Object rep, Object repDirectory, StringBuffer sb){
		try{
			//write this directory`s id and name to tree
			Method getID = repDirectory.getClass().getDeclaredMethod("getID");
			long directoryID = (Long)getID.invoke(repDirectory);
			Method getDirectoryName = repDirectory.getClass().getDeclaredMethod("getDirectoryName");
			String directoryName = (String)getDirectoryName.invoke(repDirectory);
			sb.append("{")
			  .append("id:'" + directoryID + "'")
			  .append(",text:'" + directoryName + "'")
			  .append(",expanded:true");
			
			//get sub directories and write to tree 
			Method getNrSubdirectories = repDirectory.getClass().getDeclaredMethod("getNrSubdirectories");
			int subDirCount = (Integer)getNrSubdirectories.invoke(repDirectory);
			
			//trans and jobs in this directory
			Method getTransformationNames = rep.getClass().getDeclaredMethod("getTransformationNames", long.class);
			String[] transNames = (String[])getTransformationNames.invoke(rep, directoryID);
			Method getJobNames = rep.getClass().getDeclaredMethod("getJobNames", long.class);
			String[] jobNames = (String[])getJobNames.invoke(rep, directoryID);
			
			if(subDirCount + transNames.length + jobNames.length > 0){//have child,not leaf
				sb.append(",leaf:false,children:[");
				for(int i=0;i<subDirCount;i++){//add sub directories
					if(i>0){
						sb.append(",");
					}
					Method getSubdirectory = repDirectory.getClass().getDeclaredMethod("getSubdirectory", int.class);
					Object subDirectory = getSubdirectory.invoke(repDirectory, i);
					getRepTreeJSON(rep, subDirectory, sb);
				}
				for(int i=0;i<transNames.length;i++){//add transformations
					if(subDirCount + i>0){
						sb.append(",");
					}
					Method getTransformationID = rep.getClass().getDeclaredMethod("getTransformationID", String.class, long.class);
					long transID = (Long)getTransformationID.invoke(rep, transNames[i], directoryID);
					sb.append("{")
					  .append("id:'" + transID + "_trans'")
					  .append(",text:'" + transNames[i] + "[ktr]'")
					  .append(",leaf:true")
					  .append("}");
				}
				for(int i=0;i<jobNames.length;i++){//add jobs
					if(subDirCount + transNames.length + i>0){
						sb.append(",");
					}
					Method getJobID = rep.getClass().getDeclaredMethod("getJobID", String.class, long.class);
					long jobID = (Long)getJobID.invoke(rep, jobNames[i], directoryID);
					sb.append("{")
					  .append("id:'" + jobID + "_job'")
					  .append(",text:'" + jobNames[i] + "[kjb]'")
					  .append(",leaf:true")
					  .append("}");
				}
					
				sb.append("]");
			}
			
			sb.append("}");
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		
		return sb.toString();
	}
	
	/**
	 * get connected repository
	 * @param repName
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	private Object getRep(String repName) throws Exception{
		if(!parentThreadName.equals(Thread.currentThread().getName())){
			Class<?> localVariablesClass = Class.forName("com.auphi.ketl.core.LocalVariables", true, classLoaderUtil);
		    Method getInstance = localVariablesClass.getDeclaredMethod("getInstance");
		    Method createKettleVariables = localVariablesClass.getDeclaredMethod("createKettleVariables", String.class, String.class, boolean.class);
		    Object localVariables = getInstance.invoke(localVariablesClass);
		    createKettleVariables.invoke(localVariables, Thread.currentThread().getName(), parentThreadName, false);
		}
		
		//all repositories
		Class<?> repositoriesMetaClass = Class.forName("com.auphi.ketl.repository.RepositoriesMeta", true, classLoaderUtil);
		Constructor<?> repositoriesMetaConstructor = repositoriesMetaClass.getConstructor(Class.forName("com.auphi.ketl.core.LogWriter", true, classLoaderUtil));
		Method findRepository = repositoriesMetaClass.getDeclaredMethod("findRepository", new Class[] {String.class});
		Method readData = repositoriesMetaClass.getDeclaredMethod("readData");
		
		Object repsMeta = repositoriesMetaConstructor.newInstance(logWriter);
		
		//get the right repository
		readData.invoke(repsMeta);
		Object repMeta = findRepository.invoke(repsMeta, repName);
		
		Class<?> repositoryClass = Class.forName("com.auphi.ketl.repository.Repository", true, classLoaderUtil);
		Constructor<?> repositoryConstructor = repositoryClass.getConstructor(Class.forName("com.auphi.ketl.core.LogWriter", true, classLoaderUtil),
				Class.forName("com.kingbase.ketl.repository.RepositoryMeta", true, classLoaderUtil), Class.forName("com.auphi.ketl.repository.UserInfo", true, classLoaderUtil));
		Object rep = repositoryConstructor.newInstance(logWriter, repMeta, null);
		
		//connect to the repository
		Method connect = repositoryClass.getDeclaredMethod("connect", String.class);
		connect.invoke(rep, "com.auphi.ktrl.engine.KettleEngineImpl");
		
		//check user and pass
		Class<?> userInfoClass = Class.forName("com.auphi.ketl.repository.UserInfo", true, classLoaderUtil);
		Constructor<?> userInfoConstructor = userInfoClass.getConstructor(Class.forName("com.auphi.ketl.repository.Repository", true, classLoaderUtil), String.class, String.class);
		Method getID = userInfoClass.getDeclaredMethod("getID");
		Object userInfo = userInfoConstructor.newInstance(rep, Constants.get("LoginUser"), Constants.get("LoginPassword"));
		
		if((Long)getID.invoke(userInfo)<=0){
			rep = null;
		}
		
		return rep;
	}
	
	/**
	 * get directory from repository 
	 * @param rep
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	private Object getDirectory(Object rep, String filePath) throws Exception{
		//get the root
		Method getDirectoryTree = rep.getClass().getDeclaredMethod("getDirectoryTree");
		Object directory = getDirectoryTree.invoke(rep);// Default = root
		
		// Find the directory name if one is specified...
		if (filePath!=null && !"/".equals(filePath) && !"".equals(filePath)){
			Method findDirectory = directory.getClass().getDeclaredMethod("findDirectory", String.class);
			directory = findDirectory.invoke(directory, filePath);
		}
		
		return directory;
	}
	
	/**
	 * execute trans
	 * @param transMeta
	 * @param params
	 * @param prop
	 * @return
	 * @throws Exception
	 */
	private boolean executeTrans(Object transMeta, String[] params, HashMap<?, ?> prop) throws Exception{
		boolean success = false;
		
		try {
			Class<?> transClass = Class.forName("com.auphi.ketl.trans.Trans", true, classLoaderUtil);
			Constructor<?> transConstructor = transClass.getConstructor(logWriter.getClass(), transMeta.getClass());
			Object trans = transConstructor.newInstance(logWriter, transMeta);
			
			Method getLogLevelDesc = logWriterClass.getDeclaredMethod("getLogLevelDesc");
			Method logMinimal = logWriterClass.getDeclaredMethod("logMinimal", new Class[] {String.class, String.class});
			logMinimal.invoke(logWriter, "ETL--TRANS", "Logging is at level : " + getLogLevelDesc.invoke(logWriter));
			logMinimal.invoke(logWriter, "ETL--TRANS", "Start of run");
	        
			Date start, stop;
	        Calendar cal;
	        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	        cal=Calendar.getInstance();
	        start=cal.getTime();
	        
	        Method getArguments = transMeta.getClass().getDeclaredMethod("getArguments");
	        Method prepareExecution = transClass.getDeclaredMethod("prepareExecution", String[].class);
	        prepareExecution.invoke(trans, getArguments.invoke(transMeta));
	        
	        Method startThreads = transClass.getDeclaredMethod("startThreads");
	        startThreads.invoke(trans);
	        Method waitUntilFinished = transClass.getDeclaredMethod("waitUntilFinished");
	        waitUntilFinished.invoke(trans);
	        Method endProcessing = transClass.getDeclaredMethod("endProcessing", String.class);
	        endProcessing.invoke(trans, "end");
	        
	        logMinimal.invoke(logWriter, "ETL--TRANS", "Finished");
	        cal=Calendar.getInstance();
	        stop=cal.getTime();
	        String begin=df.format(start).toString();
	        String end  =df.format(stop).toString();
	        logMinimal.invoke(logWriter, "ETL--TRANS", "Start="+begin+", Stop="+end);
	        long millis=stop.getTime()-start.getTime();
	        logMinimal.invoke(logWriter, "ETL--TRANS", "Processing ended after "+(millis/1000)+" seconds.");
	        
	        success = true;
		} catch (Exception e) {
			throw e;
		} 
		return success;
	}

	/**
	 * execute job
	 * @param jobMeta
	 * @param params
	 * @param prop
	 * @return
	 * @throws Exception
	 */
	private boolean executeJob(Object jobMeta, Object rep, String[] params, HashMap<?, ?> prop) throws Exception{
		boolean success = false;
		try{
			Class<?> jobClass = Class.forName("com.auphi.ketl.job.Job", true, classLoaderUtil);
			Object job = null;
			if(rep != null){
				Constructor<?> transConstructor = jobClass.getConstructor(logWriter.getClass(), stepLoader.getClass(), rep.getClass(), jobMeta.getClass());
				job = transConstructor.newInstance(logWriter, stepLoader, rep, jobMeta);
			}else {
				Constructor<?> transConstructor = jobClass.getConstructor(logWriter.getClass(), String.class, String.class, String[].class);
				Method getName = jobMeta.getClass().getDeclaredMethod("getName");
				Method getFileName = jobMeta.getClass().getDeclaredMethod("getFileName");
				job = transConstructor.newInstance(logWriter, getName.invoke(jobMeta), getFileName.invoke(jobMeta), params);
			}

			Method getLogLevelDesc = logWriterClass.getDeclaredMethod("getLogLevelDesc");
			Method logMinimal = logWriterClass.getDeclaredMethod("logMinimal", new Class[] {String.class, String.class});
			logMinimal.invoke(logWriter, "ETL--JOB", "Logging is at level : " + getLogLevelDesc.invoke(logWriter));
			logMinimal.invoke(logWriter, "ETL--JOB", "Start of run");
			
	        Date start, stop;
	        Calendar cal;
	        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	        cal=Calendar.getInstance();
	        start=cal.getTime();
	        
	        Method beginProcessing = jobClass.getDeclaredMethod("beginProcessing");
	        beginProcessing.invoke(job);
	        Method run = jobClass.getDeclaredMethod("run");
	        run.invoke(job);
	        
	        logMinimal.invoke(logWriter, "ETL--JOB", "Finished!");
	        cal=Calendar.getInstance();
	        stop=cal.getTime();
	        String begin=df.format(start).toString();
	        String end  =df.format(stop).toString();
	        logMinimal.invoke(logWriter, "ETL--JOB", "Start="+begin+", Stop="+end);
	        long millis=stop.getTime()-start.getTime();
	        logMinimal.invoke(logWriter, "ETL--JOB", "Processing ended after "+(millis/1000)+" seconds.");
	        
	        success = true;
		}catch(Exception e){
			throw e;
		}
		
		return success;
	}

	@Override
	public void createRepository(RepositoryBean repBean, boolean update) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String checkTableExist(RepositoryBean repBean, String tableName) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void addNewTables(RepositoryBean repBean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getTransStepsLog(String repName, String actionPath,
			String actionRef, int batch_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJobEntriesLog(String repName, String actionPath,
			String actionRef, int batch_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MonitorScheduleBean getMonitorDataFromJobLogTable(
			MonitorScheduleBean monitorScheduleBean, UserBean userBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getJobEntryErrorLog(String jobEntryName, String repName,
			String actionPath, String actionRef, int batch_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stopRunning(String repName, String fileType, String actionPath, String actionRef,
			MonitorScheduleBean monitorBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRepFromDatabase(String repName, String username, String password) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
