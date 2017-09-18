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
package com.auphi.data.hub.export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;

/**
 * 导出oracle 用户下的表和表结构
 * @author anx
 *
 */
public class JdbcExport {
	
	PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
    
    String passwrod = pHelper.getValue("PASSWORD"); 
    String userName = pHelper.getValue("USERNAME"); 
    String url = pHelper.getValue("URL");
    String driver = "oracle.jdbc.driver.OracleDriver"; 
    
//    String sql_table = "select a.table_name as TABLE_NAME,user_tab_comments.comments as TABLE_COMMENTS,a.column_name as COLUMN_NAME, "+
//	  "a.data_type as DATA_TYPE, "+
//	  "decode(a.data_type, 'NUMBER', a.data_precision, a.data_length) as DATA_LENGTH, "+
//	  "a.data_scale as DATA_SCALE, "+
//	  "f.comments as COMMENTS, "+
//	  "a.nullable as NULLABLE, "+
//	  "a.data_default as DATA_DEFAULT, "+
//	  "decode(e.key, 'Y', 'Y', 'N') as DATA_FOREIGN_KEY "+
//	  "from user_tab_columns a,user_col_comments f,user_tab_comments, "+
//	  "(select b.table_name, "+
//	  "b.index_name, "+
//	  "b.uniqueness, "+
//	  "c.column_name, "+
//	  "decode(d.constraint_name, 'R', 'Y', 'N') key "+
//	  "from user_indexes b, "+
//	  "user_ind_columns c, "+
//	  "(select constraint_name "+
//	  "from user_constraints "+
//	  "where constraint_type = 'P') d "+
//	  "where b.index_name = c.index_name "+
//	  "and b.index_name = d.constraint_name(+)) e "+
//	  "where a.table_name = e.table_name(+) " +
//	  "and a.column_name = e.column_name(+) "+
//	  "and a.table_name = f.table_name "+
//	  "and a.column_name = f.column_name "+
//	  "and a.table_name = user_tab_comments.table_name "+
//	  //--and a.table_name = 'T_EDR_CON'
//	  //--and a.table_name not in('PLAN_TABLE','T_COMMAND_PARAM')
//	  "order by a.table_name"; 

    public void getTableData(){
    	StringBuffer sf = new StringBuffer();
    	String sql_table = "select a.table_name as TABLE_NAME,user_tab_comments.comments as TABLE_COMMENTS from user_tables a,user_tab_comments where a.table_name = user_tab_comments.table_name ";
    	try { 
            Class.forName(driver); 
            Connection conn = DriverManager.getConnection(url, userName, passwrod); 
            PreparedStatement ps = conn.prepareStatement(sql_table); 
            ResultSet rs = ps.executeQuery(); 
            while (rs.next()) { 
            	Dto<String,Object> map = new BaseDto();
            	map.put("tableName", rs.getString("TABLE_NAME")==null?"":rs.getString("TABLE_NAME"));
            	map.put("tableDesc", rs.getString("TABLE_COMMENTS")==null?"":rs.getString("TABLE_COMMENTS"));
//            	
            	String jsonStr = JsonHelper.encodeObject2Json(map);
            	sf = sf.append(jsonStr).append("&");
                
            } 
            sendTableData(sf);

            // 关闭记录集 
            if (rs != null) { 
                try { 
                    rs.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

            // 关闭声明 
            if (ps != null) { 
                try { 
                    ps.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

            // 关闭链接对象 
            if (conn != null) { 
                try { 
                    conn.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    	return;
    }
    
    public void sendTableData(StringBuffer sf){
    	Map<String,Object> pram = new HashMap<String,Object>();
		pram.put("postType", "1");
		pram.put("jsonStr", sf);
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String client_path = pHelper.getValue("CLIENT_PATH"); 
		HttpRest httpRest = new HttpRest();
		String str1 = httpRest.restPost(client_path+"/datahub/exportOracleTable/save.shtml", pram);
		System.out.println(str1);
    }
    
    
    public void getTableStructureData(String tableName,int table_id){
    	StringBuffer bf = new StringBuffer();
    	String sql_table_column = "select a.column_name as COLUMN_NAME, "+
    			  "a.data_type as DATA_TYPE, "+
    			  "f.comments as COMMENTS "+
    			  "from user_tab_columns a,user_col_comments f where "+
    			  " a.column_name = f.column_name "+
    			  " and a.table_name = f.table_name "+
    			  "and a.table_name ='"+tableName+"'"; 
    	
    	try { 
            Class.forName(driver); 
            Connection conn = DriverManager.getConnection(url, userName, passwrod); 
            PreparedStatement ps = conn.prepareStatement(sql_table_column); 
            ResultSet rs = ps.executeQuery(); 
            while (rs.next()) { 
            	Dto<String,Object> map = new BaseDto();
            	map.put("COLUMN_NAME", rs.getString("COLUMN_NAME")==null?"":rs.getString("COLUMN_NAME"));
            	map.put("DATA_TYPE", rs.getString("DATA_TYPE")==null?"":rs.getString("DATA_TYPE"));
            	map.put("COMMENTS", rs.getString("COMMENTS")==null?"":rs.getString("COMMENTS"));
            	map.put("table_id", table_id);
            	
            	String jsonStr = JsonHelper.encodeObject2Json(map);
//            	bf = bf.append(jsonStr).append("&");
            	sendTableStructureData(jsonStr);
            } 
            

            // 关闭记录集 
            if (rs != null) { 
                try { 
                    rs.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

            // 关闭声明 
            if (ps != null) { 
                try { 
                    ps.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

            // 关闭链接对象 
            if (conn != null) { 
                try { 
                    conn.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 

        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
    }
    
    
    public void sendTableStructureData(String jsonStr){
    	Map<String,Object> pram = new HashMap<String,Object>();
		pram.put("postType", "1");
		pram.put("jsonString", jsonStr);
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String client_path = pHelper.getValue("CLIENT_PATH"); 
		HttpRest httpRest = new HttpRest();
		String str1 = httpRest.restPost(client_path+"/datahub/exportOracleTableStructure/save.shtml", pram);
		System.out.println(str1);
    }
    
    
    public static void main(String[] args) {
    	JdbcExport j = new JdbcExport();
    	j.getTableData();
	}

}
