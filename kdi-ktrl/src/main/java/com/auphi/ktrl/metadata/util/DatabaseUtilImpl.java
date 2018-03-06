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

import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.util.ClassLoaderUtil;

public class DatabaseUtilImpl implements DatabaseUtil {
	private static Logger logger = Logger.getLogger(DatabaseUtilImpl.class);
	
	public String getDataSourcePassword(MetaDataConnBean connBean,String userName,ClassLoaderUtil classLoaderUtil){
		Object password = null;
		try{
			
			Class<?> kttleEnvironmentClass = Class.forName("org.pentaho.di.core.KettleEnvironment", true, classLoaderUtil);
			Method env_init = kttleEnvironmentClass.getDeclaredMethod("init");
			env_init.invoke(kttleEnvironmentClass);
			
			
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
			Method KettleDatabaseRepositoryInitMethod = KettleDatabaseRepositoryClazz.getMethod("init",KettleDatabaseRepositoryMetaClazz.getInterfaces());
			KettleDatabaseRepositoryInitMethod.invoke(KettleDatabaseRepositoryInstance,KettleDatabaseRepositoryMetaInstance);
			
			Field field = KettleDatabaseRepositoryClazz.getDeclaredField("connectionDelegate");
			Object connectionDelegate = field.get(KettleDatabaseRepositoryInstance);
			Method method = connectionDelegate.getClass().getDeclaredMethod("connect");
			method.invoke(connectionDelegate);
			
			Class<?> IUserClazz = Class.forName("org.pentaho.di.repository.IUser", true, classLoaderUtil);
			Class<?> UserInfoClazz = Class.forName("org.pentaho.di.repository.UserInfo", true, classLoaderUtil);
			Object UserInfoInstance = UserInfoClazz.newInstance();
			
			field = KettleDatabaseRepositoryClazz.getDeclaredField("userDelegate");
			Object userDelegate = field.get(KettleDatabaseRepositoryInstance);
			method = userDelegate.getClass().getMethod("loadUserInfo", IUserClazz,String.class);
			Object user = method.invoke(userDelegate, UserInfoInstance,userName);
			
			method = user.getClass().getMethod("getPassword");
			password = method.invoke(user);
			//System.out.println("userName:"+userName+",password:"+password);
			
			closeConnect(KettleDatabaseRepositoryClazz,KettleDatabaseRepositoryInstance);
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return password+"";
	}
	
	/**
	 * get connection
	 * @param connConfigBean
	 * @return
	 */
	public Connection getConnection(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil) throws Exception
	{
		try{
			//ClassLoaderUtil classLoaderUtil = new ClassLoaderUtil(); 
			//classLoaderUtil.loadJarPath("kettle/4.3/");
			long start = System.currentTimeMillis();
//			Class<?> kttleEnvironmentClass = Class.forName("org.pentaho.di.core.KettleEnvironment", true, classLoaderUtil);
//			Method init = kttleEnvironmentClass.getDeclaredMethod("init");
//			init.invoke(kttleEnvironmentClass);
			
			Class<?> databaseMetaClazz = Class.forName("org.pentaho.di.core.database.DatabaseMeta", true, classLoaderUtil);
			Class<?> databaseClazz = Class.forName("org.pentaho.di.core.database.Database", true, classLoaderUtil);
			
			
			Constructor<?> databaseMetaConstructor = databaseMetaClazz.getConstructor(String.class, String.class, 
					String.class, String.class, String.class, String.class, String.class, String.class);
			Object databaseMeta = databaseMetaConstructor.newInstance(connBean.getName(), connBean.getType(),connBean.getAccess(),connBean.getServer(),
					connBean.getDatabase(),connBean.getPort(),connBean.getUsername(),connBean.getPassword());
			
			Constructor<?> databaseConstructor = databaseClazz.getConstructor(databaseMetaClazz);
			Object database = databaseConstructor.newInstance(databaseMeta);
			
			//connect to the database
			Method connect = databaseClazz.getDeclaredMethod("connect");
			connect.invoke(database);
			
			//return a connection
			Method getConnection = databaseClazz.getDeclaredMethod("getConnection");
			Connection conn = (Connection)getConnection.invoke(database);
			
			//System.out.println(conn.getMetaData().getSchemas().next());
//			//close databse connect
//			Method disconnect = databaseClass.getDeclaredMethod("disconnect");
//			disconnect.invoke(database);
			
			//System.out.println("cost[ms]:"+(System.currentTimeMillis() - start));
			
			return conn;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw new Exception(e);
		}
	}
	
	public List<MetaDataConnBean> getDataSources(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil,String userName,String password){
		List<MetaDataConnBean> retList = new ArrayList<MetaDataConnBean>();
		try{
			
			
			//KettleEnvironment.init();
//			Class<?> kttleEnvironmentClass = Class.forName("org.pentaho.di.core.KettleEnvironment", true, classLoaderUtil);
//			Method env_init = kttleEnvironmentClass.getDeclaredMethod("init");
//			env_init.invoke(kttleEnvironmentClass);
			
			
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
			
			
			//KettleDatabaseRepository rep = new KettleDatabaseRepository(); 
			Class<?> KettleDatabaseRepositoryClazz = Class.forName("org.pentaho.di.repository.kdr.KettleDatabaseRepository", true, classLoaderUtil);
			Object KettleDatabaseRepositoryInstance = KettleDatabaseRepositoryClazz.newInstance();
	        //rep.init(repInfo);
			Method KettleDatabaseRepositoryInitMethod = KettleDatabaseRepositoryClazz.getMethod("init",KettleDatabaseRepositoryMetaClazz.getInterfaces());
			KettleDatabaseRepositoryInitMethod.invoke(KettleDatabaseRepositoryInstance,KettleDatabaseRepositoryMetaInstance);
			
			//rep.connect("admin", "admin");
			Method KettleDatabaseRepositoryConnectMethod = KettleDatabaseRepositoryClazz.getMethod("connect",String.class,String.class);
			KettleDatabaseRepositoryConnectMethod.invoke(KettleDatabaseRepositoryInstance,userName, password);
			
			//List<DatabaseMeta>  databaseMetaList = rep.getDatabases();
			Method KettleDatabaseRepositorygetDatabasesMethod = KettleDatabaseRepositoryClazz.getMethod("getDatabases");
			List<Object> databaseMetaObjects = (List<Object>)KettleDatabaseRepositorygetDatabasesMethod.invoke(KettleDatabaseRepositoryInstance);
			
			if(databaseMetaObjects != null)
			{
				for(Object database:databaseMetaObjects){
					Class<?> databaseClazz = database.getClass();
					
					//database.getName()
					Method getNameMethod=databaseClazz.getMethod("getName");
					Object nameObject = getNameMethod.invoke(database);
					//database.getHostname()
					Method getHostnameMethod=databaseClazz.getMethod("getHostname");
					Object hostNameObject = getHostnameMethod.invoke(database);
					//database.getDatabaseName()
					Method getDatabaseNameMethod=databaseClazz.getMethod("getDatabaseName");
					Object databaseNameObject = getDatabaseNameMethod.invoke(database);
					//database.getDatabasePortNumberString()
					Method getDatabasePortNumberStringMethod=databaseClazz.getMethod("getDatabasePortNumberString");
					Object portObject = getDatabasePortNumberStringMethod.invoke(database);
					//database.getUsername()
					Method getUsernameMethod=databaseClazz.getMethod("getUsername");
					Object userNameObject = getUsernameMethod.invoke(database);
					//database.getPassword()
					Method getPasswordMethod=databaseClazz.getMethod("getPassword");
					Object passwordObject = getPasswordMethod.invoke(database);
					//database.getDatabaseTypeDesc()
					Method getDatabaseTypeDescMethod=databaseClazz.getMethod("getDatabaseTypeDesc");
					Object databaseTypeObject = getDatabaseTypeDescMethod.invoke(database);
					//database.getAccessTypeDesc()
					Method getAccessTypeDescMethod=databaseClazz.getMethod("getAccessTypeDesc");
					Object accessTypeDescObject = getAccessTypeDescMethod.invoke(database);
					
					//System.out.println("nameObject="+nameObject);
					//System.out.println("hostNameObject="+hostNameObject);
					//System.out.println("databaseNameObject="+databaseNameObject);
					//System.out.println("portObject="+portObject);
					//System.out.println("userNameObject="+userNameObject);
					//System.out.println("passwordObject="+passwordObject);
					//System.out.println("databaseTypeObject="+databaseTypeObject);
					//System.out.println("accessTypeDescObject="+accessTypeDescObject);
					//System.out.println();
					
					MetaDataConnBean bean = new MetaDataConnBean();
					
					bean.setName(nameObject+"");
					bean.setServer(hostNameObject+"");
					bean.setDatabase(databaseNameObject+"");
					bean.setPort(portObject+"");
					bean.setUsername(userNameObject+"");
					bean.setPassword(passwordObject+"");
					bean.setType(databaseTypeObject+"");
					bean.setAccess(accessTypeDescObject+"");
					
					retList.add(bean);
				}
			}
			
			closeConnect(KettleDatabaseRepositoryClazz,KettleDatabaseRepositoryInstance);
		}
		catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return retList;
	
	}
	/**
	 * 获取模式名
	 * @param connBean
	 * @param classLoaderUtil
	 * @return
	 */
	public List<String> getSchemas(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil) //throws Exception
	{
		List<String> retList = new ArrayList<String>();
		
		ResultSet schemas = null;
		Connection connection = null;
		try{
			connection = this.getConnection(connBean,classLoaderUtil);
			DatabaseMetaData metaData = connection.getMetaData();
			schemas = metaData.getSchemas();
			int schema_rowcount = 0;
			while(schemas.next()){
				String schema_name = schemas.getString(1);
				retList.add(schema_name);
				if(schema_rowcount == 0)
					schema_rowcount++;
			}
			if(schema_rowcount == 0)
			{
				String schema_name = Messages.getString("Metadata.Message.DefaultName");
				retList.add(schema_name);
			}
			connection.close();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return retList;
	}
	/**
	 * 获取模式下的表
	 * @param connBean
	 * @param classLoaderUtil
	 * @param schema
	 * @return
	 */
	public List<String> getTables(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil,String schemaName)
	{
		List<String> retList = new ArrayList<String>();
		
		ResultSet schemas = null;
		ResultSet tables = null;
		Connection connection = null;
		try
		{
			connection = this.getConnection(connBean,classLoaderUtil);
			DatabaseMetaData metaData = connection.getMetaData();
			schemas = metaData.getSchemas();
			
			String table_name = "";
			int schema_rowcount = 0;
			if(schemaName != null && !Messages.getString("Metadata.Message.DefaultName").equals(schemaName))
			{
				while(schemas.next()){
				    schema_rowcount++;
				    if(schemas.getString(1).equalsIgnoreCase(schemaName))
				    {
                        String schema_name = schemas.getString(1);
                        tables = metaData.getTables(null, schema_name, null,null);
                        while (tables.next())
                        {
                            table_name = tables.getString(3);
                            retList.add(table_name);
                        }
                        break;
				    }
				    else
				        continue;
				}
			}
			if(schema_rowcount == 0){//if the database donot have schema
				tables = metaData.getTables(null, null, null, null);
				while(tables.next()){
					table_name = tables.getString(3);
					retList.add(table_name);
				}
			}
			connection.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
		}
		return retList;
	}
	
	/**
	 * 获取字段名
	 * @param connBean
	 * @param classLoaderUtil
	 * @param schemaName
	 * @param tableName
	 * @return
	 */
	public List<String> getFields(MetaDataConnBean connBean,ClassLoaderUtil classLoaderUtil,String schemaName,String tableName)
	{
		List<String> retList = new ArrayList<String>();
		
		ResultSet schemas = null;
		ResultSet columns = null;
		Connection connection = null;
		try
		{
			connection = this.getConnection(connBean,classLoaderUtil);
			DatabaseMetaData metaData = connection.getMetaData();
			schemas = metaData.getSchemas();
			
			String column_name = "";
			int column_rowcount = 0;
			if(schemaName != null && !Messages.getString("Metadata.Message.DefaultName").equals(schemaName) && tableName != null && !"".equals(tableName))
			{
				while(schemas.next()){
					String schema_name = schemas.getString(1);
					if(schema_name.equals(schemaName))
					{
						columns = metaData.getColumns(null, schema_name, tableName, null);
						while(columns.next()){
							column_name = columns.getString(4);
							retList.add(column_name);
						}
					}
				}
			}
			if(column_rowcount == 0){//if the database donot have schema
				columns = metaData.getColumns(null, null, tableName, null);
				while(columns.next()){
					column_name = columns.getString(4);
					retList.add(column_name);
				}
			}
			connection.close();
		}
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
		}
		return retList;
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
