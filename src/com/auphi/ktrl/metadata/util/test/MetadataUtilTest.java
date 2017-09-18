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
package com.auphi.ktrl.metadata.util.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import junit.framework.TestCase;

import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.metadata.util.DatabaseUtil;
import com.auphi.ktrl.metadata.util.DatabaseUtilImpl;
import com.auphi.ktrl.metadata.util.KettleUtil;
import com.auphi.ktrl.metadata.util.KettleUtilImpl;
import com.auphi.ktrl.metadata.util.MetadataUtil;
import com.auphi.ktrl.util.InitServlet;

public class MetadataUtilTest extends TestCase {
	private static Logger logger = Logger.getLogger(MetadataUtilTest.class);
	/**
	 * 测试能正确输出树结构的json数据
	 */
	public void testGetResourceReportTree(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);		
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		List<MetaDataConnBean> MetaDataConnBeanList = new ArrayList<MetaDataConnBean>();
		MetaDataConnBeanList.add(connBean);
		MetadataUtil.MetaDataConnBeanList = MetaDataConnBeanList;
		
		String result = MetadataUtil.getResourceReportTree("mysql");
		
		System.out.println(result);
	}
	/**
	 * 测试获取所有数据源
	 */
	public void testGetDatasources(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);
		
		DatabaseUtil databaseUtil = new DatabaseUtilImpl();
		MetadataUtil.setDatabaseUtil(databaseUtil);
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		try{
		List<MetaDataConnBean> dataSourceList = databaseUtil.getDataSources(connBean, MetadataUtil.classLoaderUtil, "admin", "admin");
		for(MetaDataConnBean metaDataConnBean:dataSourceList)
		{
			System.out.println(metaDataConnBean.getName()); 
		}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	/**
	 * 测试获取所有模式名
	 */
	public void testGetSchemas(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);
		
		DatabaseUtil databaseUtil = new DatabaseUtilImpl();
		MetadataUtil.setDatabaseUtil(databaseUtil);
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		try{
		List<String> schemaList = databaseUtil.getSchemas(connBean,MetadataUtil.classLoaderUtil);
		for(String schemaName:schemaList)
		{
			System.out.println(schemaName); 
		}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 测试获取所有表名
	 */
	public void testGetTables(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);
		
		DatabaseUtil databaseUtil = new DatabaseUtilImpl();
		MetadataUtil.setDatabaseUtil(databaseUtil);
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		try{
		List<String> tableList = databaseUtil.getTables(connBean,MetadataUtil.classLoaderUtil, "无");
		for(String tableName:tableList)
		{
			System.out.println(tableName); 
		}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 测试获取所有表字段名
	 */
	public void testGetFields(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);
		
		DatabaseUtil databaseUtil = new DatabaseUtilImpl();
		MetadataUtil.setDatabaseUtil(databaseUtil);
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		try{
		List<String> fieldList = databaseUtil.getFields(connBean, MetadataUtil.classLoaderUtil, "无", "r_user");
		for(String fieldName:fieldList)
		{
			System.out.println(fieldName); 
		}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 测试搜索元数据
	 */
	public void testQueryMetadata(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);		
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
        connBean.setName("mysql");
        connBean.setType("MYSQL");
        connBean.setAccess("Native");
        connBean.setServer("localhost");
		connBean.setDatabase("KDI");
		connBean.setPort("3306");
		connBean.setUsername("root");
		connBean.setPassword("");
		
		List<MetaDataConnBean> MetaDataConnBeanList = new ArrayList<MetaDataConnBean>();
		MetaDataConnBeanList.add(connBean);
		MetadataUtil.MetaDataConnBeanList = MetaDataConnBeanList;
		
		String resource = "mysql";
		String query =  " select distinct trans.ID_DIRECTORY,trans.NAME from r_step step"+
						" inner join R_TRANSFORMATION trans"+
				        " on  step.ID_TRANSFORMATION = trans.ID_TRANSFORMATION "+
				        " and step.NAME like metadataSearchKey";
		String searchKey = "";
		String result = MetadataUtil.queryMetadata(0,25,resource,query,searchKey);
		System.out.println(result);
	}
}
