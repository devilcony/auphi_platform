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
package com.auphi.data.hub.core.idgenerator;


import com.auphi.data.hub.dao.SystemDao;


/**
 * ID生成器 静态类解决多线程并发访问生成ID的问题
 * 此类第一次实例化会执行所有的static代码块，如果想按需加载这些ID生成器，则应该一个ID写一个静态类就可以
 * @author zhangjiafeng
 */
public class IDHelper {
	
	private SystemDao systemDao;

	/**
	 * 事件跟踪ID
	 */
	private static DefaultIDGenerator defaultIDGenerator_eventid = null;

	/**
	 * SpringBean监控ID
	 */
	private static DefaultIDGenerator defaultIDGenerator_monitorid = null;

	/**
	 * 项目ID(测试表)
	 */
	private static DefaultIDGenerator defaultIDGenerator_xmid = null;

	/**
	 * CODEID
	 */
	private static DefaultIDGenerator defaultIDGenerator_codeid = null;

	/**
	 * EXCEPTIONID
	 */
	private static DefaultIDGenerator defaultIDGenerator_exceptionid = null;
	
	/**
	 * AUTHORIZEID_ROLE
	 */
	private static DefaultIDGenerator defaultIDGenerator_authorizeid_role = null;
	
	/**
	 * PARAMID
	 */
	private static DefaultIDGenerator defaultIDGenerator_paramid = null;
	
	/**
	 * ROLEID
	 */
	private static DefaultIDGenerator defaultIDGenerator_roleid = null;
	
	/**
	 * AUTHORIZEID_USERMENUMAP
	 */
	private static DefaultIDGenerator defaultIDGenerator_authorizeid_usermenumap = null;
	
	/**
	 * AUTHORIZEID_USER
	 */
	private static DefaultIDGenerator defaultIDGenerator_authorizeid_user = null;
	
	/**
	 * USERID
	 */
	private static DefaultIDGenerator defaultIDGenerator_userid = null;
	
	/**
	 * FILEID
	 */
	private static DefaultIDGenerator defaultIDGenerator_fileid = null;
	
	/**
	 * PARTID
	 */
	private static DefaultIDGenerator defaultIDGenerator_partid = null;
	
	/**
	 * AUTHORIZEID_EAROLEAUTHORIZE
	 */
	private static DefaultIDGenerator defaultIDGenerator_authorizeid_earoleauthorize = null;
	
	/**
	 * AUTHORIZEID_EAUSERAUTHORIZE
	 */
	private static DefaultIDGenerator defaultIDGenerator_authorizeid_eauserauthorize = null;


	/**
	 * 返回事件跟踪ID
	 * 
	 * @return
	 */
	public static String getEventID(SystemDao systemDao) {
		IdGenerator idGenerator_eventid = new IdGenerator();
		idGenerator_eventid.setFieldname("EVENTID");
		defaultIDGenerator_eventid = idGenerator_eventid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_eventid.create(systemDao);
	}

	/**
	 * 返回SpringBean监控ID
	 * 
	 * @return
	 */
	public static String getMonitorID(SystemDao systemDao) {
		IdGenerator idGenerator_monitorid = new IdGenerator();
		idGenerator_monitorid.setFieldname("MONITORID");
		defaultIDGenerator_monitorid = idGenerator_monitorid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_monitorid.create(systemDao);
	}

	/**
	 * 返回项目ID
	 * 
	 * @return
	 */
	public static String getXmID(SystemDao systemDao) {
		IdGenerator idGenerator_xmid = new IdGenerator();
		idGenerator_xmid.setFieldname("XMID");
		defaultIDGenerator_xmid = idGenerator_xmid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_xmid.create(systemDao);
	}

	/**
	 * 返回CODEID
	 * 
	 * @return
	 */
	public static String getCodeID(SystemDao systemDao) {
		IdGenerator idGenerator_codeid = new IdGenerator();
		idGenerator_codeid.setFieldname("CODEID");
		defaultIDGenerator_codeid = idGenerator_codeid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_codeid.create(systemDao);
	}
	
	/**
	 * 返回ExceptionID
	 * 
	 * @return
	 */
	public static String getExceptionID(SystemDao systemDao) {
		IdGenerator idGenerator_exceptionid = new IdGenerator();
		idGenerator_exceptionid.setFieldname("EXCEPTIONID");
		defaultIDGenerator_exceptionid = idGenerator_exceptionid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_exceptionid.create(systemDao);
	}
	
	/**
	 * 返回AUTHORIZEID_ROLE
	 * 
	 * @return
	 */
	public static String getAuthorizeid4Role(SystemDao systemDao) {
		IdGenerator idGenerator_authorizeid_role = new IdGenerator();
		idGenerator_authorizeid_role.setFieldname("AUTHORIZEID_ROLE");
		defaultIDGenerator_authorizeid_role = idGenerator_authorizeid_role.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_authorizeid_role.create(systemDao);
	}
	
	/**
	 * 返回PARAMID
	 * 
	 * @return
	 */
	public static String getParamID(SystemDao systemDao) {
		IdGenerator idGenerator_paramid = new IdGenerator();
		idGenerator_paramid.setFieldname("PARAMID");
		defaultIDGenerator_paramid = idGenerator_paramid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_paramid.create(systemDao);
	}
	
	/**
	 * 返回ROLEID
	 * 
	 * @return
	 */
	public static String getRoleID(SystemDao systemDao) {
		IdGenerator idGenerator_roleid = new IdGenerator();
		idGenerator_roleid.setFieldname("ROLEID");
		defaultIDGenerator_roleid = idGenerator_roleid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_roleid.create(systemDao);
	}
	
	/**
	 * 返回AUTHORIZEID_USERMENUMAP
	 * 
	 * @return
	 */
	public static String getAuthorizeid4Usermenumap(SystemDao systemDao) {
		IdGenerator idGenerator_authorizeid_usermenumap = new IdGenerator();
		idGenerator_authorizeid_usermenumap.setFieldname("AUTHORIZEID_USERMENUMAP");
		defaultIDGenerator_authorizeid_usermenumap = idGenerator_authorizeid_usermenumap.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_authorizeid_usermenumap.create(systemDao);
	}
	
	/**
	 * 返回AUTHORIZEID_USER
	 * 
	 * @return
	 */
	public static String getAuthorizeid4User(SystemDao systemDao) {
		IdGenerator idGenerator_authorizeid_user = new IdGenerator();
		idGenerator_authorizeid_user.setFieldname("AUTHORIZEID_USER");
		defaultIDGenerator_authorizeid_user = idGenerator_authorizeid_user.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_authorizeid_user.create(systemDao);
	}
	
	/**
	 * 返回USERID
	 * 
	 * @return
	 */
	public static String getUserID(SystemDao systemDao) {
		IdGenerator idGenerator_userid = new IdGenerator();
		idGenerator_userid.setFieldname("USERID");
		defaultIDGenerator_userid = idGenerator_userid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_userid.create(systemDao);
	}
	
	/**
	 * 返回FILEID
	 * 
	 * @return
	 */
	public static String getFileID(SystemDao systemDao) {
		IdGenerator idGenerator_fileid = new IdGenerator();
		idGenerator_fileid.setFieldname("FILEID");
		defaultIDGenerator_fileid = idGenerator_fileid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_fileid.create(systemDao);
	}
	
	/**
	 * 返回PARTID
	 * 
	 * @return
	 */
	public static String getPartID(SystemDao systemDao) {
		IdGenerator idGenerator_partid = new IdGenerator();
		idGenerator_partid.setFieldname("PARTID");
		defaultIDGenerator_partid = idGenerator_partid.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_partid.create(systemDao);
	}
	
	/**
	 * 返回Authorizeid
	 * 
	 * @return
	 */
	public static String getAuthorizeid4Earoleauthorize(SystemDao systemDao) {
		IdGenerator idGenerator_authorizeid_earoleauthorize = new IdGenerator();
		idGenerator_authorizeid_earoleauthorize.setFieldname("AUTHORIZEID_EAROLEAUTHORIZE");
		defaultIDGenerator_authorizeid_earoleauthorize = idGenerator_authorizeid_earoleauthorize.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_authorizeid_earoleauthorize.create(systemDao);
	}
	
	/**
	 * 返回Authorizeid
	 * 
	 * @return
	 */
	public static String getAuthorizeid4Eauserauthorize(SystemDao systemDao) {
		IdGenerator idGenerator_authorizeid_eauserauthorize = new IdGenerator();
		idGenerator_authorizeid_eauserauthorize.setFieldname("PARTID");
		defaultIDGenerator_authorizeid_eauserauthorize = idGenerator_authorizeid_eauserauthorize.getDefaultIDGenerator(systemDao);
		return defaultIDGenerator_authorizeid_eauserauthorize.create(systemDao);
	}
}
