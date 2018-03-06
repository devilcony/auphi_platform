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

import junit.framework.TestCase;

import com.auphi.ktrl.metadata.bean.JobTransTreeNodeBean;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.metadata.tree.Tree;
import com.auphi.ktrl.metadata.util.DatabaseUtil;
import com.auphi.ktrl.metadata.util.DatabaseUtilImpl;
import com.auphi.ktrl.metadata.util.KettleUtil;
import com.auphi.ktrl.metadata.util.KettleUtilImpl;
import com.auphi.ktrl.metadata.util.MetadataUtil;

public class KettleUtilImplTest extends TestCase {
	/**
	 * 测试生成html文件报告
	 * 测试该方法的前提条件是将WebContent/ui目录拷贝到与WebContent同一目录下，然后再执行
	 * 否则会找不到ui下的文件
	 */
	public void testCreateAutoDoc(){
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
		
//        connBean.setName("KDI_REP_V3");
//        connBean.setType("KingbaseES");
//        connBean.setAccess("Native");
//        connBean.setServer("192.168.1.231");
//		connBean.setDatabase("KDI_REP_V3");
//		connBean.setPort("54321");
//		connBean.setUsername("SYSTEM");
//		connBean.setPassword("MANAGER");
		
		String fileName = "Set arguments on a transformation";
		String fileDir = "/testjob";
		String fileType = "JOB";
		String outPutTypeStr = "HTML";
		String targetFilename = "d:\\test\\ETL Documenation.html";
		String userName = "admin";
		
		String password = databaseUtil.getDataSourcePassword(connBean,userName,MetadataUtil.classLoaderUtil);
		
		kettleUtil.createAutoDoc(fileDir, fileName, fileType, 
				outPutTypeStr, targetFilename, 
				connBean,userName, password, MetadataUtil.classLoaderUtil);
	}
	/**
	 * 测试影响分析报告的结构树的json数据
	 */
	public void testInfluenceReport(){
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
		
		String userName = "admin";
		
		String password = databaseUtil.getDataSourcePassword(connBean,userName,MetadataUtil.classLoaderUtil);
		
		Object[] object = kettleUtil.getJobTransTree(connBean, MetadataUtil.classLoaderUtil, userName, password);
		List<JobTransTreeNodeBean> jobTransTreeNodeList = (List<JobTransTreeNodeBean>)object[0];
		List<String> dirList = (ArrayList<String>)object[1];
		
		jobTransTreeNodeList = MetadataUtil.adjustTreeNode(jobTransTreeNodeList,dirList);
		
		Tree r = new Tree(jobTransTreeNodeList);    
		r.recursionFn(r.nodeList, new JobTransTreeNodeBean(1,0,"/","","",""));    
		System.out.println(r.modifyStr(r.returnStr.toString()));
	}
	
	public void testtCreateAutoDoc(){
		KettleUtil kettleUtil = new KettleUtilImpl();
		MetadataUtil.setKettleUtil(kettleUtil);
		
		DatabaseUtil databaseUtil = new DatabaseUtilImpl();
		MetadataUtil.setDatabaseUtil(databaseUtil);
		
		MetadataUtil.init();
		
		MetaDataConnBean connBean = new MetaDataConnBean();
		
        connBean.setName("KDI_REP_V3");
        connBean.setType("KingbaseES");
        connBean.setAccess("Native");
        connBean.setServer("192.168.1.231");
		connBean.setDatabase("KDI_REP_V3");
		connBean.setPort("54321");
		connBean.setUsername("SYSTEM");
		connBean.setPassword("MANAGER");
		
		String fileName = "转换-查询-Web服务查询";
		String fileDir = "/转换/查询";
		String fileType = "TRANSFORMATION";//TRANSFORMATION JOB
		String outPutTypeStr = "HTML";
		String targetFilename = "d:\\test\\test.html";
		String userName = "admin";
		
		String password = databaseUtil.getDataSourcePassword(connBean,userName,MetadataUtil.classLoaderUtil);
		
		kettleUtil.createAutoDoc(fileDir, fileName, fileType, 
				outPutTypeStr, targetFilename, 
				connBean,userName, password, MetadataUtil.classLoaderUtil);
	}

}
