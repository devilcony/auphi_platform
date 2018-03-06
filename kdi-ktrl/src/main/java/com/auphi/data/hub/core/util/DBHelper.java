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
package com.auphi.data.hub.core.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.auphi.data.hub.domain.Datasource;

/**
 * 数据库操作工具类
 * 
 * @author zhangfeng
 *
 */
public class DBHelper {
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Datasource source = new Datasource();
		source.setSourceIp("localhost");
		source.setSourceType(1);
		source.setSourceDataBaseName("demo");
		source.setSourcePort("3306");
		source.setSourceUserName("zhangfeng");
		source.setSourcePassword("zhangfeng");
		try {
			getTableStruct(source,"easequence");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static List<String> getTableStruct(Datasource source,String tableName) throws Exception{
		Connection conn = getConnection(source);
		DatabaseMetaData m_DBMetaData = conn.getMetaData(); 
		//4. 提取表内的字段的名字和类型 
		String columnName; 
		List<String> list = new ArrayList<String>();  
		ResultSet colRet = m_DBMetaData.getColumns(null,"%", tableName,"%"); 
		while(colRet.next()) { 
			columnName = colRet.getString("COLUMN_NAME"); 
			list.add(columnName);
		}
		return list;
	}
	
	public static List<String> getAllTableName(Datasource source) throws Exception{
		Connection conn = getConnection(source);
		ResultSet rs = null;  
        List<String> list = new ArrayList<String>();  
        try {  
            DatabaseMetaData metaData = conn.getMetaData();  
            rs = metaData.getTables(null, source.getSourceDataBaseName(), null, new String[]{"TABLE"});  
            while(rs.next()){  
                String tableName = rs.getString("TABLE_NAME");
                System.out.println(tableName);
                list.add(tableName);  
            }  
        } catch (SQLException e) {  
            e.printStackTrace();  
        } finally{  
        	if(conn != null){
        		conn.close();
        	}
            if(rs != null){
            	rs.close();
            }
        } 
        return list;
	}

	
	private static Connection getConnection(Datasource source) throws Exception{
		String className = "";
		String url = "";
		Connection conn = null;
		try{
			if(source.getSourceType() == 1){//mysql
				className = "com.mysql.jdbc.Driver";
				url = "jdbc:mysql://"+ source.getSourceIp() + ":" + source.getSourcePort()+"/"+ source.getSourceDataBaseName();
			} else if(source.getSourceType() == 2){//oracle
				className = "oracle.jdbc.driver.OracleDriver";
				url = "jdbc:oracle:thin:@"+ source.getSourceIp() + ":" + source.getSourcePort()+":"+ source.getSourceDataBaseName();
			} else if(source.getSourceType() == 3){//sql server
				className = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
				url = "jdbc:microsoft:sqlserver://"+ source.getSourceIp() + ":" + source.getSourcePort()+";DatabaseName="+ source.getSourceDataBaseName();
			}
			Class.forName(className);
			conn = DriverManager.getConnection(url,source.getSourceUserName(),source.getSourcePassword());
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		return conn;
	}
	
}
