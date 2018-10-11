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
package com.aofei.kettle.service;



public interface KettleEngine {
    public static final String TYPE_DIR = "kdir" ;
	public static final String TYPE_JOB = "kjb";
    public static final String TYPE_TRANS = "ktr";
    
    final int type_dir = 1 ;
    final int type_job = 2 ;
    final int type_trans = 3 ;
    public static final String TYPE_DIR_SUFFIX = "_dir";
    public static final String TYPE_JOB_SUFFIX = "_job";
    public static final String TYPE_TRANS_SUFFIX = "_trans";
	
    public static final String TABLE_USER_RESOURCE = "KDI_T_USER_RESOURCE";
    public static final String COLUMN_USER_ID = "C_USER_ID" ;
    public static final String COLUMN_RESOURCE_ID = "C_RESOURCE_ID" ;
    public static final String COLUMN_RESOURCE_TYPE_ID = "C_RESOURCE_TYPE_ID" ;    
    
    public static final String TABLE_CACHE_FILE = "R_CACHE_FILE";
    public static final String COLUMN_CACHE_FILE_ID = "ID_CACHE_FILE";
    public static final String COLUMN_CACHE_FILE_NAME = "NAME";
    public static final String COLUMN_CACHE_FILE_FILEPATH = "FILEPATH";
    public static final String COLUMN_CACHE_FILE_MEMORYSIZE = "MEMORYSIZE";
    public static final String COLUMN_CACHE_FILE_INDEXTYPE = "INDEXTYPE";
    
    public static final String VERSION_2_3 = "V2.0";
    public static final String VERSION_3_2 = "3.2";
    public static final String VERSION_4_3 = "V3.0";
    
    
    public static final String STATUS_RUNNING = "STATUS_RUNNING";
    public static final String STATUS_FINISHED = "STATUS_FINISHED";
    public static final String STATUS_STOPPED = "STATUS_STOPPED";
    
    public static final int EXECTYPE_LOCAL = 1;
	public static final int EXECTYPE_REMOTE = 2;
	public static final int EXECTYPE_CLUSTER = 3;
	public static final int EXECTYPE_HA = 4;
	
	/**
	 * execute a trans or job by a repository file
	 * @param repName
	 * @param filePath
	 * @param fileName
	 * @param fileType
	 * @param execType
	 * @param remoteServer
	 * @return
	 * @throws Exception
	 */
	public boolean execute(String repName, String filePath, String fileName, String fileType, int id, int execType, String remoteServer, String ha) throws Exception;
	
	/**
	 * get a repository tree described by a JSON string
	 * @param repName
	 * @return
	 */
	public String getRepTreeJSON(String repName, String user_id);
	
	/**
	 * check username,password is correct or not
	 * @param repName
	 * @return
	 */
	public int checkRepLogin(String repName);
	
	/**
	 * Authorize resources to user.
	 * 
	 * @param rep_name : repository name
	 * @param user_id : user id
	 * @param resource_ids : string contains resource IDs, split by ','
	 * 
	 * 
	 * */
	public void authResourcesToUser(String rep_name, String user_id, String resource_ids) ;
	
    
	/**
	 * Get JSON string of repostiory's resource tree. 
	 * 
	 * */
    public String getResourceTreeJSON(String rep_name, String user_id) ;
    
    /**
     * get xml data from repository
     * @param repName
     * @param actionPath
     * @param actionRef
     * @return
     */
    public String getXML(String repName, String fileType, String actionPath, String actionRef);
    
    /**
     * Get JSON String of running trans`s/job`s details
     * @param repName
     * @param fileType
     * @param actionPath
     * @param actionRef
     * @param id_logchannel
     * @return
     */
    public String getActiveDetails(String repName, String fileType, String actionPath, String actionRef, String id_logchannel);
    
    /**
     * Get JSON String of trans steps results
     * @param repName
     * @param actionPath
     * @param actionRef
     * @return
     */
    public String getTransStepsLog(String repName, String actionPath, String actionRef, int batch_id);
    
    /**
     * Get JSON String of trans steps results
     * @param repName
     * @param actionPath
     * @param actionRef
     * @return
     */
    public String getJobEntriesLog(String repName, String actionPath, String actionRef, int batch_id);
    

    /**
     * get slave server names from the repository
     * @param repName repository name
     * @return
     */
    public String[] getSlaveNames(String repName); 
    

	
	/**
	 * get job entry error log
	 * @param jobEntryName
	 * @param repName
	 * @param actionPath
	 * @param actionRef
	 * @param batch_id
	 * @return
	 */
	public String getJobEntryErrorLog(String jobEntryName, String repName, String actionPath, String actionRef, int batch_id);
	

	public Object getRepFromDatabase(String repName, String username, String password) throws Exception;
}
