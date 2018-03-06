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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.auphi.ktrl.conn.util.ConnectionPool;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Repository;
import com.auphi.data.hub.service.RepositoryService;

/**
 * 数据源配置控制器
 * 
 * @author renxn
 *
 */
@Controller("repository")
public class RepositoryController extends BaseMultiActionController {
	
	private static Log log = LogFactory.getLog(RepositoryController.class);

	private final static String INDEX = "admin/repository";
	
	@Autowired
	private RepositoryService repositoryService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			PaginationSupport<Repository> page = repositoryService.queryRepositoryList(dto);
			String jsonString = JsonHelper.encodeObject2Json(page,"yyyy-MM-dd HH:mm:ss");	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	Connection connection=null;
	PreparedStatement smt=null;
	ResultSet rs=null;
	
	public ModelAndView saveRepository(HttpServletRequest req,HttpServletResponse resp,Repository repo) throws IOException{	
		try{
			repositoryService.saveRepository(repo);
			this.setOkTipMsg("添加资源库成功",resp); 
		}catch(Exception e){
			this.setFailTipMsg("添加资源库失败",resp);
			e.printStackTrace();
		}
		return null;
	}

	
	public ModelAndView updateRepository(HttpServletRequest req,HttpServletResponse resp,Repository repo) throws IOException{	
		try{
			repositoryService.updateRepository(repo);
			this.setOkTipMsg("修改资源库成功", resp);
		}catch(Exception e){
			this.setFailTipMsg("修改资源库失败", resp);
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 删除数据库
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deleteRepository(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String repositoryID = req.getParameter("strChecked");
			repositoryService.deleteRepository(repositoryID);
			this.setOkTipMsg("资源库删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("资源库删除失败", resp);
		}
		return null;
	}
	
	public ModelAndView getDbTypeList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		List<Dto> dbTypeList = new ArrayList<Dto>();
		try{
			String sql = "SELECT ID_DATABASE_TYPE AS DBTYPE,DESCRIPTION FROM R_DATABASE_TYPE";
			connection = ConnectionPool.getConnection();
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
			String jsonString = JsonHelper.encodeObject2Json(dbTypeList);	
			write(jsonString, resp);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.close();
		}	
		return null;
	}
	
	
	
	/**
	 * 测试数据库连接
	 * @param req
	 * @param resp
	 * @param repo
	 * @return
	 * @throws IOException
	 */
	public ModelAndView testSource(HttpServletRequest req,HttpServletResponse resp,Repository repo) throws IOException{	
		try{
			if(!checkDatasource(repo)){
				this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
			} else {
				this.setOkTipMsg("数据源验证已通过，连接成功！", resp);
			}	
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
		}
		return null;
	}
	

	private boolean checkDatasource(Repository repo) throws Exception{
		String typeName=null;
		try{
			connection = ConnectionPool.getConnection();	            
	        smt=  connection.prepareStatement("SELECT *FROM R_DATABASE_TYPE WHERE ID_DATABASE_TYPE =?");
	        smt.setInt(1, Integer.valueOf(repo.getDbType()));
	        rs=smt.executeQuery();
	        while (rs.next()) {
	        	 typeName=rs.getString("DESCRIPTION");	
	        }
			DatabaseMeta databaseMeta = new DatabaseMeta(repo.getRepositoryName(),typeName,"Native",repo.getDbHost(),
						repo.getDbName(),repo.getDbPort(),repo.getUserName(),repo.getPassword());
			Database database = new Database(databaseMeta);
			database.connect();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			//ConnectionPool.freeConn(rs, smt, null, connection);
	        this.close();
		}
	}
	
	//关闭资源
    public void close(){
        try{
        	// 关闭记录集
            if (rs != null) {
                rs.close();
            }
            // 关闭声明
            if (smt != null) {
            	smt.close();
            }
            // 关闭链接对象
            if (connection != null) {
            	connection.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    
	public ModelAndView getRepostoryList(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			List<Repository> list = repositoryService.queryRepositoryList();
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
