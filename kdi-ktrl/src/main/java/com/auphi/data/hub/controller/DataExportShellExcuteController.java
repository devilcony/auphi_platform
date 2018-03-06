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
package com.auphi.data.hub.controller;

import java.io.File;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.export.GenerateShell;
import com.auphi.data.hub.export.HttpRest;

/**
 * 数据导出shell远程执行
 * 
 * @author anx
 *
 */
@Controller("dataExportShellExcute")
public class DataExportShellExcuteController extends BaseMultiActionController {
	private static Log log = LogFactory.getLog(DataExportShellExcuteController.class);
	
    /**
     * 80server执行shell全量导出数据文件
     * @param req
     * @param resp
     * @return
     */
	public synchronized String executeShell_All(HttpServletRequest req,HttpServletResponse resp
//			,String RESULT_SEP,String FIELDS,String TABLE_NAME,String jobConfigId,String client_path
			) {
		
		String flag = "1";
		
		String RESULT_SEP = req.getParameter("RESULT_SEP");
		String FIELDS = req.getParameter("FIELDS");
		String TABLE_NAME = req.getParameter("TABLE_NAME");
		String CONDITIONS = req.getParameter("CONDITIONS");
		
		String jobConfigId = req.getParameter("CONFIG_ID");
		java.util.Date date=new java.util.Date();
		
		String path = "file";
		String filePath = "/home/oracle/data.txt";
//		GenerateShell generateShell = new GenerateShell();
		
		File file = new File("/home/oracle/"+path);
		if (!file.exists()) {
			GenerateShell.mkdir(path);  
			System.out.println("============all");
		}
		
		Timestamp startTime=new Timestamp(date.getTime());
		
		GenerateShell.createSQL(RESULT_SEP, FIELDS, TABLE_NAME, filePath, path,CONDITIONS);//"-", "", tableName, filePath, path
		GenerateShell.createShell(path, filePath);//path, filePath
		String result = GenerateShell.exectorShell(RESULT_SEP, FIELDS, TABLE_NAME, filePath, path);//colsep, fields, tableName, filePath, path
		System.out.println("***********************全量导出返回值是 result = "+result);
		log.info("全量导出 --- 远程执行 shell 完成");
//		generateShell = null;
		Timestamp endTime=new Timestamp(date.getTime());
		
		int count = 0;
		if (result !=null){
			count = Integer.parseInt(result);
			flag = "0";
		}
		Map<String,Object> dto_monitor = new HashMap<String,Object>();
		dto_monitor.put("postType", "1");
		dto_monitor.put("p_CONFIG_ID", jobConfigId);
		dto_monitor.put("p_STARTTIME", startTime);
		dto_monitor.put("p_END_TIME", endTime);
		dto_monitor.put("p_EXPORT_COUNT", count);
		dto_monitor.put("p_STATUS", flag);
		dto_monitor.put("p_DATA_PATH", filePath);
		HttpRest httpRest = new HttpRest();
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String client_path = pHelper.getValue("CLIENT_PATH"); 
		httpRest.restPost(client_path+"/datahub/dataExportMonitor/save.shtml", dto_monitor);//****client_path是枢纽系统地址
//		String str_monitor = httpRest.restPost(client_path+"/datahub/dataExportMonitor/save.shtml", dto_monitor);//****client_path是枢纽系统地址
//		System.out.println("=============server all send str_monitor==="+str_monitor);
	
		return flag;
	}
	
	
	/**
	 * 80 server上执行的 增量数据导出
	 * @param req
	 * @param resp
	 * @return
	 */
	public synchronized String executeShell_Increment(HttpServletRequest req,HttpServletResponse resp
//			,String RESULT_SEP,String FIELDS,String TABLE_NAME,String jobConfigId,String client_path
			) {
		
		String flag = "1";
		
		Map<String,Object> dto_monitor = new HashMap<String,Object>();
		Map<String,Object> dto_inrement = new HashMap<String,Object>();
		String RESULT_SEP = req.getParameter("RESULT_SEP");//分隔符
		String FIELDS = req.getParameter("FIELDS");//返回字段
		String TABLE_NAME = req.getParameter("TABLE_NAME");//表名
		String INCREMENTFIELD = req.getParameter("INCREMENTFIELD");//增量时间字段
		String STARTTIME = req.getParameter("STARTTIME");//开始时间
		String CONDITIONS = req.getParameter("CONDITIONS");//开始时间
		String jobConfigId = req.getParameter("CONFIG_ID");
		
		String path = "file";
		String filePath = "/home/oracle/data.txt";
		GenerateShell generateShell = new GenerateShell();
		
		File file = new File("/home/oracle/"+path);
		if (!file.exists()) {
			generateShell.mkdir(path);
			System.out.println("============in");
		}
		
		java.util.Date date=new java.util.Date();
		Timestamp startTime=new Timestamp(date.getTime());
		
		String endTimeStamp = generateShell.createSQL_Increment(RESULT_SEP, FIELDS, TABLE_NAME, filePath, path,INCREMENTFIELD,STARTTIME,CONDITIONS);//"-", "", tableName, filePath, path
		generateShell.createShell(path, filePath);//path, filePath
		String result = generateShell.exectorShell(RESULT_SEP, FIELDS, TABLE_NAME, filePath, path);//colsep, fields, tableName, filePath, path
		System.out.println("***********************增量导出返回值是 result = "+result);
		log.info("增量导出 --- 远程执行 shell 完成");
		generateShell = null;
		Timestamp endTime=new Timestamp(date.getTime());
		int count = 0;
		if (result !=null){
			count = Integer.parseInt(result);
			flag = "0";
		}
		dto_monitor.put("postType", "1");
		dto_monitor.put("p_CONFIG_ID", jobConfigId);
		dto_monitor.put("p_STARTTIME", startTime);
		dto_monitor.put("p_END_TIME", endTime);
		dto_monitor.put("p_EXPORT_COUNT", count);
		dto_monitor.put("p_STATUS", flag);
		dto_monitor.put("p_DATA_PATH", filePath);
		
		dto_inrement.put("postType", "1");
		dto_inrement.put("p_CONFIG_ID", req.getParameter("CONFIG_ID"));
		dto_inrement.put("p_ROWNUM", count);
		dto_inrement.put("p_LAST_DATE", endTimeStamp);
		
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String client_path = pHelper.getValue("CLIENT_PATH"); 
		HttpRest httpRest = new HttpRest();
		//****client_path是枢纽系统地址
		httpRest.restPost(client_path+"/datahub/dataExportMonitor/save.shtml", dto_monitor);
//		String str_monitor = httpRest.restPost(client_path+"/datahub/dataExportMonitor/save.shtml", dto_monitor);
		httpRest.restPost(client_path+"/datahub/dataExportIncrement/save.shtml", dto_inrement);
//		String str_inrement = httpRest.restPost(client_path+"/datahub/dataExportIncrement/save.shtml", dto_inrement);
	
		return flag;
	}
	
}
