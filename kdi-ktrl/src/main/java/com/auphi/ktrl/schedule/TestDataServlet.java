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
package com.auphi.ktrl.schedule;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;

import com.alibaba.fastjson.JSON;
import com.auphi.ktrl.schedule.util.FTPUtil;
import com.auphi.ktrl.schedule.util.MarketUtil;
import com.auphi.ktrl.schedule.view.FastConfigView;

/**
 * Servlet implementation class TestDataServlet
 */
public class TestDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");	
	    response.setCharacterEncoding("UTF-8"); 
	    request.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json; charset=UTF-8"); 
		Database database=null;
		if("testData".equals(action))
		{
			String fastConfig=request.getParameter("fastConfigData");
			HashMap<String,String> testState=new HashMap<String, String>(); 
			FastConfigView fastConfigView= JSON.parseObject(fastConfig, FastConfigView.class);
			switch (fastConfigView.getIdSourceType()) {
			case 1:
	            //源数据
				database=MarketUtil.getDatabase(fastConfigView.getIdSourceDatabase());
	            try {
					database.connect();
					testState.put("state1", "数据库连接成功");
				} catch (KettleDatabaseException e) {
					// TODO Auto-generated catch block
					testState.put("state1", "数据库连接失败");
					e.printStackTrace();
				}
	        	database.disconnect(); 				
				switch (fastConfigView.getIdDestType()) {
				case 1:
		            //目标数据
		            database=MarketUtil.getDatabase(fastConfigView.getIdDestDatabase());
		            try {
						database.connect();
						testState.put("state2", "目标数据库连接成功");
					} catch (KettleDatabaseException e) {
						// TODO Auto-generated catch block
						testState.put("state2", "目标数据库连接失败");
						e.printStackTrace();
					}
		        	database.disconnect(); 					
					break;
				case 2:
					
					break;
				case 3:
					testState.put("state2", "Hadoop连接成功");					
					break;
				case 4:
					testState.put("state2", "集市连接成功");					
					break;					
				default:
					break;
				}
				break;
			case 2:
	        	//ftp
	            if(FTPUtil.testFTP(fastConfigView))
	            {
					testState.put("state1", "FTP连接失败");
	            }else {
					testState.put("state1", "FTP连接成功");
				}				
				switch (fastConfigView.getIdDestType()) {
				case 1:
		            //目标数据
		            database=MarketUtil.getDatabase(fastConfigView.getIdDestDatabase());
		            try {
						database.connect();
						testState.put("state2", "目标数据库连接成功");
					} catch (KettleDatabaseException e) {
						// TODO Auto-generated catch block
						testState.put("state2", "目标数据库连接失败");
						e.printStackTrace();
					}
		        	database.disconnect(); 					
					break;
				case 2:
					
					break;
				case 3:
					testState.put("state2", "Hadoop连接成功");					
					break;
				case 4:
					testState.put("state2", "集市连接成功");					
					break;					
				default:
					break;
				}				
				break;
			case 3:
				switch (fastConfigView.getIdDestType()) {
				case 1:
		            //目标数据
		            database=MarketUtil.getDatabase(fastConfigView.getIdDestDatabase());
		            try {
						database.connect();
						testState.put("state2", "目标数据库连接成功");
					} catch (KettleDatabaseException e) {
						// TODO Auto-generated catch block
						testState.put("state2", "目标数据库连接失败");
						e.printStackTrace();
					}
		        	database.disconnect(); 					
					break;
				case 2:
					
					break;
				case 3:
					testState.put("state2", "Hadoop连接成功");					
					break;
				case 4:
					testState.put("state2", "集市连接成功");					
					break;					
				default:
					break;
				}				
				break;
			case 4:
				testState.put("state1", "集市连接成功");	
				switch (fastConfigView.getIdDestType()) {
				case 1:
		            //目标数据
		            database=MarketUtil.getDatabase(fastConfigView.getIdDestDatabase());
		            try {
						database.connect();
						testState.put("state2", "目标数据库连接成功");
					} catch (KettleDatabaseException e) {
						// TODO Auto-generated catch block
						testState.put("state2", "目标数据库连接失败");
						e.printStackTrace();
					}
		        	database.disconnect(); 					
					break;
				case 2:
					
					break;
				case 3:
					testState.put("state2", "Hadoop连接成功");					
					break;
				case 4:
					testState.put("state2", "集市连接成功");				
					break;					
				default:
					break;
				}				
				break;				
			default:
				break;
			}
	        PrintWriter out = response.getWriter();
	        out.write(JSON.toJSONString(testState)); 
	        out.close();  
		}
	}

}
