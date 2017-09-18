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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.pentaho.di.core.encryption.Encr;
import org.quartz.JobDetail;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.monitor.bean.MonitorScheduleBean;
import com.auphi.ktrl.monitor.util.MonitorUtil;
import com.auphi.ktrl.schedule.template.Template;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.ClassLoaderUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.StringUtil;

public class KettleEngineImpl4_3 implements KettleEngine {
    private static Logger logger = Logger.getLogger(KettleEngineImpl4_3.class);

    private static ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil(); 
    
    private static List<Object> activeTrans = new ArrayList<Object>();
    private static List<Object> activeJobs = new ArrayList<Object>();
    
    public static void init() {
        try {
            //init environment
            Class<?> kttleEnvironmentClass = Class.forName("org.pentaho.di.core.KettleEnvironment", true, classLoaderUtil);
            Method init = kttleEnvironmentClass.getDeclaredMethod("init");
            init.invoke(kttleEnvironmentClass);
        } catch (Exception e) {
        	e.printStackTrace();
            logger.error(e.getMessage(),e);
        }
    }

    @Override
    public synchronized boolean execute(String repName, String filePath, String fileName, String fileType, int monitor_id, int execType, String remoteServer, String ha) throws Exception{
        boolean success = false;
        
        Object rep = null;
        Method disconnect = null;
        boolean connected = false;
        try {
            rep = getRep(repName);
            connected = true;
            
            Object directory = getDirectory(rep, filePath);
            
            if(directory!=null){
                if(fileType.equalsIgnoreCase(TYPE_TRANS)){
                    //execute trans
                    Method loadTransformation = rep.getClass().getDeclaredMethod("loadTransformation", new Class[] {String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), 
                            boolean.class, String.class});
                    Object transMeta = loadTransformation.invoke(rep, fileName, directory, null, true, null);
                    
                    success = executeTrans(transMeta, null, null, monitor_id, execType, remoteServer, ha);
                }else if(fileType.equalsIgnoreCase(TYPE_JOB)){
                    //execute job
                    Method loadJob = rep.getClass().getDeclaredMethod("loadJob", String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), 
                            Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), String.class);
                    Object jobMeta = loadJob.invoke(rep, fileName, directory, null, null);
                    
                    success = executeJob(jobMeta, rep, null, null, monitor_id, execType, remoteServer, ha);
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
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        
        Object rep = null;
        Method disconnect = null;
        boolean connected = false;
        try{
        	rep = getRep(repName);
            connected = true;
            
            String authResourceIDs = getAuthResourceIDs(rep,user_id) ;
            
            Object rootDirectory = getDirectory(rep, "/");
            
            boolean isAdmin = UserUtil.isAdmin(Integer.parseInt("".equals(user_id)?"0":user_id));
            
            getRepTreeJSON(rep, rootDirectory, sb, authResourceIDs.split(","), isAdmin);
            
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
        int success = 1;
        
        Method disconnect = null;
        Object rep = null;
        boolean connected = false;
        try{
            rep = getRep(repName);
            connected = true;
            
            disconnect = rep.getClass().getDeclaredMethod("disconnect");
            disconnect.invoke(rep);
            success = 0;
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
                logger.error(e.getMessage(),e);
            } 
        } 
        
        return success;
    }
    
    /**
     * get repository tree write to json
     * @param rep
     * @param repDirectory
     * @param sb
     * @return
     */
    private String getRepTreeJSON(Object rep, Object repDirectory, StringBuffer sb, String[] authedResourceIDs, boolean isAdmin){
        try{
            //write this directory`s id and name to tree
            Class<?> longObjectIdClass = Class.forName("org.pentaho.di.repository.LongObjectId", true, classLoaderUtil);
            Method getId = longObjectIdClass.getDeclaredMethod("getId");
            Method getObjectId = repDirectory.getClass().getDeclaredMethod("getObjectId");
            Object longObjectId = getObjectId.invoke(repDirectory);
            Object directoryID = getId.invoke(longObjectId);
            Method getDirectoryName = repDirectory.getClass().getDeclaredMethod("getName");
            String directoryName = (String)getDirectoryName.invoke(repDirectory);
            
            //get sub directories and write to tree 
            Method getNrSubdirectories = repDirectory.getClass().getDeclaredMethod("getNrSubdirectories");
            int subDirCount = (Integer)getNrSubdirectories.invoke(repDirectory);
            
            //trans and jobs in this directory
            Method getTransformationNames = rep.getClass().getDeclaredMethod("getTransformationNames", 
                    Class.forName("org.pentaho.di.repository.ObjectId", true, classLoaderUtil), boolean.class);
            String[] transNames = (String[])getTransformationNames.invoke(rep, longObjectId, false);
            Method getJobNames = rep.getClass().getDeclaredMethod("getJobNames", 
                    Class.forName("org.pentaho.di.repository.ObjectId", true, classLoaderUtil), boolean.class);
            String[] jobNames = (String[])getJobNames.invoke(rep, longObjectId, false);
            
            if(subDirCount + transNames.length + jobNames.length > 0){//have child,not leaf
                StringBuffer sb_objs = new StringBuffer();
                
                for(int i=0;i<subDirCount;i++){//add sub directories
                	StringBuffer sb_subs = new StringBuffer();
                    Method getSubdirectory = repDirectory.getClass().getDeclaredMethod("getSubdirectory", int.class);
                    Object subDirectory = getSubdirectory.invoke(repDirectory, i);
                    getRepTreeJSON(rep, subDirectory, sb_subs, authedResourceIDs, isAdmin);
                    if(sb_subs.length() > 0 && sb_objs.length() > 0){
                    	sb_objs.append(",")
                    		   .append(sb_subs);
                    }else {
                    	sb_objs.append(sb_subs);
                	}
                }
                for(int i=0;i<transNames.length;i++){//add transformations
                    Method getTransformationID = rep.getClass().getDeclaredMethod("getTransformationID", String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil));
                    Object transLongObjectID = getTransformationID.invoke(rep, transNames[i], repDirectory);
                    Object transID = getId.invoke(transLongObjectID);
                    if(!isAdmin){
                    	for(String authedResourceId : authedResourceIDs){
                        	if((transID.toString() + TYPE_TRANS_SUFFIX).equals(authedResourceId)){
                        		if(sb_objs.length() > 0){
                        			sb_objs.append(",");
                        		}
                        		sb_objs.append("{")
                        			   .append("id:'" + transID + TYPE_TRANS_SUFFIX + "'")
                        			   .append(",text:'" + transNames[i] + "[" + TYPE_TRANS + "]'")
                        			   .append(",leaf:true")
                        			   .append("}");	
                        		break;
                        	}
                        }
                    }else {
                    	if(sb_objs.length() > 0){
                			sb_objs.append(",");
                		}
                    	sb_objs.append("{")
         			   		   .append("id:'" + transID + TYPE_TRANS_SUFFIX + "'")
         			   		   .append(",text:'" + transNames[i] + "[" + TYPE_TRANS + "]'")
         			   		   .append(",leaf:true")
         			   		   .append("}");
                    }
                }
                for(int i=0;i<jobNames.length;i++){//add jobs
                    Method getJobId = rep.getClass().getDeclaredMethod("getJobId", String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil));
                    Object jobObjectID = getJobId.invoke(rep, jobNames[i], repDirectory);
                    Object jobID = getId.invoke(jobObjectID);
                    if(!isAdmin){
                    	for(String authedResourceId : authedResourceIDs){
                        	if((jobID.toString() + TYPE_JOB_SUFFIX).equals(authedResourceId)){
                        		if(sb_objs.length() > 0){
                        			sb_objs.append(",");
                        		}
                        		sb_objs.append("{")
                                  	   .append("id:'" + jobID + TYPE_JOB_SUFFIX + "'")
                                  	   .append(",text:'" + jobNames[i] + "[" + TYPE_JOB + "]'")
                                  	   .append(",leaf:true")
                                  	   .append("}");
                        		break;
                        	}
                        }
                    }else {
                    	if(sb_objs.length() > 0){
                			sb_objs.append(",");
                		}
                		sb_objs.append("{")
                          	   .append("id:'" + jobID + TYPE_JOB_SUFFIX + "'")
                          	   .append(",text:'" + jobNames[i] + "[" + TYPE_JOB + "]'")
                          	   .append(",leaf:true")
                          	   .append("}");
                    }
                }
                
                if(sb_objs.length() > 0){
                	sb.append("{")
                      .append("id:'" + directoryID + "'")
                      .append(",text:'" + directoryName + "'")
                      .append(",expanded:true,leaf:false,children:[")
                	  .append(sb_objs)
                	  .append("]")
                	  .append("}");
                }
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        
        return sb.toString();
    }
    
    /**
     * Get repositories' meta data from database.
     * */
    public static Object getRepsMetaFromDatabase(){
	   Object repsMeta = new Object(); 
	   
	   try
        {
        	Class<?> repositoriesMetaClass = Class.forName("org.pentaho.di.repository.RepositoriesMeta", true, classLoaderUtil);
            Class<?> databaseMetaClass = Class.forName("org.pentaho.di.core.database.DatabaseMeta", true, classLoaderUtil);
            Class<?> repositoryMetaClass = Class.forName("org.pentaho.di.repository.RepositoryMeta", true, classLoaderUtil);
            Class<?> kettleDatabaseRepositoryMetaClass = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta", true, classLoaderUtil);
            Constructor<?> repositoriesMetaConstructor = repositoriesMetaClass.getConstructor();
            Constructor<?> databaseMetaConstructor = databaseMetaClass.getConstructor(String.class,String.class,
                String.class,String.class,String.class,String.class,String.class,String.class);
            Constructor<?> kettleDatabaseRepositoryMetaConstructor = kettleDatabaseRepositoryMetaClass.getConstructor(String.class,
                String.class,String.class,databaseMetaClass);
            repsMeta = repositoriesMetaConstructor.newInstance();
            Method addDatabase = repositoriesMetaClass.getDeclaredMethod("addDatabase", databaseMetaClass) ;
            Method addRepository = repositoriesMetaClass.getDeclaredMethod("addRepository", repositoryMetaClass) ;
        	
            List<RepositoryBean> reps = RepositoryUtil.getAllRepositories() ;
            for (int i = 0 ; i < reps.size() ; i ++)
            {
                RepositoryBean rep = reps.get(i) ;
                Object databaseMeta = databaseMetaConstructor.newInstance(rep.getRepositoryName(),rep.getDbType(),
                    rep.getDbAccess(),rep.getDbHost(),rep.getDbName(),rep.getDbPort(),rep.getUserName(),rep.getPassword()) ;
                Object repositoryMeta = kettleDatabaseRepositoryMetaConstructor.newInstance(String.valueOf(rep.getRepositoryID()),rep.getRepositoryName(),
                        rep.getDbType(),databaseMeta) ;
                addDatabase.invoke(repsMeta, databaseMeta) ;
                addRepository.invoke(repsMeta, repositoryMeta) ;
            }
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(),e);
        }
        return repsMeta;
    }
    
    /**
     * get connected repository
     * @param repName
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public Object getRepFromDatabase(String repName, String username, String password) throws Exception{
        //all repositories
        Class<?> repositoriesMetaClass = Class.forName("org.pentaho.di.repository.RepositoriesMeta", true, classLoaderUtil);
        Method findRepository = repositoriesMetaClass.getDeclaredMethod("findRepository", new Class[] {String.class});
        
        Object repsMeta = getRepsMetaFromDatabase();
        
        //get the right repository
        Object repMeta = findRepository.invoke(repsMeta, repName);
        
        Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
        Class<?> pluginRegistryClass = Class.forName("org.pentaho.di.core.plugins.PluginRegistry", true, classLoaderUtil);
        Method getInstance = pluginRegistryClass.getDeclaredMethod("getInstance");
        Method loadClass = pluginRegistryClass.getDeclaredMethod("loadClass", new Class[] {Class.class, Object.class, Class.class});
        Method init = repositoryClass.getDeclaredMethod("init", Class.forName("org.pentaho.di.repository.RepositoryMeta", true, classLoaderUtil));
        Object pluginRegistry = getInstance.invoke(pluginRegistryClass);
        
        Object rep = loadClass.invoke(pluginRegistry, Class.forName("org.pentaho.di.core.plugins.RepositoryPluginType", true, classLoaderUtil), 
                repMeta, repositoryClass);
        
        init.invoke(rep, repMeta);
        
        //connect to the repository
        Method connect = repositoryClass.getDeclaredMethod("connect", new Class[]{String.class, String.class});
        connect.invoke(rep, username, password);
        
        return rep;
    }
    
    /**
     * get connected repository
     * @param repName
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public Object getRep(String repName) throws Exception{
        //all repositories
        Class<?> repositoriesMetaClass = Class.forName("org.pentaho.di.repository.RepositoriesMeta", true, classLoaderUtil);
        Method findRepository = repositoriesMetaClass.getDeclaredMethod("findRepository", new Class[] {String.class});
        
        Object repsMeta = getRepsMetaFromDatabase();
        
        Object repMeta = findRepository.invoke(repsMeta, repName);
        
        Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
        Class<?> pluginRegistryClass = Class.forName("org.pentaho.di.core.plugins.PluginRegistry", true, classLoaderUtil);
        Method getInstance = pluginRegistryClass.getDeclaredMethod("getInstance");
        Method loadClass = pluginRegistryClass.getDeclaredMethod("loadClass", new Class[] {Class.class, Object.class, Class.class});
        Method init = repositoryClass.getDeclaredMethod("init", Class.forName("org.pentaho.di.repository.RepositoryMeta", true, classLoaderUtil));
        Object pluginRegistry = getInstance.invoke(pluginRegistryClass);
        
        Object rep = loadClass.invoke(pluginRegistry, Class.forName("org.pentaho.di.core.plugins.RepositoryPluginType", true, classLoaderUtil), 
                repMeta, repositoryClass);
        
        init.invoke(rep, repMeta);
        
        //connect to the repository
        Method connect = repositoryClass.getDeclaredMethod("connect", new Class[]{String.class, String.class});
        connect.invoke(rep, Constants.get("LoginUser"), Constants.get("LoginPassword"));
        
        return rep;
    }
    
    /**
     * get directory from repository 
     * @param rep
     * @param filePath
     * @return
     * @throws Exception
     */
    public Object getDirectory(Object rep, String filePath) throws Exception{
        //get the root
        Method loadRepositoryDirectoryTree = rep.getClass().getDeclaredMethod("loadRepositoryDirectoryTree");
        Object directory = loadRepositoryDirectoryTree.invoke(rep);// Default = root
        
        // Find the directory name if one is specified...
        if (filePath!=null && !"/".equals(filePath) && !"".equals(filePath)){
            Method findDirectory = directory.getClass().getDeclaredMethod("findDirectory", String.class);
            directory = findDirectory.invoke(directory, filePath);
        }
        
        return directory;
    }
    
    /**
     * execute transation
     * @param transMeta
     * @param params
     * @param prop
     * @return
     * @throws Exception
     */
    private boolean executeTrans(Object transMeta, String[] params, HashMap<?, ?> prop, int monitor_id, int execType, String remoteServer, String ha) throws Exception{
        boolean success = false;
        Date start = new Date();
        Date stop;
        String status = MonitorUtil.STATUS_FINISHED;
        String logChannelId = "";
        Object result = null;
        Object trans = null;
        int id_server = 0;
        
        try {
        	String simpleObjectId = UUID.randomUUID().toString();
            //init trans object
            Class<?> transMetaClass = Class.forName("org.pentaho.di.trans.TransMeta", true, classLoaderUtil);
            Class<?> transClass = Class.forName("org.pentaho.di.trans.Trans", true, classLoaderUtil);
            Constructor<?> transConstructor = transClass.getConstructor(transMetaClass);
            trans = transConstructor.newInstance(transMeta);
            
            if(EXECTYPE_LOCAL == execType){
            	Class<?> simpleLoggingObjectClass = Class.forName("org.pentaho.di.core.logging.SimpleLoggingObject", true, classLoaderUtil);
            	Class<?> loggingObjectTypeClass = Class.forName("org.pentaho.di.core.logging.LoggingObjectType", true, classLoaderUtil);
            	Class<?> loggingObjectInterfaceClass = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
            	Constructor<?> simpleLoggingObjectConstructor = simpleLoggingObjectClass.getConstructor(String.class, loggingObjectTypeClass, loggingObjectInterfaceClass);
            	
            	Field jobField = loggingObjectTypeClass.getDeclaredField("JOB");
                Object simpleLoggingObject = simpleLoggingObjectConstructor.newInstance("SPOON", jobField.get(""), null);
                Method setContainerObjectId = simpleLoggingObjectClass.getDeclaredMethod("setContainerObjectId", String.class);
                setContainerObjectId.invoke(simpleLoggingObject, simpleObjectId);
                
                Method setParent = transClass.getDeclaredMethod("setParent", loggingObjectInterfaceClass);
                setParent.invoke(trans, simpleLoggingObject);
            	
            	//logs
                Method getLogChannel = transClass.getDeclaredMethod("getLogChannel");
                Object logChannel = getLogChannel.invoke(trans);
                Method logMinimal = logChannel.getClass().getDeclaredMethod("logMinimal", new Class[] {String.class, Object[].class});
                
                //set log channel ids
                Method getLogChannelId = transClass.getDeclaredMethod("getLogChannelId");
                logChannelId = (String)getLogChannelId.invoke(trans);
                
                logMinimal.invoke(logChannel, new Object[] {"ETL--TRANS Start of run", new Object[0]});
                
                //trans execute
                Method getArguments = transMetaClass.getDeclaredMethod("getArguments");
                Method prepareExecution = transClass.getDeclaredMethod("prepareExecution", String[].class);
                prepareExecution.invoke(trans, getArguments.invoke(transMeta));
                
                //add to active 
                activeTrans.add(trans);
                
                Method startThreads = transClass.getDeclaredMethod("startThreads");
                startThreads.invoke(trans);
                Method waitUntilFinished = transClass.getDeclaredMethod("waitUntilFinished");
                waitUntilFinished.invoke(trans);
                
                //remove from active
                activeTrans.remove(trans);
                
                //log write
                logMinimal.invoke(logChannel, new Object[] {"ETL--TRANS Finished", new Object[0]});
                stop=new Date();
                logMinimal.invoke(logChannel, new Object[] {"ETL--TRANS Start="+StringUtil.DateToString(start, "yyyy/MM/dd HH:mm:ss")+", Stop="+StringUtil.DateToString(stop, "yyyy/MM/dd HH:mm:ss"), new Object[0]});
                long millis=stop.getTime()-start.getTime();
                logMinimal.invoke(logChannel, new Object[] {"ETL--TRANS Processing ended after "+(millis/1000)+" seconds.", new Object[0]});
                
                //get result
                Method getResult = transClass.getDeclaredMethod("getResult");
                result = getResult.invoke(trans);
                
                success = true;
            }else if(EXECTYPE_REMOTE == execType){
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
                Method getRepository = transMeta.getClass().getDeclaredMethod("getRepository");
                Object rep = getRepository.invoke(transMeta);
                
                Class<?> transExcutionConfigClass = Class.forName("org.pentaho.di.trans.TransExecutionConfiguration", true, classLoaderUtil);
                
                Constructor<?> consTransExcutionConfig = transExcutionConfigClass.getConstructor();
                Object transExcutionConfig = consTransExcutionConfig.newInstance();
                
                Method setRepository = transExcutionConfigClass.getDeclaredMethod("setRepository", repositoryClass);
                setRepository.invoke(transExcutionConfig, rep);
                
                Method setExcutingLocally = transExcutionConfigClass.getDeclaredMethod("setExecutingLocally", boolean.class);
                setExcutingLocally.invoke(transExcutionConfig, false);
                Method setExecutingRemotely = transExcutionConfigClass.getDeclaredMethod("setExecutingRemotely", boolean.class);
                setExecutingRemotely.invoke(transExcutionConfig, true);
                Method setExecutingClustered = transExcutionConfigClass.getDeclaredMethod("setExecutingClustered", boolean.class);
                setExecutingClustered.invoke(transExcutionConfig, false);
                
//                Method findSlaveServer = transMeta.getClass().getDeclaredMethod("findSlaveServer", String.class);
//                Object slaveServer = findSlaveServer.invoke(transMeta, remoteServer);
                SlaveServerBean slaveServerBean = getSlaveServerBean(remoteServer, "");
                Object slaveServer = createSlaveServer(slaveServerBean);
                
                Method setRemoteServer = transExcutionConfigClass.getDeclaredMethod("setRemoteServer", slaveServer.getClass());
                setRemoteServer.invoke(transExcutionConfig, slaveServer);
                		
                
                Method sendToSlaveServer = trans.getClass().getDeclaredMethod("sendToSlaveServer", transMeta.getClass(), transExcutionConfigClass, repositoryClass);
                sendToSlaveServer.invoke(trans, transMeta, transExcutionConfig, rep);
                
                success = true;
            }else if(EXECTYPE_CLUSTER == execType){
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
                Method getRepository = transMeta.getClass().getDeclaredMethod("getRepository");
                Object rep = getRepository.invoke(transMeta);
                
                Class<?> transExcutionConfigClass = Class.forName("org.pentaho.di.trans.TransExecutionConfiguration", true, classLoaderUtil);
                
                Constructor<?> consTransExcutionConfig = transExcutionConfigClass.getConstructor();
                Object transExcutionConfig = consTransExcutionConfig.newInstance();
                
                Method setRepository = transExcutionConfigClass.getDeclaredMethod("setRepository", repositoryClass);
                setRepository.invoke(transExcutionConfig, rep);
                
                Method setExcutingLocally = transExcutionConfigClass.getDeclaredMethod("setExecutingLocally", boolean.class);
                setExcutingLocally.invoke(transExcutionConfig, false);
                Method setExecutingRemotely = transExcutionConfigClass.getDeclaredMethod("setExecutingRemotely", boolean.class);
                setExecutingRemotely.invoke(transExcutionConfig, false);
                Method setExecutingClustered = transExcutionConfigClass.getDeclaredMethod("setExecutingClustered", boolean.class);
                setExecutingClustered.invoke(transExcutionConfig, true);
                Method setClusterPosting = transExcutionConfigClass.getDeclaredMethod("setClusterPosting", boolean.class);
                setClusterPosting.invoke(transExcutionConfig, true);
                Method setClusterPreparing = transExcutionConfigClass.getDeclaredMethod("setClusterPreparing", boolean.class);
                setClusterPreparing.invoke(transExcutionConfig, true);
                Method setClusterStarting = transExcutionConfigClass.getDeclaredMethod("setClusterStarting", boolean.class);
                setClusterStarting.invoke(transExcutionConfig, true);
                Method setClusterShowingTransformation = transExcutionConfigClass.getDeclaredMethod("setClusterShowingTransformation", boolean.class);
                setClusterShowingTransformation.invoke(transExcutionConfig, true);
            	
                Method executeClustered = trans.getClass().getDeclaredMethod("executeClustered", transMeta.getClass(), transExcutionConfigClass);
                executeClustered.invoke(trans, transMeta, transExcutionConfig);
            }else if(EXECTYPE_HA == execType){
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
                Method getRepository = transMeta.getClass().getDeclaredMethod("getRepository");
                Object rep = getRepository.invoke(transMeta);
                
                Class<?> transExcutionConfigClass = Class.forName("org.pentaho.di.trans.TransExecutionConfiguration", true, classLoaderUtil);
                
                Constructor<?> consTransExcutionConfig = transExcutionConfigClass.getConstructor();
                Object transExcutionConfig = consTransExcutionConfig.newInstance();
                
                Method setRepository = transExcutionConfigClass.getDeclaredMethod("setRepository", repositoryClass);
                setRepository.invoke(transExcutionConfig, rep);
                
                Method setExcutingLocally = transExcutionConfigClass.getDeclaredMethod("setExecutingLocally", boolean.class);
                setExcutingLocally.invoke(transExcutionConfig, false);
                Method setExecutingRemotely = transExcutionConfigClass.getDeclaredMethod("setExecutingRemotely", boolean.class);
                setExecutingRemotely.invoke(transExcutionConfig, true);
                Method setExecutingClustered = transExcutionConfigClass.getDeclaredMethod("setExecutingClustered", boolean.class);
                setExecutingClustered.invoke(transExcutionConfig, false);
                
//                Method findSlaveServer = transMeta.getClass().getDeclaredMethod("findSlaveServer", String.class);
//                Object slaveServer = findSlaveServer.invoke(transMeta, remoteServer);
                
                SlaveServerBean slaveServerBean = getSlaveServerBean("", ha);
                id_server = slaveServerBean.getId_slave();
                Object slaveServer = createSlaveServer(slaveServerBean);
                
                Method setRemoteServer = transExcutionConfigClass.getDeclaredMethod("setRemoteServer", slaveServer.getClass());
                setRemoteServer.invoke(transExcutionConfig, slaveServer);
                		
                
                Method sendToSlaveServer = trans.getClass().getDeclaredMethod("sendToSlaveServer", transMeta.getClass(), transExcutionConfigClass, repositoryClass);
                sendToSlaveServer.invoke(trans, transMeta, transExcutionConfig, rep);
                
                success = true;
            }
        } catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            String errMsg = sw.toString();
//            
//            logger.error(e.getMessage(), e);
//            MonitorUtil.updateMonitorAfterError(monitor_id, errMsg);
            
            status = MonitorUtil.STATUS_ERROR;
            throw e;
        } finally {
        	Method getBatchId = trans.getClass().getDeclaredMethod("getBatchId");
        	int batch_id = getBatchId.invoke(trans)==null?-1:((Long)getBatchId.invoke(trans)).intValue();
            monitor(monitor_id, start, status, logChannelId, result, id_server, batch_id);
            if(trans != null && activeTrans.contains(trans)){
            	activeTrans.remove(trans);
            }
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
    public boolean executeJob(Object jobMeta, Object rep, String[] params, HashMap<?, ?> prop, int monitor_id, int execType, String remoteServer, String ha) throws Exception{
        boolean success = false;
        Date start = new Date();
        Date stop;
        String status = MonitorUtil.STATUS_FINISHED;
        String logChannelId = "";
        Object result = null;
        Object job = null;
        int id_server = 0;
        
        try{
            //init job object
            Class<?> jobClass = Class.forName("org.pentaho.di.job.Job", true, classLoaderUtil);
            String simpleObjectId = UUID.randomUUID().toString();
            if(rep != null){
            	Class<?> simpleLoggingObjectClass = Class.forName("org.pentaho.di.core.logging.SimpleLoggingObject", true, classLoaderUtil);
            	Class<?> loggingObjectTypeClass = Class.forName("org.pentaho.di.core.logging.LoggingObjectType", true, classLoaderUtil);
            	Class<?> loggingObjectInterfaceClass = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
            	Constructor<?> simpleLoggingObjectConstructor = simpleLoggingObjectClass.getConstructor(String.class, loggingObjectTypeClass, loggingObjectInterfaceClass);
            	
            	Field jobField = loggingObjectTypeClass.getDeclaredField("JOB");
                Object simpleLoggingObject = simpleLoggingObjectConstructor.newInstance("SPOON", jobField.get(""), null);
                Method setContainerObjectId = simpleLoggingObjectClass.getDeclaredMethod("setContainerObjectId", String.class);
                setContainerObjectId.invoke(simpleLoggingObject, simpleObjectId);
                Constructor<?> jobConstructor = jobClass.getConstructor(repositoryClass, jobMeta.getClass(), loggingObjectInterfaceClass);
    			job = jobConstructor.newInstance(rep, jobMeta, simpleLoggingObject);
            }else {
                Constructor<?> jobConstructor = jobClass.getConstructor(String.class, String.class, String[].class);
                Method getName = jobMeta.getClass().getDeclaredMethod("getName");
                Method getFileName = jobMeta.getClass().getDeclaredMethod("getFileName");
                job = jobConstructor.newInstance(getName.invoke(jobMeta), getFileName.invoke(jobMeta), params);
            }
            
            Method setInteractive = jobClass.getDeclaredMethod("setInteractive", boolean.class);
            setInteractive.invoke(job, true);
            
            if(EXECTYPE_LOCAL == execType){
            	//logs
                Method getLogChannel = jobClass.getDeclaredMethod("getLogChannel");
                Object logChannel = getLogChannel.invoke(job);
                
                //set log channel ids
                Method getLogChannelId = jobClass.getDeclaredMethod("getLogChannelId");
                logChannelId = (String)getLogChannelId.invoke(job);
                
                Method logMinimal = logChannel.getClass().getDeclaredMethod("logMinimal", new Class[] {String.class, Object[].class});
                logMinimal.invoke(logChannel, new Object[] {"ETL--JOB Start of run", new Object[0]});
                
                //add to active 
                activeJobs.add(job);
                
                //job execute
                Method beginProcessing = jobClass.getDeclaredMethod("beginProcessing");
                beginProcessing.invoke(job);
//                Method getBatchId = jobClass.getDeclaredMethod("getBatchId");
//                int batchId = getBatchId.invoke(job)==null?-1:((Long)getBatchId.invoke(job)).intValue();
//                
//                MonitorUtil.updateMonitorInLocalRun(monitor_id, batchId);
                
                MonitorUtil.updateMonitorInLocalRun(monitor_id, logChannelId);
                
                Class<?> threadClass = Class.forName("java.lang.Thread", true, classLoaderUtil);
                Method start_job = threadClass.getDeclaredMethod("start");
                start_job.invoke(job);
                Method waitUntilFinished = jobClass.getDeclaredMethod("waitUntilFinished");
                waitUntilFinished.invoke(job);
                
                //remove from active
                activeJobs.remove(job);
                
                //log write
                logMinimal.invoke(logChannel, new Object[] {"ETL--JOB Finished!", new Object[0]});
                stop = new Date();
                logMinimal.invoke(logChannel, new Object[] {"ETL--JOB Start="+StringUtil.DateToString(start, "yyyy/MM/dd HH:mm:ss")+", Stop="+StringUtil.DateToString(stop, "yyyy/MM/dd HH:mm:ss"), new Object[0]});
                long millis=stop.getTime()-start.getTime();
                logMinimal.invoke(logChannel, new Object[] {"ETL--JOB Processing ended after "+(millis/1000)+" seconds.", new Object[0]});
                
                Method getResult = jobClass.getDeclaredMethod("getResult");
                result = getResult.invoke(job);
                
                success = true;
            }else if(EXECTYPE_REMOTE == execType){
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
                Class<?> jobExcutionConfigClass = Class.forName("org.pentaho.di.job.JobExecutionConfiguration", true, classLoaderUtil);
                
                Constructor<?> consJobExcutionConfig = jobExcutionConfigClass.getConstructor();
                Object jobExcutionConfig = consJobExcutionConfig.newInstance();
                
                Method setRepository = jobExcutionConfigClass.getDeclaredMethod("setRepository", repositoryClass);
                setRepository.invoke(jobExcutionConfig, rep);
                
                Method setExcutingLocally = jobExcutionConfigClass.getDeclaredMethod("setExecutingLocally", boolean.class);
                setExcutingLocally.invoke(jobExcutionConfig, false);
                Method setExecutingRemotely = jobExcutionConfigClass.getDeclaredMethod("setExecutingRemotely", boolean.class);
                setExecutingRemotely.invoke(jobExcutionConfig, true);
                
//                Method findSlaveServer = jobMeta.getClass().getDeclaredMethod("findSlaveServer", String.class);
//                Object slaveServer = findSlaveServer.invoke(jobMeta, remoteServer);
                SlaveServerBean slaveServerBean = getSlaveServerBean(remoteServer, "");
                Object slaveServer = createSlaveServer(slaveServerBean);
                
                Method setRemoteServer = jobExcutionConfigClass.getDeclaredMethod("setRemoteServer", slaveServer.getClass());
                setRemoteServer.invoke(jobExcutionConfig, slaveServer);
                		
                
                Method sendToSlaveServer = job.getClass().getDeclaredMethod("sendToSlaveServer", jobMeta.getClass(), jobExcutionConfigClass, repositoryClass);
                sendToSlaveServer.invoke(job, jobMeta, jobExcutionConfig, rep);
                
                success = true;
            }else if(EXECTYPE_HA == execType){
            	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
                Class<?> jobExcutionConfigClass = Class.forName("org.pentaho.di.job.JobExecutionConfiguration", true, classLoaderUtil);
                
                Constructor<?> consJobExcutionConfig = jobExcutionConfigClass.getConstructor();
                Object jobExcutionConfig = consJobExcutionConfig.newInstance();
                
                Method setRepository = jobExcutionConfigClass.getDeclaredMethod("setRepository", repositoryClass);
                setRepository.invoke(jobExcutionConfig, rep);
                
                Method setExcutingLocally = jobExcutionConfigClass.getDeclaredMethod("setExecutingLocally", boolean.class);
                setExcutingLocally.invoke(jobExcutionConfig, false);
                Method setExecutingRemotely = jobExcutionConfigClass.getDeclaredMethod("setExecutingRemotely", boolean.class);
                setExecutingRemotely.invoke(jobExcutionConfig, true);
                
//                Method findSlaveServer = jobMeta.getClass().getDeclaredMethod("findSlaveServer", String.class);
//                Object slaveServer = findSlaveServer.invoke(jobMeta, remoteServer);
                SlaveServerBean slaveServerBean = getSlaveServerBean("", ha);
                id_server = slaveServerBean.getId_slave();
                Object slaveServer = createSlaveServer(slaveServerBean);
                
                Method setRemoteServer = jobExcutionConfigClass.getDeclaredMethod("setRemoteServer", slaveServer.getClass());
                setRemoteServer.invoke(jobExcutionConfig, slaveServer);
                		
                
                Method sendToSlaveServer = job.getClass().getDeclaredMethod("sendToSlaveServer", jobMeta.getClass(), jobExcutionConfigClass, repositoryClass);
                sendToSlaveServer.invoke(job, jobMeta, jobExcutionConfig, rep);
                
                success = true;
            }
        }catch (Exception e) {
//            StringWriter sw = new StringWriter();
//            PrintWriter pw = new PrintWriter(sw);
//            e.printStackTrace(pw);
//            String errMsg = sw.toString();
//            
//            logger.error(e.getMessage(), e);
//            MonitorUtil.updateMonitorAfterError(monitor_id, errMsg);
        	
            status = MonitorUtil.STATUS_ERROR;
            throw e;
        } finally {
        	Method getBatchId = job.getClass().getDeclaredMethod("getBatchId");
        	int batch_id = getBatchId.invoke(job)==null?-1:((Long)getBatchId.invoke(job)).intValue();
            monitor(monitor_id, start, status, logChannelId, result, id_server, batch_id);
            if(job != null && activeJobs.contains(job)){
            	activeJobs.remove(job);
            }
        }
        
        return success;
    }
    
    /**
     * 记录监控日志信息
     * @param monitor_id 监控记录id
     * @param start 开始时间
     * @param status 状态
     */
    private static void monitor(int monitor_id, Date start, String status, String logChannelId, Object result, int id_server, int batch_id){
        try{
            String logMessage = "";
            //get running logs
            Class<?> centralLogStoreClass = Class.forName("org.pentaho.di.core.logging.CentralLogStore", true, classLoaderUtil);
            Method getAppender = centralLogStoreClass.getDeclaredMethod("getAppender");
            Object appender = getAppender.invoke(centralLogStoreClass);

			Method getBuffer = appender.getClass().getDeclaredMethod("getBuffer", String.class, boolean.class);
			StringBuffer sb = (StringBuffer) getBuffer.invoke(appender, logChannelId, true);

			logMessage = sb.toString().replaceAll("\n", "<br>");
            
            if(result != null){
                Method getNrErrors = result.getClass().getDeclaredMethod("getNrErrors");
                Method getNrLinesInput = result.getClass().getDeclaredMethod("getNrLinesInput");
                Method getNrLinesOutput = result.getClass().getDeclaredMethod("getNrLinesOutput");
                Method getNrLinesUpdated = result.getClass().getDeclaredMethod("getNrLinesUpdated");
                Method getNrLinesRead = result.getClass().getDeclaredMethod("getNrLinesRead");
                Method getNrLinesWritten = result.getClass().getDeclaredMethod("getNrLinesWritten");
                Method getNrLinesDeleted = result.getClass().getDeclaredMethod("getNrLinesDeleted");
                
                long nrErrors = (Long)getNrErrors.invoke(result);
                long nrLinesInput = (Long)getNrLinesInput.invoke(result);
                long nrLinesOutput = (Long)getNrLinesOutput.invoke(result);
                long nrLinesUpdated = (Long)getNrLinesUpdated.invoke(result);
                long nrLinesRead = (Long)getNrLinesRead.invoke(result);
                long nrLinesWritten = (Long)getNrLinesWritten.invoke(result);
                long nrLinesDeleted = (Long)getNrLinesDeleted.invoke(result);
                
                if(nrErrors > 0){
                	status = MonitorUtil.STATUS_ERROR;
                }
                
                MonitorUtil.updateMonitorAfterRun(monitor_id, start, logMessage, status, nrErrors, nrLinesInput, nrLinesOutput, nrLinesUpdated, nrLinesRead, nrLinesWritten, nrLinesDeleted, id_server, batch_id);
            }else {
                MonitorUtil.updateMonitorAfterRun(monitor_id, start, logMessage, status, 0, 0, 0, 0, 0, 0, 0, id_server, batch_id);
            }
            
            
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Get resource ID, removed suffix.
     * */
    private int getResourceID(String resourceID)
    {
        int index = resourceID.indexOf('_') ;
        return Integer.parseInt(resourceID.substring(0,index)) ;
    }
    
    /**
     * Get resource type.
     * 
     * */
    private int getResourceType(String resourceID)
    {
        if (resourceID.endsWith(TYPE_DIR_SUFFIX))
            return type_dir ;
        
        if (resourceID.endsWith(TYPE_JOB_SUFFIX))
            return type_job ;
        
        if (resourceID.endsWith(TYPE_TRANS_SUFFIX))
            return type_trans ;
        
        return -1 ;
    }    
    /**
     * Get type-specific suffixed resource ID.
     * 
     * */
    private String getResourceID(Object resourceID, int resource_type)
    {
        switch(resource_type)
        {
            case type_trans:
                return resourceID + TYPE_TRANS_SUFFIX ;
            case type_job:
                return resourceID + TYPE_JOB_SUFFIX ;
            case type_dir:
                return resourceID + TYPE_DIR_SUFFIX ;
            default:
                return "" ;
        }
    }
    /**
     * get repository tree write to json
     * @param rep
     * @param repDirectory
     * @param sb
     * @return
     */
    private String getResourceCheckTreeJSON(Object rep, Object repDirectory, StringBuffer sb){
        try
        {
            //write this directory`s id and name to tree
            Class<?> longObjectIdClass = Class.forName("org.pentaho.di.repository.LongObjectId", true, classLoaderUtil);
            Method getId = longObjectIdClass.getDeclaredMethod("getId");
            Method getObjectId = repDirectory.getClass().getDeclaredMethod("getObjectId");
            Object longObjectId = getObjectId.invoke(repDirectory);
            Object directoryID = getId.invoke(longObjectId);
            Method getDirectoryName = repDirectory.getClass().getDeclaredMethod("getName");
            String directoryName = (String)getDirectoryName.invoke(repDirectory);
            sb.append("{")
              .append("id:'" + getResourceID(directoryID,type_dir) + "'")
              .append(",checked:false,text:'" + directoryName + "'")
              .append(",expanded:true");
            
            //get sub directories and write to tree 
            Method getNrSubdirectories = repDirectory.getClass().getDeclaredMethod("getNrSubdirectories");
            int subDirCount = (Integer)getNrSubdirectories.invoke(repDirectory);
            
            //trans and jobs in this directory
            Method getTransformationNames = rep.getClass().getDeclaredMethod("getTransformationNames", 
                    Class.forName("org.pentaho.di.repository.ObjectId", true, classLoaderUtil), boolean.class);
            String[] transNames = (String[])getTransformationNames.invoke(rep, longObjectId, false);
            Method getJobNames = rep.getClass().getDeclaredMethod("getJobNames", 
                    Class.forName("org.pentaho.di.repository.ObjectId", true, classLoaderUtil), boolean.class);
            String[] jobNames = (String[])getJobNames.invoke(rep, longObjectId, false);
            
            if(subDirCount + transNames.length + jobNames.length > 0){//have child,not leaf
                sb.append(",leaf:false,children:[");
                for(int i=0;i<subDirCount;i++){//add sub directories
                    if(i>0){
                        sb.append(",");
                    }
                    Method getSubdirectory = repDirectory.getClass().getDeclaredMethod("getSubdirectory", int.class);
                    Object subDirectory = getSubdirectory.invoke(repDirectory, i);
                    getResourceCheckTreeJSON(rep, subDirectory, sb);
                }
                for(int i=0;i<transNames.length;i++){//add transformations
                    if(subDirCount + i>0){
                        sb.append(",");
                    }
                    Method getTransformationID = rep.getClass().getDeclaredMethod("getTransformationID", String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil));
                    Object transLongObjectID = getTransformationID.invoke(rep, transNames[i], repDirectory);
                    Object transID = getId.invoke(transLongObjectID);
                    sb.append("{")
                      .append("id:'" + getResourceID(transID,type_trans) )
                      .append("',checked:false,text:'" + transNames[i] + "[ktr]'")
                      .append(",leaf:true")
                      .append("}");
                }
                for(int i=0;i<jobNames.length;i++){//add jobs
                    if(subDirCount + transNames.length + i>0){
                        sb.append(",");
                    }
                    Method getJobId = rep.getClass().getDeclaredMethod("getJobId", String.class, 
                            Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil));
                    Object jobObjectID = getJobId.invoke(rep, jobNames[i], repDirectory);
                    Object jobID = getId.invoke(jobObjectID);
                    sb.append("{")
                      .append("id:'" + getResourceID(jobID,type_job))
                      .append("',checked:false,text:'" + jobNames[i] + "[kjb]'")
                      .append(",leaf:true")
                      .append("}");
                }
                sb.append("]");
            }
            else if (transNames.length + jobNames.length == 0)// empty subdir
            {
                sb.append(",leaf:false,children:[]");
            }
            
            sb.append("}");
            
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        
        return sb.toString();
    }

    /**
     * Get JSON string of resource tree, with authorised resource checked.
     * 
     * */
    private String getResourceTree(String repName, String user_id){
        StringBuffer sb = new StringBuffer(1024*4);
        sb.append("[");
        
        Object rep = null;
        Method disconnect = null;
        boolean connected = false;
        try
        {
           
            rep = getRepFromDatabase(repName,  Constants.get("LoginUser") , Constants.get("LoginPassword") );
            connected = true;
            
            Object rootDirectory = getDirectory(rep, "/");
            
            getResourceCheckTreeJSON(rep, rootDirectory, sb);
            String authResourceIDs = getAuthResourceIDs(rep,user_id) ;
            String [] ids = authResourceIDs.split(",") ;
            int index = 0 ;
            String subString ;
            String replaceString ;
            for (String id: ids)
            {
                subString = "id:'"+id+"',checked:false" ;
                replaceString = "id:'"+id+"',checked:true" ;
                index = sb.indexOf(subString) ;
                if (index >= 0)
                {
                    sb.replace(index, index+subString.length(), replaceString) ;
                }
            }
            
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
                // TODO Auto-generated catch block
                logger.error(e.getMessage(),e);
            } 
        } 
        
        sb.append("]");
//        System.out.println(sb.toString());
        return sb.toString();
    }
    
    /**
     * Return authorised resource IDs of one user. Each resource ID ends with
     * a type-specific suffix.  
     * 
     * @param rep : connected repository object
     * @param user_id : 
     * @return string contains resource IDs split by ','
     * 
     * */
    private String getAuthResourceIDs(Object rep,String user_id)
    {
        
        StringBuffer sb = new StringBuffer(1024);
        String query_sql = "select * from " + TABLE_USER_RESOURCE 
                            +"  where " + COLUMN_USER_ID + " = " + user_id ;

        try
        {
            
            Method getDatabase = rep.getClass().getDeclaredMethod("getDatabase") ;
            Object database = getDatabase.invoke(rep) ;
            Method getConnection = database.getClass().getDeclaredMethod("getConnection") ;
            Connection conn = (Connection)getConnection.invoke(database) ;
            
            try
            {
                Statement stt = conn.createStatement() ;
                ResultSet rs = stt.executeQuery(query_sql) ;
                
                int count = 0 ;
                while (rs.next())
                {
                    if (count > 0)
                        sb.append(",") ;
                    int resourceID = rs.getInt(COLUMN_RESOURCE_ID) ;
                    int resource_type_ID = rs.getInt(COLUMN_RESOURCE_TYPE_ID) ;
                    sb.append(getResourceID(resourceID,resource_type_ID)) ;
                    count ++ ;
                }
                
                rs.close() ;
                stt.close() ;
                
            }
            catch(SQLException e)
            {
            	logger.error(e.getMessage(), e) ;
                System.err.println(query_sql) ;
                return "" ;
            }
        } 
        catch (Exception e) 
        {
            logger.error(e.getMessage(),e);
        }
       
//        System.out.println(sb.toString());
        return sb.toString();        
    }
    
    /**
     * Get connection object from a connected database-based repository.
     * 
     * 
     * */
    public static Connection getRepConnection(Object rep)
    {
        Object conn = null ;
        try
        {
            Class<?> kettleDatabaseRepositoryClass = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepository", true, classLoaderUtil) ;
            Class<?> databaseClass =  Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil); 
            Method getDatabase = kettleDatabaseRepositoryClass.getDeclaredMethod("getDatabase") ;
            Method getConnection = databaseClass.getDeclaredMethod("getConnection") ;
            Object database = getDatabase.invoke(rep) ;
            conn = getConnection.invoke(database) ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e) ;
        }
        
        return (Connection)conn ;
    }

    @Override
    public void authResourcesToUser(String rep_name, String user_id,
            String resource_ids)
    {
        
        final String insert_ps = " insert into " + TABLE_USER_RESOURCE 
                               + "("+COLUMN_USER_ID+","+COLUMN_RESOURCE_ID+","+COLUMN_RESOURCE_TYPE_ID
                               +") values(?,?,?);" ;
        final String delete_sql = " delete from " + TABLE_USER_RESOURCE + " where " + COLUMN_USER_ID + " = " ;
        try
        {
            Object rep = getRepFromDatabase(rep_name,Constants.get("LoginUser"),Constants.get("LoginPassword")) ;
            Connection conn = getRepConnection(rep);
            try
            {
                boolean isAutoCommit = conn.getAutoCommit() ;
                if (isAutoCommit)
                    conn.setAutoCommit(false) ;
                
                Statement stt = conn.createStatement() ;
                stt.executeUpdate(delete_sql+user_id ) ;
                
                PreparedStatement ps = conn.prepareStatement(insert_ps) ;
                String[] ids = resource_ids.split(",") ;
                for (String id : ids)
                {
                	if (id.length() == 0)
                		continue ;
                    int resource_id = getResourceID(id) ;
                    int resource_type = getResourceType(id) ;
                    ps.setInt(1, Integer.parseInt(user_id)) ;
                    ps.setInt(2, resource_id) ;
                    ps.setInt(3, resource_type) ;
                    ps.addBatch() ;
                }
                ps.executeBatch() ;
                conn.commit() ;
                ps.close() ;
                stt.close() ;
                conn.setAutoCommit(isAutoCommit) ;
            }
            catch (Exception e)
            {
            	logger.error(e.getMessage(), e) ;
            }
            Method disconnect = rep.getClass().getDeclaredMethod("disconnect") ;
            disconnect.invoke(rep) ;
        }
        catch (Exception e)
        {
        	logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String getResourceTreeJSON(String rep_name, String user_id)
    {
        return getResourceTree(rep_name, user_id) ;
    }
    
    @Override
    public String getXML(String repName, String fileType, String actionPath, String actionRef){
    	String xml = "";
    	Method disconnect = null;
        Object rep = null;
        boolean connected = false;

        try{
    		rep = getRep(repName);
    		connected = true;
    		
    		Object directory = getDirectory(rep, actionPath);
    		
    		if(fileType.equalsIgnoreCase(TYPE_TRANS)){
                //get trans xml
                Method loadTransformation = rep.getClass().getDeclaredMethod("loadTransformation", new Class[] {String.class, 
                        Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), 
                        boolean.class, String.class});
                Object transMeta = loadTransformation.invoke(rep, actionRef, directory, null, true, null);
                
                Method getXML = transMeta.getClass().getDeclaredMethod("getXML");
                xml = (String)getXML.invoke(transMeta);
                
            }else if(fileType.equalsIgnoreCase(TYPE_JOB)){
                //get job xml
                Method loadJob = rep.getClass().getDeclaredMethod("loadJob", String.class, 
                        Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), 
                        Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), String.class);
                Object jobMeta = loadJob.invoke(rep, actionRef, directory, null, null);
                
                Method getXML = jobMeta.getClass().getDeclaredMethod("getXML");
                xml = (String)getXML.invoke(jobMeta);
            }
    		xml = xml.replaceAll("\r", "");
    		xml = xml.replaceAll("\n", "");
    		xml = xml.replaceAll("\\\\", "\\\\\\\\");
    		disconnect = rep.getClass().getDeclaredMethod("disconnect");
            disconnect.invoke(rep);
            connected = false;
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
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
    	
    	return xml;
    }
    
    @Override
	public String getActiveDetails(String repName, String fileType, String actionPath, String actionRef, String monitor_id_logchannel){
    	StringBuffer statusJSON = new StringBuffer();
        try{
    		if(fileType.equalsIgnoreCase(TYPE_TRANS)){
                //get trans status
    			for(Object trans : activeTrans){
    				//get trans repname,if equal continue
//    				Method getTransMeta = trans.getClass().getDeclaredMethod("getTransMeta");
//    				Object transMeta = getTransMeta.invoke(trans);
//    				Method getRepository = transMeta.getClass().getDeclaredMethod("getRepository");
//    				Object rep = getRepository.invoke(transMeta);
//    				Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
//    				Method getRepName = repositoryClass.getDeclaredMethod("getName");
//    				String trans_repName = (String)getRepName.invoke(rep);
//    				if(trans_repName.equals(repName)){
//    					//get trans repDirectory,if equal continue
//    					Method getRepDict = transMeta.getClass().getDeclaredMethod("getRepositoryDirectory");
//    					Object repDict = getRepDict.invoke(transMeta);
//    					Class<?> repDictClass = Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil);
//    					Method getDictName = repDictClass.getDeclaredMethod("getPath");
//    					String trans_dictName = (String)getDictName.invoke(repDict);
//    					if(trans_dictName.equals(actionPath)){
//    						//get trans name,if equal continue
//    						Method getTransName = trans.getClass().getDeclaredMethod("getName");
//    						String trans_name = (String)getTransName.invoke(trans);
//    						if(trans_name.equals(actionRef)){
//    							Method getBatchId = trans.getClass().getDeclaredMethod("getBatchId");
//    							int trans_batch_id = getBatchId.invoke(trans)==null?-1:((Long)getBatchId.invoke(trans)).intValue();
//    							if((trans_batch_id == -1 || monitor_batch_id == trans_batch_id) && statusJSON.length() == 0){
    							Method getLogChannelId = trans.getClass().getDeclaredMethod("getLogChannelId");
    							String logChannelId = (String)getLogChannelId.invoke(trans);
    							if(logChannelId.equals(monitor_id_logchannel)){
    								statusJSON.append("{objs:[");
    								//get steps status
        							Method getTransStepExecutionStatusLookup = trans.getClass().getDeclaredMethod("getTransStepExecutionStatusLookup");
        							Object[] statusLookup = (Object[])getTransStepExecutionStatusLookup.invoke(trans);
        							
        							//get steps
        							Method getSteps = trans.getClass().getDeclaredMethod("getSteps");
        							List<Object> steps = (List<Object>)getSteps.invoke(trans);
        							for(int i=0;i<steps.size();i++){
        								int state = 0;
        								//create status json string
        								Object step = steps.get(i);
        								Field stepNameField = step.getClass().getDeclaredField("stepname");
        								String stepName = (String)stepNameField.get(step);
        								
        								Enum stepStatus = (Enum)statusLookup[i];
        								String stateName = stepStatus.name();    						
        								if(STATUS_RUNNING.equals(stateName)){
        									state = 1;
        								}else if(STATUS_FINISHED.equals(stateName)){
        									state = 2;
        								}else if(STATUS_STOPPED.equals(stateName)){
        									state = 0;
        								}else {
        									state = 3;
        								}
        								
        								
        								if(i > 0){
        									statusJSON.append(",");
        								}
        								
        								statusJSON.append("{name:'")
    									  .append(stepName)
    									  .append("',state:'")
    									  .append(state)
    									  .append("'}");
        							}
        							statusJSON.append("]}");
    							}
//    						}
//    					}
//    				}
    			}
            }else if(fileType.equalsIgnoreCase(TYPE_JOB)){
                //get job status
            	for(Object job : activeJobs){
//    				Method getRepository = job.getClass().getDeclaredMethod("getRep");
//    				Object rep = getRepository.invoke(job);
//    				Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
//    				Method getRepName = repositoryClass.getDeclaredMethod("getName");
//    				String job_repName = (String)getRepName.invoke(rep);
//    				if(job_repName.equals(repName)){
//    					//get job repDirectory,if equal continue
    					Method getJobMeta = job.getClass().getDeclaredMethod("getJobMeta");
        				Object jobMeta = getJobMeta.invoke(job);
//    					Method getRepDict = jobMeta.getClass().getDeclaredMethod("getRepositoryDirectory");
//    					Object repDict = getRepDict.invoke(jobMeta);
//    					Class<?> repDictClass = Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil);
//    					Method getDictName = repDictClass.getDeclaredMethod("getPath");
//    					String job_dictName = (String)getDictName.invoke(repDict);
//    					if(job_dictName.equals(actionPath)){
//    						//get job name,if equal continue
//    						Method getJobName = job.getClass().getDeclaredMethod("getJobname");
//    						String job_name = (String)getJobName.invoke(job);
//    						if(job_name.equals(actionRef)){
//    							Method getBatchId = job.getClass().getDeclaredMethod("getBatchId");
//    							int job_batch_id = getBatchId.invoke(job)==null?-1:((Long)getBatchId.invoke(job)).intValue();
//    							if((job_batch_id == -1 || monitor_batch_id == job_batch_id) && statusJSON.length() == 0){
    							Method getLogChannelId = job.getClass().getDeclaredMethod("getLogChannelId");
    							String logChannelId = (String)getLogChannelId.invoke(job);
    							if(logChannelId.equals(monitor_id_logchannel)){
    								statusJSON.append("{objs:[");
    								//getActiveJobs
        							Method getActiveJobEntryTransformations = job.getClass().getDeclaredMethod("getActiveJobEntryTransformations");
        							Map<Object, Object> activeJobEntryTrans = (Map<Object, Object>)getActiveJobEntryTransformations.invoke(job);
        							Method getActiveJobEntryJobs = job.getClass().getDeclaredMethod("getActiveJobEntryJobs");
        							Map<Object, Object> activeJobEntryJobs = (Map<Object, Object>)getActiveJobEntryJobs.invoke(job);
        							//get job results
        							Method getJobEntryResults = job.getClass().getDeclaredMethod("getJobEntryResults");
        							List<Object> jobEntryResults = (List<Object>)getJobEntryResults.invoke(job);
        							//get job copies
        							Method getJobCopies = jobMeta.getClass().getDeclaredMethod("getJobCopies");
        							List<Object> jobCopies = (List<Object>)getJobCopies.invoke(jobMeta); 
        							//set state:0 as have not run,1 as running,2 as finished
        							for(int i=0;i<jobCopies.size();i++){
        								int state = 0;
        								Method getEntryName = jobCopies.get(i).getClass().getDeclaredMethod("getName");
        								String entryName = (String)getEntryName.invoke(jobCopies.get(i));
        								
        								for(int j=0;j<jobEntryResults.size();j++){
        									Object jobEntryResult = jobEntryResults.get(j);
        									Method getJobEntryName_ = jobEntryResult.getClass().getDeclaredMethod("getJobEntryName");
        									Object jobEntryName_ = getJobEntryName_.invoke(jobEntryResult);
        									
        									if(jobEntryName_ != null){
        										if(jobEntryName_.equals(entryName)){
        											state = 2;
        											break;
        										}
        									}
        								}
        								
//        								if(state == 0){
        									if(activeJobEntryJobs != null){
            									if(activeJobEntryJobs.get(jobCopies.get(i)) != null){
            										state = 1;
            									}
            								}
        									if(activeJobEntryTrans != null){
            									if(activeJobEntryTrans.get(jobCopies.get(i)) != null){
            										state = 1;
            									}
            								}
//        								}

        								if(i > 0){
        									statusJSON.append(",");
        								}
        								
        								statusJSON.append("{name:'")
    									  .append(entryName)
    									  .append("',state:'")
    									  .append(state)
    									  .append("'}");
        							}
        							statusJSON.append("]}");
    							}
//    						}
//    					}
//    				}
            	}
            }
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
        } 
        if(statusJSON.length() == 0){
        	statusJSON.append("{objs:[]}");
        }
    	return statusJSON.toString();
	}

	@Override
	public String getTransStepsLog(String repName,
			String actionPath, String actionRef, int id_batch) {
		StringBuffer resultsJSON = new StringBuffer();
		Method repDisconnect = null;
		Method databaseDisconnect = null;
        Object rep = null;
        boolean connected = false;
        Object database = null;
		
		try{
			rep = getRep(repName);
    		connected = true;
    		
    		Object directory = getDirectory(rep, actionPath);
    		
    		Method loadTransformation = rep.getClass().getDeclaredMethod("loadTransformation", new Class[] {String.class, 
                     Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), 
                     boolean.class, String.class});
            Object transMeta = loadTransformation.invoke(rep, actionRef, directory, null, true, null);
             
            Method getStepLogTable = transMeta.getClass().getDeclaredMethod("getStepLogTable");
            Object stepLogTable = getStepLogTable.invoke(transMeta);
            Class<?> baseLogTableClass = Class.forName("org.pentaho.di.core.logging.BaseLogTable", true, classLoaderUtil);
            Method getTableName = baseLogTableClass.getDeclaredMethod("getTableName");
            String setpLogTableName = getTableName.invoke(stepLogTable)==null?null:(String)getTableName.invoke(stepLogTable);
            
            if(setpLogTableName != null){
            	resultsJSON.append("{OBJS:[");
            	
            	Method getFields = baseLogTableClass.getDeclaredMethod("getFields");
            	List<?> logFields = (List<?>)getFields.invoke(stepLogTable);
            	
            	Method getDatabaseMeta = baseLogTableClass.getDeclaredMethod("getDatabaseMeta");
            	Object databaseMeta = getDatabaseMeta.invoke(stepLogTable);
            	
            	Class<?> loggingObjectInterface = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
            	Class<?> databaseClass = Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil);
            	Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
            	
            	// open a connection
            	database = databaseConstructor.newInstance(null , databaseMeta);
            	Class<?> variableSpaceClass = Class.forName("org.pentaho.di.core.variables.VariableSpace", true, classLoaderUtil);
            	Method shareVariablesWith = database.getClass().getDeclaredMethod("shareVariablesWith", variableSpaceClass);
            	shareVariablesWith.invoke(database, transMeta);
            	Method connect = database.getClass().getDeclaredMethod("connect");
            	connect.invoke(database);
            	
            	// First, we get the information out of the database table...
            	Method getQuotedSchemaTableCombination = baseLogTableClass.getDeclaredMethod("getQuotedSchemaTableCombination");
            	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(stepLogTable);
            	
            	Method getName = transMeta.getClass().getDeclaredMethod("getName");
            	String transName = (String)getName.invoke(transMeta);
            	
            	StringBuilder sql = new StringBuilder();
            	sql.append("SELECT DISTINCT * FROM ")
            	   .append(schemaTable)
            	   .append(" WHERE ID_BATCH=")
            	   .append(id_batch)
            	   .append(" AND TRANSNAME='")
            	   .append(transName)
            	   .append("'");
            	   
            	Method openQuery = database.getClass().getDeclaredMethod("openQuery", String.class);
            	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
            	
            	boolean first = true;
            	Method getRow = database.getClass().getDeclaredMethod("getRow", ResultSet.class);
    			Object[] rowData = (Object[])getRow.invoke(database, resultSet);
    			
            	while(rowData != null){
            		if(first){
            			resultsJSON.append("{");
            		}else {
            			resultsJSON.append(",{");
            		}
            		
            		for(int i=0;i<logFields.size();i++){
            			Object logField = logFields.get(i);
            			Method getId = logField.getClass().getDeclaredMethod("getId");
            			String fieldName = (String)getId.invoke(logField);
            			
            			if(i>0){
            				resultsJSON.append(",");
            			}
            			
            			if(rowData[i] instanceof Integer || rowData[i] instanceof Long){
            				resultsJSON.append(fieldName)
            						   .append(":")
            						   .append(rowData[i]);
            			}else if(rowData[i] instanceof String){
            				String data = (String)rowData[i];
            				data = data.replaceAll("'", "\\\\'");
            				resultsJSON.append(fieldName)
 						   .append(":'")
 						   .append(data)
 						   .append("'");
            			}else {
            				resultsJSON.append(fieldName)
  						   .append(":'")
  						   .append(rowData[i])
  						   .append("'");
            			}
            			
            		}
            		
            		first = false;
            		resultsJSON.append("}");
            		rowData = (Object[])getRow.invoke(database, resultSet);
            	}
            	resultsJSON.append("]}");
            	resultSet.close();
            }
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally {
			try {
                if(connected){
                    repDisconnect = rep.getClass().getDeclaredMethod("disconnect");
                    repDisconnect.invoke(rep);
                }
                if (database != null){
                	databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                	databaseDisconnect.invoke(database);
                }
                    
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            } 
		}
		
		return resultsJSON.toString();
	}
	
	@Override
	public String getJobEntriesLog(String repName,
			String actionPath, String actionRef, int id_batch) {
		StringBuffer resultsJSON = new StringBuffer();
		Method repDisconnect = null;
		Method databaseDisconnect = null;
        Object rep = null;
        boolean connected = false;
        Object database = null;
		
		try{
			rep = getRep(repName);
    		connected = true;
    		
    		Object directory = getDirectory(rep, actionPath);
    		
    		Method loadJob = rep.getClass().getDeclaredMethod("loadJob", String.class, 
                     Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), 
                     Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), String.class);
            Object jobMeta = loadJob.invoke(rep, actionRef, directory, null, null);
             
            Method getJobEntryLogTable = jobMeta.getClass().getDeclaredMethod("getJobEntryLogTable");
            Object jobEntryLogTable = getJobEntryLogTable.invoke(jobMeta);
            Class<?> baseLogTableClass = Class.forName("org.pentaho.di.core.logging.BaseLogTable", true, classLoaderUtil);
            Method getTableName = baseLogTableClass.getDeclaredMethod("getTableName");
            String jobEntryLogTableName = getTableName.invoke(jobEntryLogTable)==null?null:(String)getTableName.invoke(jobEntryLogTable);
             
            if(jobEntryLogTableName != null){
            	resultsJSON.append("{OBJS:[");
            	
            	Method getFields = baseLogTableClass.getDeclaredMethod("getFields");
            	List<?> logFields = (List<?>)getFields.invoke(jobEntryLogTable);
            	
            	Method getDatabaseMeta = baseLogTableClass.getDeclaredMethod("getDatabaseMeta");
            	Object databaseMeta = getDatabaseMeta.invoke(jobEntryLogTable);
            	
            	Class<?> loggingObjectInterface = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
            	Class<?> databaseClass = Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil);
            	Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
            	
            	// open a connection
            	database = databaseConstructor.newInstance(null , databaseMeta);
            	Class<?> variableSpaceClass = Class.forName("org.pentaho.di.core.variables.VariableSpace", true, classLoaderUtil);
            	Method shareVariablesWith = database.getClass().getDeclaredMethod("shareVariablesWith", variableSpaceClass);
            	shareVariablesWith.invoke(database, jobMeta);
            	Method connect = database.getClass().getDeclaredMethod("connect");
            	connect.invoke(database);
            	
            	// First, we get the information out of the database table...
            	Method getQuotedSchemaTableCombination = baseLogTableClass.getDeclaredMethod("getQuotedSchemaTableCombination");
            	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(jobEntryLogTable);
            	
            	Method getName = jobMeta.getClass().getDeclaredMethod("getName");
            	String jobName = (String)getName.invoke(jobMeta);
            	
            	StringBuilder sql = new StringBuilder();
            	sql.append("SELECT * FROM ")
            	   .append(schemaTable)
            	   .append(" WHERE ID_BATCH=")
            	   .append(id_batch)
            	   .append(" AND TRANSNAME='")
            	   .append(jobName)
            	   .append("' GROUP BY CHANNEL_ID");
            	   
            	Method openQuery = database.getClass().getDeclaredMethod("openQuery", String.class);
            	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
            	
            	boolean first = true;
            	Method getRow = database.getClass().getDeclaredMethod("getRow", ResultSet.class);
    			Object[] rowData = (Object[])getRow.invoke(database, resultSet);
    			
            	while(rowData != null){
            		if(first){
            			resultsJSON.append("{");
            		}else {
            			resultsJSON.append(",{");
            		}
            		
            		for(int i=0;i<logFields.size();i++){
            			Object logField = logFields.get(i);
            			Method getId = logField.getClass().getDeclaredMethod("getId");
            			String fieldName = (String)getId.invoke(logField);
            			
            			if(i>0){
            				resultsJSON.append(",");
            			}
            			
            			if(rowData[i] instanceof Integer || rowData[i] instanceof Long){
            				resultsJSON.append(fieldName)
            						   .append(":")
            						   .append(rowData[i]);
            			}else if(rowData[i] instanceof String){
            				String data = (String)rowData[i];
            				data = data.replaceAll("'", "\\\\'");
            				resultsJSON.append(fieldName)
 						   .append(":'")
 						   .append(data)
 						   .append("'");
            			}else {
            				resultsJSON.append(fieldName)
  						   .append(":'")
  						   .append(rowData[i])
  						   .append("'");
            			}
            			
            		}
            		
            		first = false;
            		resultsJSON.append("}");
            		rowData = (Object[])getRow.invoke(database, resultSet);
            	}
            	resultsJSON.append("]}");
            	resultSet.close();
            }
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally {
			try {
                if(connected){
                    repDisconnect = rep.getClass().getDeclaredMethod("disconnect");
                    repDisconnect.invoke(rep);
                }
                if (database != null){
                	databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                	databaseDisconnect.invoke(database);
                }
                    
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            } 
		}
		
		return resultsJSON.toString();
	}
	
	@Override
	public MonitorScheduleBean getMonitorDataFromJobLogTable(
			MonitorScheduleBean monitorScheduleBean, UserBean userBean) {

		Method repDisconnect = null;
		Method databaseDisconnect = null;
        Object rep = null;
        boolean connected = false;
        Object database = null;
		
		try{
			JobDetail jobDetail = QuartzUtil.findByJobName(monitorScheduleBean.getJobName(), userBean);
			
			String fileType = jobDetail.getJobDataMap().getString("fileType")==null?"":jobDetail.getJobDataMap().getString("fileType");
			if(KettleEngine.TYPE_TRANS.equals(fileType)){
				return monitorScheduleBean;
			}
			
			String repName = jobDetail.getJobDataMap().getString("repName")==null?"Default":jobDetail.getJobDataMap().getString("repName");
			rep = getRep(repName);
    		connected = true;
    		
    		boolean isFastConfig = jobDetail.getJobDataMap().getBoolean("isFastConfig");
    		Object directory = null;
    		String jobName = "";
    		if(isFastConfig){
    			String fastConfigJson = jobDetail.getJobDataMap().getString("fastConfigJson");
        		FastConfigView fastConfigView = JSON.parseObject(fastConfigJson, FastConfigView.class); 
        		String middlePath = "Template" + fastConfigView.getIdSourceType() + fastConfigView.getIdDestType() + fastConfigView.getLoadType();
        		
        		directory = getDirectory(rep, Template.TEMPLATE_PATH + "/" + middlePath);
        		jobName = Template.JOB_NAME;
    		}else {
    			String actionPath = jobDetail.getJobDataMap().getString("actionPath")==null?"":jobDetail.getJobDataMap().getString("actionPath");
    			directory = getDirectory(rep, actionPath);
    			jobName = jobDetail.getJobDataMap().getString("actionRef")==null?"":jobDetail.getJobDataMap().getString("actionRef");
    		}
    		
    		Method loadJob = rep.getClass().getDeclaredMethod("loadJob", String.class, 
                     Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), 
                     Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), String.class);
            Object jobMeta = loadJob.invoke(rep, jobName, directory, null, null);
             
            Method getJobLogTable = jobMeta.getClass().getDeclaredMethod("getJobLogTable");
            Object jobLogTable = getJobLogTable.invoke(jobMeta);
            Class<?> baseLogTableClass = Class.forName("org.pentaho.di.core.logging.BaseLogTable", true, classLoaderUtil);
            Method getTableName = baseLogTableClass.getDeclaredMethod("getTableName");
            String jobLogTableName = getTableName.invoke(jobLogTable)==null?null:(String)getTableName.invoke(jobLogTable);
             
            if(jobLogTableName != null){
            	Method getDatabaseMeta = baseLogTableClass.getDeclaredMethod("getDatabaseMeta");
            	Object databaseMeta = getDatabaseMeta.invoke(jobLogTable);
            	
            	Class<?> loggingObjectInterface = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
            	Class<?> databaseClass = Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil);
            	Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
            	
            	// open a connection
            	database = databaseConstructor.newInstance(null , databaseMeta);
            	Class<?> variableSpaceClass = Class.forName("org.pentaho.di.core.variables.VariableSpace", true, classLoaderUtil);
            	Method shareVariablesWith = database.getClass().getDeclaredMethod("shareVariablesWith", variableSpaceClass);
            	shareVariablesWith.invoke(database, jobMeta);
            	Method connect = database.getClass().getDeclaredMethod("connect");
            	connect.invoke(database);
            	
            	// First, we get the information out of the database table...
            	Method getQuotedSchemaTableCombination = baseLogTableClass.getDeclaredMethod("getQuotedSchemaTableCombination");
            	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(jobLogTable);
            	
            	StringBuilder sql = new StringBuilder();
            	sql.append("SELECT * FROM ")
            	   .append(schemaTable)
            	   .append(" WHERE CHANNEL_ID='")
            	   .append(monitorScheduleBean.getId_logchannel())
            	   .append("' ORDER BY ID_JOB ASC");
            	   
            	Method openQuery = database.getClass().getDeclaredMethod("openQuery", String.class);
            	ResultSet rs = (ResultSet)openQuery.invoke(database, sql.toString());
            	
            	while(rs.next()){
            		monitorScheduleBean.setLines_error(rs.getInt("ERRORS"));
    				monitorScheduleBean.setLines_input(rs.getInt("LINES_INPUT"));
    				monitorScheduleBean.setLines_output(rs.getInt("LINES_OUTPUT"));
    				monitorScheduleBean.setLines_updated(rs.getInt("LINES_UPDATED"));
    				monitorScheduleBean.setLines_read(rs.getInt("LINES_READ"));
    				monitorScheduleBean.setLines_written(rs.getInt("LINES_WRITTEN"));
    				monitorScheduleBean.setLines_deleted(rs.getInt("LINES_REJECTED"));
    				String log_field = rs.getString("LOG_FIELD")==null?"":rs.getString("LOG_FIELD").replaceAll("\\s(?=[0-9]{4}/[0-9])", "<BR>");
    				monitorScheduleBean.setLogMsg(log_field);
            	}
            	rs.close();
            }
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally {
			try {
                if(connected){
                    repDisconnect = rep.getClass().getDeclaredMethod("disconnect");
                    repDisconnect.invoke(rep);
                }
                if (database != null){
                	databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                	databaseDisconnect.invoke(database);
                }
                    
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            } 
		}
		
		return monitorScheduleBean;
	}
	
	@Override
	public String getJobEntryErrorLog(String jobEntryName, String repName,
			String actionPath, String actionRef, int id_batch) {
		StringBuffer errorLog = new StringBuffer();
        Object rep = null;
        boolean connected = false;
        Object database = null;
		
		try{
			rep = getRep(repName);
    		connected = true;
    		
    		Object directory = getDirectory(rep, actionPath);
    		
    		Method loadJob = rep.getClass().getDeclaredMethod("loadJob", String.class, 
                     Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil), 
                     Class.forName("org.pentaho.di.core.ProgressMonitorListener", true, classLoaderUtil), String.class);
            Object jobMeta = loadJob.invoke(rep, actionRef, directory, null, null);
             
            //get the selected jobEntry
            Method getJobCopies = jobMeta.getClass().getDeclaredMethod("getJobCopies");
            List<Object> listJobEntryCopies = (List<Object>)getJobCopies.invoke(jobMeta);
            Object jobEntry = null;
            String entryName = "";
            for(Object jobEntryCopy : listJobEntryCopies){
            	Method getEntry = jobEntryCopy.getClass().getDeclaredMethod("getEntry");
            	jobEntry = getEntry.invoke(jobEntryCopy);
            	Class<?> jobEntryBaseClass = Class.forName("org.pentaho.di.job.entry.JobEntryBase", true, classLoaderUtil);
            	Method getName = jobEntryBaseClass.getDeclaredMethod("getName");
            	entryName = (String)getName.invoke(jobEntry);
            	if(entryName.equals(jobEntryName)){
            		break;
            	}
            }
            
            Class<?> baseLogTableClass = Class.forName("org.pentaho.di.core.logging.BaseLogTable", true, classLoaderUtil);
            Method getTableName = baseLogTableClass.getDeclaredMethod("getTableName");
            Method getDatabaseMeta = baseLogTableClass.getDeclaredMethod("getDatabaseMeta");
            Class<?> loggingObjectInterface = Class.forName("org.pentaho.di.core.logging.LoggingObjectInterface", true, classLoaderUtil);
        	Class<?> databaseClass = Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil);
        	Class<?> variableSpaceClass = Class.forName("org.pentaho.di.core.variables.VariableSpace", true, classLoaderUtil);
        	Method shareVariablesWith = databaseClass.getDeclaredMethod("shareVariablesWith", variableSpaceClass);
        	Method connect = databaseClass.getDeclaredMethod("connect");
        	Method getQuotedSchemaTableCombination = baseLogTableClass.getDeclaredMethod("getQuotedSchemaTableCombination");
        	Method getName = jobMeta.getClass().getDeclaredMethod("getName");
        	String jobName = (String)getName.invoke(jobMeta);
        	Method openQuery = databaseClass.getDeclaredMethod("openQuery", String.class);
        	Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
    		
        	//get errorLog from jobEntryLogTable
            Method getJobEntryLogTable = jobMeta.getClass().getDeclaredMethod("getJobEntryLogTable");
            Object jobEntryLogTable = getJobEntryLogTable.invoke(jobMeta);
            String jobEntryLogTableName = getTableName.invoke(jobEntryLogTable)==null?null:(String)getTableName.invoke(jobEntryLogTable);
            
            if(jobEntryLogTableName != null){
            	Object databaseMeta = getDatabaseMeta.invoke(jobEntryLogTable);
            	Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
            	
            	// open a connection
            	database = databaseConstructor.newInstance(null , databaseMeta);
            	shareVariablesWith.invoke(database, jobMeta);
            	connect.invoke(database);
            	
            	// First, we get the information out of the database table...
            	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(jobEntryLogTable);
            	
            	StringBuilder sql = new StringBuilder();
            	sql.append("SELECT DISTINCT * FROM ")
            	   .append(schemaTable)
            	   .append(" WHERE ID_BATCH=")
            	   .append(id_batch)
            	   .append(" AND TRANSNAME='")
            	   .append(jobName)
            	   .append("' AND STEPNAME='")
            	   .append(jobEntryName)
            	   .append("'");
            	   
            	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
            	
            	if(resultSet.next()){
            		errorLog.append(resultSet.getString("LOG_FIELD"));
            	}
            	
            	resultSet.close();
            	Method databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
            	databaseDisconnect.invoke(database);
            }
        	
        	if(jobEntry.getClass().getName().endsWith("JobEntryTrans")){
    			Object transMeta = null;
    			String trans_logChannelId = "";
    			int trans_id_batch = -1;
    			//JOB ENTRY TRANS
    			//1:get channel_id from ChannelLogTable by jobEntryName
    			Method getChannelLogTable = jobMeta.getClass().getDeclaredMethod("getChannelLogTable");
    			Object channelLogTable = getChannelLogTable.invoke(jobMeta);
    			String channelLogTableName = getTableName.invoke(channelLogTable)==null?null:(String)getTableName.invoke(channelLogTable);
    			if(channelLogTableName != null){
    				Object databaseMeta = getDatabaseMeta.invoke(channelLogTable);
    				Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
    				
    				// open a connection
                	database = databaseConstructor.newInstance(null , databaseMeta);
                	shareVariablesWith.invoke(database, jobMeta);
                	connect.invoke(database);
                	
                	// First, we get the information out of the database table...
                	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(channelLogTable);
    				
                	StringBuilder sql = new StringBuilder();
                	sql.append("SELECT DISTINCT * FROM ")
                	   .append(schemaTable)
                	   .append(" WHERE ID_BATCH=")
                	   .append(id_batch)
                	   .append(" AND OBJECT_NAME='")
                	   .append(jobEntryName)
                	   .append("' AND LOGGING_OBJECT_TYPE='TRANS'");
                	   
                	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
                	if(resultSet.next()){
                		trans_logChannelId = resultSet.getString("CHANNEL_ID");
                	}
                	
                	resultSet.close();
                	Method databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                	databaseDisconnect.invoke(database);
    			}
    			
    			//2:get id_batch from TransLogTable by channel_id
    			if(trans_logChannelId != null && !"".equals(trans_logChannelId)){
    				Class<?> jobEntryTransClass = Class.forName("org.pentaho.di.job.entries.trans.JobEntryTrans", true, classLoaderUtil);
    				Method getTransMeta = jobEntryTransClass.getDeclaredMethod("getTransMeta", repositoryClass, variableSpaceClass);
    				transMeta = getTransMeta.invoke(jobEntry, rep, jobEntry);
    				Method getTransLogTable = transMeta.getClass().getDeclaredMethod("getTransLogTable");
    				Object transLogTable = getTransLogTable.invoke(transMeta);
    				String transLogTableName = getTableName.invoke(transLogTable)==null?null:(String)getTableName.invoke(transLogTable);
    				if(transLogTableName != null){
    					Object databaseMeta = getDatabaseMeta.invoke(transLogTable);
        				Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
        				
        				// open a connection
                    	database = databaseConstructor.newInstance(null , databaseMeta);
                    	shareVariablesWith.invoke(database, jobMeta);
                    	connect.invoke(database);
                    	
                    	// First, we get the information out of the database table...
                    	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(transLogTable);
                    	
                    	StringBuilder sql = new StringBuilder();
                    	sql.append("SELECT DISTINCT * FROM ")
                    	   .append(schemaTable)
                    	   .append(" WHERE CHANNEL_ID='")
                    	   .append(trans_logChannelId)
                    	   .append("' AND TRANSNAME='")
                    	   .append(jobEntryName)
                    	   .append("'");
                    	
                    	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
                    	if(resultSet.next()){
                    		trans_id_batch = resultSet.getInt("ID_BATCH");
                    	}
                    	
                    	resultSet.close();
                    	Method databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                    	databaseDisconnect.invoke(database);
    				}
    			}
    			
    			//3:get log_field from StepLogTable by id_batch and errors>0
    			if(trans_id_batch != -1){
    				Method getStepLogTable = transMeta.getClass().getDeclaredMethod("getStepLogTable");
    				Object stepLogTable = getStepLogTable.invoke(transMeta);
    				String stepLogTableName = getTableName.invoke(stepLogTable)==null?null:(String)getTableName.invoke(stepLogTable);
    				
    				if(stepLogTableName != null){
    					Object databaseMeta = getDatabaseMeta.invoke(stepLogTable);
        				Constructor<?> databaseConstructor = databaseClass.getConstructor(loggingObjectInterface, databaseMeta.getClass());
        				
        				// open a connection
                    	database = databaseConstructor.newInstance(null , databaseMeta);
                    	shareVariablesWith.invoke(database, jobMeta);
                    	connect.invoke(database);
                    	
                    	// First, we get the information out of the database table...
                    	String schemaTable = (String)getQuotedSchemaTableCombination.invoke(stepLogTable);
                    	
                    	StringBuilder sql = new StringBuilder();
                    	sql.append("SELECT DISTINCT * FROM ")
                    	   .append(schemaTable)
                    	   .append(" WHERE ID_BATCH=")
                    	   .append(trans_id_batch)
                    	   .append(" AND TRANSNAME='")
                    	   .append(jobEntryName)
                    	   .append("' AND ERRORS>0");
                    	
                    	ResultSet resultSet = (ResultSet)openQuery.invoke(database, sql.toString());
                    	while(resultSet.next()){
                    		if(errorLog.length() > 0){
                    			errorLog.append("\\r\\n");
                    		}
                    		errorLog.append(resultSet.getString("LOG_FIELD"));
                    	}
                    	
                    	resultSet.close();
                    	Method databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                    	databaseDisconnect.invoke(database);
    				}
    			}
    		}
		}catch(Exception e){
			StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
			errorLog.append(sw.toString());
			logger.error(e.getMessage(), e);
		}finally {
			try {
                if(connected){
                    Method repDisconnect = rep.getClass().getDeclaredMethod("disconnect");
                    repDisconnect.invoke(rep);
                }
                if (database != null){
                	Method databaseDisconnect = database.getClass().getDeclaredMethod("disconnect");
                	databaseDisconnect.invoke(database);
                	database = null;
                }
                    
            } catch (Exception e) {
                logger.error(e.getMessage(),e);
            } 
		}
		
		return errorLog.toString();
	}
    
    @Override
	public String[] getSlaveNames(String repName){
		try{
			Object rep = getRep(repName);
			Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
			Method getSlaveNames = repositoryClass.getDeclaredMethod("getSlaveNames", boolean.class);
			
			String[] slaveNames = (String[])getSlaveNames.invoke(rep, false);
			return slaveNames;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return null;
		}
	}
    
    private SlaveServerBean getSlaveServerBean(String remoteServer, String ha) throws Exception{
    	SlaveServerBean slaveServerBean = new SlaveServerBean();
    	if(!"".equals(remoteServer)){
    		slaveServerBean = SlaveServerUtil.getSlaveServer(remoteServer);
    	}else if(!"".equals(ha)){
    		slaveServerBean = SlaveServerUtil.getBestServerFromCluster(ha);
    	}
    	
    	return slaveServerBean;
    }
    
    /**
     * create instance of SlaveServer using platform slave configuration
     * @param remoteServer
     * @param ha
     * @return
     * @throws Exception
     */
    private Object createSlaveServer(SlaveServerBean slaveServerBean) throws Exception{
        boolean isMaster = false;
        if("1".equals(slaveServerBean.getMaster())){
        	isMaster = true;
        }
        Class<?> slaveServerClass = Class.forName("org.pentaho.di.cluster.SlaveServer", true, classLoaderUtil);
		Constructor<?> slaveServerConstructor = slaveServerClass
				.getConstructor(String.class, String.class,
						String.class, String.class, String.class,
						String.class, String.class, String.class,
						boolean.class);
		Object slaveServer = slaveServerConstructor.newInstance(
				slaveServerBean.getName(),
				slaveServerBean.getHost_name(),
				slaveServerBean.getPort(),
				slaveServerBean.getUsername(),
				slaveServerBean.getPassword(),
				slaveServerBean.getProxy_host_name(),
				slaveServerBean.getProxy_port(),
				slaveServerBean.getNon_proxy_hosts(), isMaster);
		
		return slaveServer;
    }
    
    /**
	 * 主要的实现方式是通过类KettleDatabaseRepository的createRepositorySchema方法来获取
	 * 
	 */
	public void createRepository(RepositoryBean repBean,boolean update) {
		try {
			//获取DatabaseMeta实例
			Class<?> databaseMetaClass = Class.forName(
					"org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil);
			Constructor<?> databaseMetaConstructor = databaseMetaClass.getConstructor(
					String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class);
			Object databaseMeta = databaseMetaConstructor.newInstance(
									repBean.getRepositoryName(),repBean.getDbType(), 
									repBean.getDbAccess(), repBean.getDbHost(), 
									repBean.getDbName(), repBean.getDbPort(),
									repBean.getUserName(),repBean.getPassword());	
			//获取database 实例
			Class<?> databaseClass = Class.forName(
					"org.pentaho.di.core.database.Database", true,classLoaderUtil);
			Constructor<?> databaseConstructor = databaseClass.getConstructor(
					Class.forName("org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil));
			Object database = databaseConstructor.newInstance(databaseMeta);
			Method connect = database.getClass().getDeclaredMethod("connect");
			connect.invoke(database);
			
			String filePath = "dbTables/tables_mysql_repo.sql";
			Connection conn = null;
			Statement stmt = null;
			PreparedStatement pstmt = null;
			try{
				Method getConnection = database.getClass().getDeclaredMethod("getConnection");
				conn = (Connection)getConnection.invoke(database);
				stmt = conn.createStatement();
				logger.info("Execute sql file start:" + filePath);
				DataBaseUtil.executeSQLScript(stmt, filePath);
				logger.info("Execute sql file end:" + filePath);
				
//				Method getPluginId = databaseMetaClass.getDeclaredMethod("getPluginId");
//				Object pluginId = getPluginId.invoke(databaseMeta);
//				
//				Field databaseDelegateField = kettleDatabaseRepositoryClass.getDeclaredField("databaseDelegate");
//				Object databaseDelegate = databaseDelegateField.get(kettleDatabaseRepository);
//				Method getDatabaseTypeID = databaseDelegate.getClass().getDeclaredMethod("getDatabaseTypeID", String.class);
//				Object databaseTypeId = getDatabaseTypeID.invoke(databaseDelegate, (String)pluginId);
//				
//				Method getId = databaseTypeId.getClass().getDeclaredMethod("getId");
//				Object databaseTypeid = getId.invoke(databaseTypeId);
//				
				int id=1;
				conn.setAutoCommit(false);
				String sql = "INSERT INTO R_DATABASE (ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME) values (?,?,?,?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);
				pstmt.setObject(1, id);
				pstmt.setObject(2, "template");
				pstmt.setObject(3, repBean.getUserName());
				pstmt.setObject(4, Encr.encryptPasswordIfNotUsingVariables(repBean.getPassword()));
				pstmt.setObject(5, 28);
				pstmt.setObject(6, "1");
				pstmt.setObject(7, repBean.getDbName());
				pstmt.setObject(8, repBean.getDbPort());
				pstmt.setObject(9, repBean.getDbHost());
				pstmt.execute();
				conn.commit();
				conn.setAutoCommit(true);
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}finally{
				try{
					if(stmt != null){
						stmt.close();
					}
					if(pstmt != null){
						pstmt.close();
					}

				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
			
			Method disconnect = database.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(database);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String checkTableExist(RepositoryBean repBean,String tableName){
		String isExist = "true";
		try{
			//获取DatabaseMeta实例
			Class<?> databaseMetaClass = Class.forName(
					"org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil);
			Constructor<?> databaseMetaConstructor = databaseMetaClass.getConstructor(
					String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class);
			Object databaseMeta = databaseMetaConstructor.newInstance(
									repBean.getRepositoryName(),repBean.getDbType(), 
									repBean.getDbAccess(), repBean.getDbHost(), 
									repBean.getDbName(), repBean.getDbPort(),
									repBean.getUserName(),repBean.getPassword());	
			//获取database 实例
			Class<?> databaseClass = Class.forName(
					"org.pentaho.di.core.database.Database", true,classLoaderUtil);
			Constructor<?> databaseConstructor = databaseClass.getConstructor(
					Class.forName("org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil));
			Object database = databaseConstructor.newInstance(databaseMeta);
			Method connect = database.getClass().getDeclaredMethod("connect");
			connect.invoke(database);
			Method checkTableExist = database.getClass().getDeclaredMethod("checkTableExists", String.class);
			isExist = String.valueOf(checkTableExist.invoke(database, tableName));
			Method disconnect = database.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(database);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			isExist = "error";
		}
		return isExist;
	}
	
	public void addNewTables(RepositoryBean repBean){
		try{
			//获取DatabaseMeta实例
			Class<?> databaseMetaClass = Class.forName(
					"org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil);
			Constructor<?> databaseMetaConstructor = databaseMetaClass.getConstructor(
					String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class);
			Object databaseMeta = databaseMetaConstructor.newInstance(
									repBean.getRepositoryName(),repBean.getDbType(), 
									repBean.getDbAccess(), repBean.getDbHost(), 
									repBean.getDbName(), repBean.getDbPort(),
									repBean.getUserName(),repBean.getPassword());	
			//获取database 实例
			Class<?> databaseClass = Class.forName(
					"org.pentaho.di.core.database.Database", true,classLoaderUtil);
			Constructor<?> databaseConstructor = databaseClass.getConstructor(
					Class.forName("org.pentaho.di.core.database.DatabaseMeta", true,classLoaderUtil));
			Object database = databaseConstructor.newInstance(databaseMeta);
			Method connect = database.getClass().getDeclaredMethod("connect");
			connect.invoke(database);
			String statement="";
			if("false".equals(checkTableExist(repBean, TABLE_USER_RESOURCE)))
				statement="CREATE TABLE " + 
						TABLE_USER_RESOURCE + 
						"(" + COLUMN_USER_ID + " INTEGER," + 
						COLUMN_RESOURCE_ID + " INTEGER," + 
						COLUMN_RESOURCE_TYPE_ID + " INTEGER);";  
			if("false".equals(checkTableExist(repBean, TABLE_CACHE_FILE)))
				statement=statement+"CREATE TABLE " + TABLE_CACHE_FILE + 
				"(" + COLUMN_CACHE_FILE_ID + " INTEGER," + 
				COLUMN_CACHE_FILE_NAME + " VARCHAR(255)," + 
				COLUMN_CACHE_FILE_FILEPATH + " VARCHAR(255)," + 
				COLUMN_CACHE_FILE_MEMORYSIZE + " INTEGER," + 
				COLUMN_CACHE_FILE_INDEXTYPE + " INTEGER);"; 
			if(!"".equals(statement)){
				Method executeStatement = database.getClass().getDeclaredMethod("execStatements", String.class);
				executeStatement.invoke(database, statement);				
			}
			Method disconnect = database.getClass().getDeclaredMethod("disconnect");
			disconnect.invoke(database);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}		
	}
	
	public String stopRunning(String repName, String fileType, String actionPath, String actionRef, MonitorScheduleBean monitorBean){
    	StringBuffer statusJSON = new StringBuffer();
    	int batch_id = 0;
        try{
    		if(fileType.equalsIgnoreCase(TYPE_TRANS)){
    			List<Object> stopTrans = new ArrayList<Object>();
                //get trans status
    			for(Object trans : activeTrans){
    				//get trans repname,if equal continue
    				Method getTransMeta = trans.getClass().getDeclaredMethod("getTransMeta");
    				Object transMeta = getTransMeta.invoke(trans);
    				Method getRepository = transMeta.getClass().getDeclaredMethod("getRepository");
    				Object rep = getRepository.invoke(transMeta);
    				Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
    				Method getRepName = repositoryClass.getDeclaredMethod("getName");
    				String trans_repName = (String)getRepName.invoke(rep);
    				if(trans_repName.equals(repName)){
    					//get trans repDirectory,if equal continue
    					Method getRepDict = transMeta.getClass().getDeclaredMethod("getRepositoryDirectory");
    					Object repDict = getRepDict.invoke(transMeta);
    					Class<?> repDictClass = Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil);
    					Method getDictName = repDictClass.getDeclaredMethod("getPath");
    					String trans_dictName = (String)getDictName.invoke(repDict);
    					if(trans_dictName.equals(actionPath)){
    						//get trans name,if equal continue
    						Method getTransName = trans.getClass().getDeclaredMethod("getName");
    						String trans_name = (String)getTransName.invoke(trans);
    						if(trans_name.equals(actionRef)){
    							stopTrans.add(trans);
    						}
    					}
    				}
    			}
    			for(Object stop_trans : stopTrans){
					Method stopAll = stop_trans.getClass().getDeclaredMethod("stopAll");
					stopAll.invoke(stop_trans);
					Field running = stop_trans.getClass().getDeclaredField("running");
					running.set(stop_trans, false);
					Field initialized = stop_trans.getClass().getDeclaredField("initialized");
					initialized.set(stop_trans, false);
					Field halted = stop_trans.getClass().getDeclaredField("halted");
					halted.set(stop_trans, false);
					
//					Method getBatchId = stop_trans.getClass().getDeclaredMethod("getBatchId");
//                	batch_id = getBatchId.invoke(stop_trans)==null?-1:((Long)getBatchId.invoke(stop_trans)).intValue();
//                	Date start_date = StringUtil.StringToDate(monitorBean.getStartTime(), "yyyy-MM-dd HH:mm:ss");
//                	monitor(monitorBean.getId(), start_date, MonitorUtil.STATUS_STOPPED, 
//                			monitorBean.getId_logchannel(), null, 0, batch_id);
//                    if(stop_trans != null && activeTrans.contains(stop_trans)){
//                    	activeTrans.remove(stop_trans);
//                    }
				}
            }else if(fileType.equalsIgnoreCase(TYPE_JOB)){
                //get job status
            	List<Object> stopJobs = new ArrayList<Object>();
            	for(Object job : activeJobs){
    				Method getRepository = job.getClass().getDeclaredMethod("getRep");
    				Object rep = getRepository.invoke(job);
    				Class<?> repositoryClass = Class.forName("org.pentaho.di.repository.Repository", true, classLoaderUtil);
    				Method getRepName = repositoryClass.getDeclaredMethod("getName");
    				String job_repName = (String)getRepName.invoke(rep);
    				if(job_repName.equals(repName)){
//    					//get job repDirectory,if equal continue
    					Method getJobMeta = job.getClass().getDeclaredMethod("getJobMeta");
        				Object jobMeta = getJobMeta.invoke(job);
    					Method getRepDict = jobMeta.getClass().getDeclaredMethod("getRepositoryDirectory");
    					Object repDict = getRepDict.invoke(jobMeta);
    					Class<?> repDictClass = Class.forName("org.pentaho.di.repository.RepositoryDirectoryInterface", true, classLoaderUtil);
    					Method getDictName = repDictClass.getDeclaredMethod("getPath");
    					String job_dictName = (String)getDictName.invoke(repDict);
    					if(job_dictName.equals(actionPath)){
    						//get job name,if equal continue
    						Method getJobName = job.getClass().getDeclaredMethod("getJobname");
    						String job_name = (String)getJobName.invoke(job);
    						if(job_name.equals(actionRef)){
    							Method isActive = job.getClass().getDeclaredMethod("isActive");
    							boolean active = (Boolean)isActive.invoke(job);
    							Method isInitialized = job.getClass().getDeclaredMethod("isInitialized");
    							boolean initialized = (Boolean)isInitialized.invoke(job);
    							if (job != null && active && initialized) {
    								stopJobs.add(job);
    							}
    						}
    					}
    				}
            	}
            	for(Object stopJob : stopJobs){
					Method stopAll = stopJob.getClass().getDeclaredMethod("stopAll");
					stopAll.invoke(stopJob);
					Method waitUntilFinished = stopJob.getClass().getDeclaredMethod("waitUntilFinished", long.class);
					// wait until everything is stopped, maximum 5 seconds...
					waitUntilFinished.invoke(stopJob, 5000);
//					Method getBatchId = stopJob.getClass().getDeclaredMethod("getBatchId");
//                	batch_id = getBatchId.invoke(stopJob)==null?-1:((Long)getBatchId.invoke(stopJob)).intValue();
//                	Date start_date = StringUtil.StringToDate(monitorBean.getStartTime(), "yyyy-MM-dd HH:mm:ss");
//                	monitor(monitorBean.getId(), start_date, MonitorUtil.STATUS_STOPPED, 
//                			monitorBean.getId_logchannel(), null, 0, batch_id);
//                    if(stopJob != null && activeJobs.contains(stopJob)){
//                    	activeJobs.remove(stopJob);
//                    }
				}
            }
    	}catch(Exception e){
    		logger.error(e.getMessage(), e);
        } 
        if(statusJSON.length() == 0){
        	statusJSON.append("{objs:[]}");
        }
    	return statusJSON.toString();
	}
}
