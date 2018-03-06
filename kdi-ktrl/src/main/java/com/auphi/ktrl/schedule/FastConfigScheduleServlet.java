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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.ha.bean.HAClusterBean;
import com.auphi.ktrl.ha.util.HAClusterUtil;
import com.auphi.ktrl.schedule.tools.RemoteMarketToosl;
import com.auphi.ktrl.schedule.util.FTPUtil;
import com.auphi.ktrl.schedule.util.MarketUtil;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.view.DestColumuTypeView;
import com.auphi.ktrl.schedule.view.FTPSourceView;
import com.auphi.ktrl.schedule.view.FastConfigDatabaseView;
import com.auphi.ktrl.schedule.view.FastConfigView;
import com.auphi.ktrl.schedule.view.FieldMappingView;
import com.auphi.ktrl.schedule.view.HashMapAndArray;
import com.auphi.ktrl.schedule.view.QrtzJobDetailsView;
import com.auphi.ktrl.system.user.bean.UserBean;


/**
 * Servlet implementation class SkyformScheduleServlet
 */
public class FastConfigScheduleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    //public static String [] primarys={};//定义全局变量主键
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FastConfigScheduleServlet() {
  
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
	@SuppressWarnings({ "unused", "resource" })
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String action = request.getParameter("action");	
	    response.setCharacterEncoding("UTF-8"); 
	    request.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json; charset=UTF-8"); 
		ResultSet rs = null;
		Connection connection=null;
		PreparedStatement smt=null;
		String user_id = request.getSession().getAttribute("user_id")==null?"":request.getSession().getAttribute("user_id").toString();
		UserBean userBean = request.getSession().getAttribute("userBean")==null?null:(UserBean)request.getSession().getAttribute("userBean");
		String idDatabase= request.getParameter("idDatabase")==null?"":request.getParameter("idDatabase");
		if("findDatabase".equals(action))
		{
			List<FastConfigDatabaseView> fastConfigDatabaseViews= new ArrayList<FastConfigDatabaseView>();			 
			   try {
			    connection=ConnectionPool.getConnection();
	            smt=  connection.prepareStatement("SELECT * FROM R_DATABASE");
	            rs=smt.executeQuery();
				while (rs.next()) {
					FastConfigDatabaseView fastConfigDatabaseView =new FastConfigDatabaseView();
					fastConfigDatabaseView.setName(rs.getString("NAME"));
					fastConfigDatabaseView.setIdDatase(rs.getInt("ID_DATABASE"));
					fastConfigDatabaseViews.add(fastConfigDatabaseView);
					
				} 
			       PrintWriter out = response.getWriter(); 
			       out.write(JSON.toJSONString(fastConfigDatabaseViews));
                   out.close();
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{	
				ConnectionPool.freeConn(rs, null, smt, connection);

			}
		} if("findTable".equals(action))
		{
			///ConnConfigBean connConfig = DataBaseUtil.getQuartzConfig();	
			List<FastConfigDatabaseView> fastConfigDatabaseViews= new ArrayList<FastConfigDatabaseView>();	
        	FastConfigDatabaseView fastConfigDatabaseView =new FastConfigDatabaseView();
        	String typeName=null;
			   try {
	            connection = ConnectionPool.getConnection();	            
	            smt=  connection.prepareStatement("SELECT * FROM R_DATABASE D JOIN R_DATABASE_TYPE T ON D.ID_DATABASE_TYPE=T.ID_DATABASE_TYPE  WHERE ID_DATABASE =? ");
	            smt.setInt(1, Integer.valueOf(idDatabase));
	            rs=smt.executeQuery();
	            while (rs.next()) 
	            {
					fastConfigDatabaseView.setIdDatase(rs.getInt("ID_DATABASE"));
					fastConfigDatabaseView.setName(rs.getString("NAME"));
					fastConfigDatabaseView.setIdDatabaseType(rs.getInt("ID_DATABASE_TYPE"));
					fastConfigDatabaseView.setIdDatabaseContype(rs.getInt("ID_DATABASE_CONTYPE"));
					typeName=rs.getString("DESCRIPTION");
					fastConfigDatabaseView.setHostName(rs.getString("HOST_NAME"));
					fastConfigDatabaseView.setDatabaseName(rs.getString("DATABASE_NAME"));
					fastConfigDatabaseView.setPort(rs.getInt("PORT"));
					fastConfigDatabaseView.setUserName(rs.getString("USERNAME"));
					fastConfigDatabaseView.setPassword(rs.getString("PASSWORD"));
					fastConfigDatabaseView.setServerName(rs.getString("SERVERNAME"));
					fastConfigDatabaseView.setDataTbs(rs.getString("DATA_TBS"));
					fastConfigDatabaseView.setIndexTbs(rs.getString("INDEX_TBS"));	
	            }
	            
	           // Encr.decryptPasswordOptionallyEncrypted  	           
	            DatabaseMeta databaseMeta = new DatabaseMeta(fastConfigDatabaseView.getName(),
	            		typeName, "Native", fastConfigDatabaseView.getHostName(),
	            		fastConfigDatabaseView.getDatabaseName(), fastConfigDatabaseView.getPort().toString(),
	            		fastConfigDatabaseView.getUserName(), Encr.decryptPasswordOptionallyEncrypted(fastConfigDatabaseView.getPassword()));
			   Database database = new Database(databaseMeta);		 
	            //database.setConnection(connection);
				database.connect();	
				if(typeName.equalsIgnoreCase("Hadoop Hive 2") || typeName.equalsIgnoreCase("Hadoop Hive"))
				{
					database.execStatement("use "+fastConfigDatabaseView.getDatabaseName());
				}
				String[]  tablas=database.getTablenames();
				for (int i = 0; i < tablas.length; i++) {
					FastConfigDatabaseView f =new FastConfigDatabaseView();
					f.setName(tablas[i]);
					fastConfigDatabaseViews.add(f);
				}
				//database.o
				//rs=database.openQuery("SELETE *FROM R_DATABASE", null, null, ResultSet.FETCH_FORWARD,false);	

			       PrintWriter out = response.getWriter(); 
			       out.write(JSON.toJSONString(fastConfigDatabaseViews)); 
			       //System.out.println(JSON.toJSON(fastConfigDatabaseViews));
			       out.close();
                   database.disconnect();
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KettleDatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{		
			       try {
						smt.close();
					    rs.close();
					    connection.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		}else if("findRemoteMarketTable".equals(action)){  
           //采用session 存储方法，如果速度过慢会导致前端的combox 要点击两下才能显示数据         
		       PrintWriter out = response.getWriter(); 	
		       out.write(MarketUtil.getSchemaNames(request)); 
		       out.close();
		     
		       
		}else if("findSchemaTable".equals(action)){  
	           //采用session 存储方法，如果速度过慢会导致前端的combox 要点击两下才能显示数据   
			       String schema= request.getParameter("schemaName");
			       PrintWriter out = response.getWriter(); 	
			       out.write(MarketUtil.getSchemaTables(request,schema)); 
			       //out.flush();
			       out.close();
			     
			       
		}
//		else if("findRemoteMarketTableField".equals(action))
//		{
//		}
		else if("findField".equals(action))
		{
			String fastConfig=request.getParameter("fastConfigData");
			FastConfigView fastConfigView= JSON.parseObject(fastConfig, FastConfigView.class);

        	//java.util.HashMap<Integer, FieldMappingView> sourceHashMap=new java.util.HashMap<Integer, FieldMappingView>();
			java.util.TreeMap<Integer, FieldMappingView> sourceHashMap=new java.util.TreeMap<Integer, FieldMappingView>();
        	java.util.TreeMap<Integer, FieldMappingView> destHashMap=new java.util.TreeMap<Integer, FieldMappingView>();
            List<FieldMappingView> sourceORdest= new ArrayList<FieldMappingView>();
            HashMapAndArray hashMapAndArray= null;
        	//String [] primarys={};
        	
        	Database database=null;
        	String typeName=null;
            if(fastConfigView.getIdSourceType()>0)
            {
				switch (fastConfigView.getIdSourceType()) {
				case 1:  //database
					   hashMapAndArray=MarketUtil.getSourceHashMap(fastConfigView, database,rs);
					   //database.disconnect();
					   sourceHashMap=hashMapAndArray.getHashMap();
						//如果 目标数据源类型
						if(fastConfigView.getIdDestType()>0)
						{
							switch (fastConfigView.getIdDestType()) {
							case 1:
								destHashMap= MarketUtil.getDestHashMap(fastConfigView,database);
								break;
							case 2:
								
								break;
							case 3:
								break;
							case 4:								
								JSONObject jsonParam = new JSONObject();
								jsonParam.put("schemaName",fastConfigView.getDestSchenaName().trim());
								jsonParam.put("tableName", fastConfigView.getDestTableName().trim());
				    		    String json =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTableStructure", jsonParam);	
				    		    destHashMap=MarketUtil.getDestHashMap(json);
								break;
							default:
								break;
							}	
						}
               
					break;
				case 2:  //ftp
					
					hashMapAndArray=FTPUtil.getSourceHashMap(fastConfigView);
					System.out.println("=======finish get source");
					sourceHashMap=hashMapAndArray.getHashMap();
					//如果 目标数据源类型
					if(fastConfigView.getIdDestType()>0)
					{
						switch (fastConfigView.getIdDestType()) {
						case 1:
							
							destHashMap= MarketUtil.getDestHashMap(fastConfigView,database);
							break;
						case 2:
							break;
						case 3:
							
							break;
						case 4:			
							JSONObject jsonParams = new JSONObject();
							jsonParams.put("schemaName",fastConfigView.getDestSchenaName().trim());
							jsonParams.put("tableName", fastConfigView.getDestTableName().trim());
			    		    String jsons =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTableStructure", jsonParams);	
			    		    destHashMap=MarketUtil.getDestHashMap(jsons);
							break;
						default:
							break;
						}	
					}
					break;
				case 3: //hadoop
					
					break;
				case 4: //mart(remote db)
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("schemaName",fastConfigView.getSourceSchenaName().trim());
					jsonParam.put("tableName", fastConfigView.getSourceTableName().trim());
	    		       String json =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTableStructure", jsonParam);	
	    		       hashMapAndArray=MarketUtil.getSourceHashMap(json);
	    		       sourceHashMap=hashMapAndArray.getHashMap();
						//如果 目标数据源类型
						if(fastConfigView.getIdDestType()>0)
						{
							switch (fastConfigView.getIdDestType()) {
							case 1:
								
								destHashMap= MarketUtil.getDestHashMap(fastConfigView,database);
								break;
							case 2:
								break;
							case 3:
								break;
							case 4:								
								JSONObject jsonParams = new JSONObject();
								jsonParams.put("schemaName",fastConfigView.getDestSchenaName().trim());
								jsonParams.put("tableName", fastConfigView.getDestTableName().trim());
				    		    String jsons =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTableStructure", jsonParams);	
				    		    destHashMap=MarketUtil.getDestHashMap(jsons);
								break;
							default:
								break;
							}	
						}
					break;				
				default:
					break;
				}   
				
				//关闭
				try {
					if(rs!=null)
					{
						rs.close();
					}
					if(connection!=null)
					{
						connection.close();
					}	
					if(smt!=null)
					{
						smt.close();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				//jason 20141106
				if(destHashMap.isEmpty()&&!sourceHashMap.isEmpty())
				{
					System.out.println("===== dest is null");
					destHashMap = generateDestHashMap(sourceHashMap);
				}
				
			       
				//组合数据
				if(!sourceHashMap.isEmpty()&&!destHashMap.isEmpty())
				{
	                sourceORdest= MarketUtil.getSourceORdest(destHashMap, sourceHashMap);				
				}else if(sourceHashMap.isEmpty()&&!destHashMap.isEmpty())
				{
					sourceORdest=MarketUtil.getSourceORdest(destHashMap);
				}else if(destHashMap.isEmpty()&&!sourceHashMap.isEmpty())
				{
					sourceORdest=MarketUtil.getSourceORdest(sourceHashMap);	
				}
				
				try{
	                if(hashMapAndArray!=null)
	                {
	                	 if(hashMapAndArray.getPrimarys()!=null && hashMapAndArray.getPrimarys().length>0)
	                     { //主键配置
	                         for (int i = 0; i < sourceORdest.size(); i++) {
	         					for (int j = 0; j < hashMapAndArray.getPrimarys().length; j++) {
	         						if(sourceORdest.get(i).getSourceColumnName().equals(hashMapAndArray.getPrimarys()[j]))
	         						{
	         							sourceORdest.get(i).setIsPrimary(true);
	         						}
	         					} 
	         				}               	
	                     }
	                }   
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
                
		       PrintWriter out = response.getWriter(); 			       
		       out.write(JSON.toJSONString(sourceORdest)); 
		       out.close();  
            }
		}else if("findDatabaseType".equals(action)) {
        	List<DestColumuTypeView> typeList= new ArrayList<DestColumuTypeView>();
        	for (String s : ValueMetaInterface.typeCodes) {
        		DestColumuTypeView destColumuTypeView= new DestColumuTypeView();
        		destColumuTypeView.setName(s);
				typeList.add(destColumuTypeView);
			}
			       PrintWriter out = response.getWriter(); 	
			       out.write(JSON.toJSONString(typeList)); 
			       out.close();				   
		}else if("saveDispatch".equals(action))
		{
		   String fastConfigJson= request.getParameter("fastConfigJson")==null?null:request.getParameter("fastConfigJson");
		   String fieldMappingJson=request.getParameter("fieldMappingJson")==null?null:request.getParameter("fieldMappingJson");
		   String dispatchingModeJosn= request.getParameter("dispatchingModeJosn")==null?null:request.getParameter("dispatchingModeJosn");
		   String jobName=request.getParameter("jobName")==null?null:request.getParameter("jobName");
		   String checkIsUpatde=request.getParameter("checkIsUpatde")==null?"false":request.getParameter("checkIsUpatde");		   
		   if(fastConfigJson!=null&&fieldMappingJson!=null&&dispatchingModeJosn!=null&&jobName!=null)
		   {
			   boolean check=false;
			   if(checkIsUpatde.equals("true"))
			   {
				   check=true;
			   }else if(checkIsUpatde.equals("false"))
			   {
				   check=false;
			   }
			   QuartzUtil.saveDispatch(fastConfigJson, fieldMappingJson, dispatchingModeJosn, jobName, check, userBean);			   				   

		   }

		}else if("checkJobName".equals(action)){
            connection = ConnectionPool.getConnection();
            String jobName=request.getParameter("jobName");
            String check="no";
            try {
                smt=  connection.prepareStatement("SELECT *FROM QRTZ_JOB_DETAILS WHERE JOB_NAME =?");
                smt.setString(1, jobName);
                rs=smt.executeQuery();
                if(rs.next())
                {
                  check="yes";
                }
                if(smt!=null)
            	{
            	  smt.close();
            	}
	            if(connection!=null)
	            {
	            	connection.close();
	            }
			       PrintWriter out = response.getWriter(); 	
			       out.write(check);
			       out.close();	
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else if("findFtpTable".equals(action))
		{
            connection = ConnectionPool.getConnection();
            try {
                smt=  connection.prepareStatement("SELECT *FROM KDI_T_FTP");
                rs=smt.executeQuery();
                List<FTPSourceView>ftpSourceViews= new ArrayList<FTPSourceView>();
                while(rs.next())
                {
                	FTPSourceView ftpSourceView= new FTPSourceView();
                	ftpSourceView.setFtpID(Integer.valueOf(rs.getString("ID_FTP")));
                	ftpSourceView.setName(rs.getString("NAME"));
                	ftpSourceViews.add(ftpSourceView);
                }
			       PrintWriter out = response.getWriter(); 	
			       out.write(JSON.toJSONString(ftpSourceViews));
			       out.close();	
			} catch (Exception e) {
				e.printStackTrace();
			}finally
			{
				ConnectionPool.freeConn(rs, null, smt, connection);
			}
		}else if("findJob".equals(action)){
			String jobName=request.getParameter("jobName")==null?null:request.getParameter("jobName");
		       QrtzJobDetailsView qrtzJobDetailsView= QuartzUtil.findQrtzJobDetailsView(jobName, userBean);
			   PrintWriter out = response.getWriter(); 	
		       out.write(JSON.toJSONString(qrtzJobDetailsView));
		       out.close();	
		}else if("findHA".equals(action)) {
			List<HAClusterBean> listHA = HAClusterUtil.findAll();
			for(HAClusterBean ha : listHA){
				ha.setBase_port(null);
				ha.setDynamic_cluster(null);
				ha.setSlaves(null);;
				ha.setSockets_buffer_size(null);
				ha.setSockets_compressed(null);
				ha.setSockets_flush_interval(null);;
			}
			PrintWriter out = response.getWriter(); 	
			out.write(JSON.toJSONString(listHA)); 
			out.close();				   
		}
	}

	/**
	 * generate dest columns by source columns
	 * @param sourceHashMap
	 * @return
	 */
	private TreeMap<Integer, FieldMappingView> generateDestHashMap(
			TreeMap<Integer, FieldMappingView> sourceHashMap) {
		
		Iterator<Entry<Integer, FieldMappingView>> sourceKeyIterator = sourceHashMap.entrySet().iterator();
		int i=0;
		TreeMap<Integer, FieldMappingView>destHashMap = new TreeMap<Integer, FieldMappingView>();
       	while (sourceKeyIterator.hasNext()) {
           	Entry<Integer, FieldMappingView> s = sourceKeyIterator.next();
           	FieldMappingView sourceFieldMappingView =s.getValue();
          	FieldMappingView dest=new FieldMappingView();
            dest.setDestColumuName(sourceFieldMappingView.getSourceColumnName());  
            dest.setDestColumnType("".equals(sourceFieldMappingView.getSourceColumnType())?sourceFieldMappingView.getDestColumnType():sourceFieldMappingView.getSourceColumnType());            
            dest.setSourceColumnName("");
            dest.setSourceColumnType("");
            dest.setStartIndex(0);
            dest.setEndIndex(0);
            dest.setReference("");
            dest.setIsPrimary(false);
            dest.setIsNullable(false);
            dest.setDestLength("");
            dest.setDestScale("-1");
            destHashMap.put(i++, dest);
            System.out.println("=====put"+i+"  "+dest.toString());
       }
       	return destHashMap;
	}
	


}
