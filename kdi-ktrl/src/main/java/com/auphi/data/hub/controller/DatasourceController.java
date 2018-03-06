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

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.DatasourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.Constants;

/**
 * 数据源配置控制器
 * 
 * @author zhangfeng
 *
 */
@Controller("datasource")
public class DatasourceController extends BaseMultiActionController {
	
	private static Log log = LogFactory.getLog(ServiceController.class);

	private final static String INDEX = "admin/datasource";
	
	@Autowired
	private DatasourceService datasourceService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{		
		
		Dto<String,Object> dto = new BaseDto();
		String querySourceName = req.getParameter("queryParam");
		//dto.put("queryParam", querySourceName);
		int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
		System.out.println("repId=" + repId);
		this.setPageParam(dto, req);
		String start= dto.getAsString("start");
		String end= dto.getAsString("end");
		log.info("start="+start);
		log.info("end="+end);
		Integer total=null;
		Connection connection=null;
		PreparedStatement smt=null;
		ResultSet rs=null;
		try {
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);
			
			String sql = "SELECT COUNT(ID_DATABASE) AS TOTAL FROM"+
		    "(SELECT ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME FROM R_DATABASE ";
		    if(null!=querySourceName&&""!=querySourceName){
		          sql+="WHERE NAME like '%" +querySourceName+"%'";
		    }
			sql+="ORDER BY ID_DATABASE desc) D";
			
	        smt=  connection.prepareStatement(sql);
	        rs=smt.executeQuery();
	        while (rs.next()) {
	        	total=Integer.valueOf(rs.getInt("TOTAL"));	
	        }
			log.info("total="+total);
			rs.close();
			smt.close();
			
			String sql2 = "SELECT * FROM (SELECT ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME FROM R_DATABASE ";
			    if(null!=querySourceName&&""!=querySourceName){   
				   sql2 +="WHERE NAME like '%" +querySourceName+"%'";
			    }
			       sql2 +="ORDER BY ID_DATABASE desc) D limit "+start+","+end;
	        smt=  connection.prepareStatement(sql2);
	        rs=smt.executeQuery();
	        List<Datasource> items = new ArrayList<Datasource>();
	        while (rs.next()) {
	        	Datasource datasource=new Datasource();
	        	Integer database_id=Integer.valueOf(rs.getInt("ID_DATABASE"));
	        	datasource.setSourceId(database_id);
	        	String  database_sourcename=rs.getString("NAME");
	        	datasource.setSourceName(database_sourcename);
	        	String  database_user=rs.getString("USERNAME");
	        	datasource.setSourceUserName(database_user);
	        	String  database_pwd=rs.getString("PASSWORD");
	        	datasource.setSourcePassword(database_pwd);
	        	Integer database_type=Integer.valueOf(rs.getInt("ID_DATABASE_TYPE"));
	        	datasource.setSourceType(database_type);
	        	String database_name=rs.getString("DATABASE_NAME");
	        	datasource.setSourceDataBaseName(database_name);
	        	String database_port=rs.getString("PORT");
	        	datasource.setSourcePort(database_port);
	        	String  database_host=rs.getString("HOST_NAME");
	        	datasource.setSourceIp(database_host);
	        	items.add(datasource);
	        }
	        rs.close();
			smt.close();
			connection.close();
	        
			PaginationSupport<Datasource> page = new PaginationSupport<Datasource>(items, total);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			if(rs != null){
				rs.close();
			}
			if(smt != null){
				smt.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		return null;
	}
	
	
	public ModelAndView saveSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException, SQLException{	
		Integer id = null;
		Connection connection = null;
		Statement smt = null;
		PreparedStatement pstmt = null;
		ResultSet rs=null;
		try{
			int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
			System.out.println("repId=" + repId);
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);	  
			
	        smt=connection.createStatement();
	        rs=smt.executeQuery("SELECT MAX(ID_DATABASE) AS MAXID FROM R_DATABASE");
	        while (rs.next()) {
	        	 id=Integer.valueOf(rs.getInt("MAXID")+1);	
	        }
			log.info("sourceId="+id);
			rs.close();
			smt.close();
			
			connection.setAutoCommit(false);
			String sql = "INSERT INTO R_DATABASE (ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME) values (?,?,?,?,?,?,?,?,?)";
			pstmt=  connection.prepareStatement(sql);
			pstmt.setObject(1, id);
			pstmt.setObject(2, source.getSourceName());
			pstmt.setObject(3, source.getSourceUserName());
			pstmt.setObject(4, Encr.encryptPasswordIfNotUsingVariables(source.getSourcePassword()));
			pstmt.setObject(5, source.getSourceType());
			pstmt.setObject(6, "1");
			pstmt.setObject(7, source.getSourceDataBaseName());
			pstmt.setObject(8, source.getSourcePort());
			pstmt.setObject(9, source.getSourceIp());
			pstmt.execute();
			connection.commit();
			connection.setAutoCommit(true);
			pstmt.close();
			
			this.setOkTipMsg("数据源保存成功！", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加数据源失败请检查后重新添加！", resp);
		}finally{
			if(rs != null){
				rs.close();
			}
			if(smt != null){
				smt.close();
			}
			if(pstmt != null){
				pstmt.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		return null;
	}

	
	public ModelAndView updateSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException, SQLException{	
		Connection connection=null;
		PreparedStatement smt=null;
		try{
			int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
			System.out.println("repId=" + repId);
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);	  
			
			connection.setAutoCommit(false);
			String sql = "UPDATE R_DATABASE SET NAME=?,USERNAME=?,PASSWORD=?,ID_DATABASE_TYPE=? ,DATABASE_NAME=?,PORT=?,HOST_NAME=? WHERE ID_DATABASE=?";
	        smt=  connection.prepareStatement(sql);
			smt.setObject(1, source.getSourceName());
			smt.setObject(2, source.getSourceUserName());
			smt.setObject(3, Encr.encryptPasswordIfNotUsingVariables(source.getSourcePassword()));
			smt.setObject(4, source.getSourceType());
			smt.setObject(5, source.getSourceDataBaseName());
			smt.setObject(6, source.getSourcePort());
			smt.setObject(7, source.getSourceIp());
			smt.setObject(8, source.getSourceId());
			smt.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			smt.close();
			connection.close();
			
			this.setOkTipMsg("数据源修改成功！", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加数据源失败请检查后重新编辑！", resp);
		}finally{
			if(smt != null){
				smt.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		return null;
	}
	
	
	/**
	 * 删除数据库
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 * @throws SQLException 
	 */
	public ModelAndView deleteSource(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{
		Connection connection=null;
		PreparedStatement smt=null;
		try{
			String idDatabases = req.getParameter("strChecked");
			
			String sql = "DELETE FROM R_DATABASE WHERE ID_DATABASE IN ("+idDatabases+")";

			int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
			System.out.println("repId=" + repId);
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);	  
			
			connection.setAutoCommit(false);
			smt=  connection.prepareStatement(sql);
			smt.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			log.info("idDatabases="+idDatabases);
			this.setOkTipMsg("数据库删除成功", resp);
			
			smt.close();
			connection.close();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			this.setOkTipMsg("数据库删除失败", resp);
		}finally{
			if(smt != null){
				smt.close();
			}
			if(connection != null){
				connection.close();
			}
		}
		return null;
	}
	
	public ModelAndView getDbTypeList(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{ 
		List<Dto<?,?>> dbTypeList = new ArrayList<Dto<?,?>>();
		Connection connection=null;
		PreparedStatement smt=null;
		ResultSet rs = null;
		try{
			String sql = "SELECT ID_DATABASE_TYPE AS DBTYPE,DESCRIPTION FROM R_DATABASE_TYPE";

			int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
			System.out.println("repId=" + repId);
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);	  
			
			smt=  connection.prepareStatement(sql);
			rs=smt.executeQuery();
	        while (rs.next()) {
	        	 Dto<String,Object> dto1 = new BaseDto();
	        	 String dbType = String.valueOf(rs.getInt("DBTYPE"));	
	        	 String dbTypeName = rs.getString("DESCRIPTION");
	        		dto1.put("sourceType",dbType);
					dto1.put("sourceTypeName",dbTypeName);
					dbTypeList.add(dto1);
	        }
	        rs.close();
	        smt.close();
	        connection.close();
	        
			String jsonString = JsonHelper.encodeObject2Json(dbTypeList);	
			write(jsonString, resp);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}finally{
			if(rs != null){
				rs.close();
			}
			if(smt != null){
				smt.close();
			}
			if(connection != null){
				connection.close();
			}
		}	
		return null;
	}
	
	public ModelAndView getRepList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		List<Dto<?,?>> dbTypeList = new ArrayList<Dto<?,?>>();
		try{
			UserBean userBean = req.getSession().getAttribute("userBean")==null?null:(UserBean)req.getSession().getAttribute("userBean");
			List<RepositoryBean> listReps = RepositoryUtil.getAllRepositories(userBean);
			
	        for (RepositoryBean repBean : listReps) {
	        	 Dto<String,Object> dto1 = new BaseDto();
	        	 dto1.put("repId",repBean.getRepositoryID());
	        	 dto1.put("repName",repBean.getRepositoryName());
	        	 dbTypeList.add(dto1);
	        }
			String jsonString = JsonHelper.encodeObject2Json(dbTypeList);	
			write(jsonString, resp);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	
	/**
	 * 测试数据库连接
	 * @param req
	 * @param resp
	 * @param source
	 * @return
	 * @throws IOException
	 */
	public ModelAndView testSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException{	
		Connection connection = null;
		PreparedStatement smt = null;
		ResultSet rs = null;
		String typeName = "";
		try{
			int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
			System.out.println("repId=" + repId);
			RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
			KettleEngine kettleEngine = new KettleEngineImpl4_3();
			Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
			connection = KettleEngineImpl4_3.getRepConnection(rep);	  
			smt=  connection.prepareStatement("SELECT *FROM R_DATABASE_TYPE WHERE ID_DATABASE_TYPE =?");
	        smt.setInt(1, Integer.valueOf(source.getSourceType()));
	        rs=smt.executeQuery();
	        while (rs.next()) {
	        	 typeName=rs.getString("DESCRIPTION");	
	        }
			source.setSourceTypeName(typeName);
			if(!checkDatasource(source)){
				this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
			} else {
				this.setOkTipMsg("数据源验证已通过，连接成功！", resp);
			}	
		} catch(Exception e){
			log.error(e.getMessage(), e);
			this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
		}
		return null;
	}
	

	private boolean checkDatasource(Datasource source) throws Exception{
		try{
			DatabaseMeta databaseMeta = new DatabaseMeta(source.getSourceName(),source.getSourceTypeName(),"Native",source.getSourceIp(),
						source.getSourceDataBaseName(),source.getSourcePort(),source.getSourceUserName(),source.getSourcePassword());
			@SuppressWarnings("deprecation")
			Database database = new Database(databaseMeta);
			database.connect();
			database.disconnect();
			return true;
		}catch(Exception e){
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
	public ModelAndView getDataSourceList(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		try {
			List<Datasource> list = datasourceService.querySourceList();
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}
	
}
