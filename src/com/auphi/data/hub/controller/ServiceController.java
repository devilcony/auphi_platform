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

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.DBHelper;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.domain.Service;
import com.auphi.data.hub.service.InterfaceService;
import com.auphi.ktrl.conn.util.ConnectionPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


/**
 * 对外数据接出接口管理控制器
 * 
 * @author zhangjiafeng
 *
 */
@ApiIgnore
@Controller("service")
public class ServiceController extends BaseMultiActionController {
	
	private static Log logger = LogFactory.getLog(ServiceController.class);
	
	private final static String INDEX = "admin/service_list";
	private final static String VIEW = "admin/viewService";
	
	@Autowired
	private InterfaceService interfaceService;
	

	
	
	/**
	 * 跳转到首页
	 * @param req
	 * @param resp
	 * @return
	 */
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	/**
	 * 查询角色列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			PaginationSupport<Object> page = interfaceService.queryServiceList(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	
	public ModelAndView saveService(HttpServletRequest req,HttpServletResponse resp,Service service) throws IOException{
		try{
			//生成服务标识
			String indentify = UUID.randomUUID().toString();
			//生成服务URL
			PropertiesHelper helper = PropertiesFactory.getPropertiesHelper(PropertiesFile.APP);
			//String transName = service.getTransName();
			//String[] nameAndId = transName.split("@");
			//service.setTransName(nameAndId[0]);
			//service.setJobConfigId(nameAndId[1]);
			String url = helper.getValue("service.url");
			service.setServiceIdentify(indentify);
			service.setServiceUrl(url);
			service.setCreateDate(new Date());
			interfaceService.saveService(service);
			this.setOkTipMsg("添加数据接出服务配置成功", resp);
		}catch(Exception e){
			this.setFailTipMsg("添加数据接出服务配置失败", resp);
			e.printStackTrace();
		}
		return null;
	}
	
	
	public ModelAndView updateService(HttpServletRequest req,HttpServletResponse resp,Service service) throws IOException{
		try{
//			String transName = service.getTransName();
//			String[] nameAndId = transName.split("@");
//			service.setTransName(nameAndId[0]);
//			service.setJobConfigId(nameAndId[1]);
			interfaceService.updateService(service);
			this.setOkTipMsg("编辑数据接出服务配置成功", resp);
		}catch(Exception e){
			this.setFailTipMsg("编辑数据接出服务配置失败", resp);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 删除服务权限信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deleteService(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String serviceIds = req.getParameter("strChecked");
			logger.info("serviceIds="+serviceIds);
			interfaceService.deleteServiceById(serviceIds);
			this.setOkTipMsg("服务接口删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("服务接口删除失败", resp);
		}
		return null;
	}
	
	
	
	public ModelAndView getJobList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		String returnType = req.getParameter("returnType");
		if(null!=returnType && returnType.equals("0")){
			return null;
		}
		List<Dto> trans = this.interfaceService.queryAllJob();
		List<Dto> jobs = new ArrayList<Dto>();
		for(Dto dto : trans){
			Dto<String,Object> dto1 = new BaseDto();
			dto1.put("transName", dto.getAsString("name")+"@" + dto.getAsString("taskId"));
			dto1.put("transDisplayName", dto.getAsString("name"));
			jobs.add(dto1);
		}
		String jsonString = JsonHelper.encodeObject2Json(jobs);	
		write(jsonString, resp);
		return null;
	}
	
	public ModelAndView getTransList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		
		List<Dto> trans = this.interfaceService.queryAllTrans();
		List<Dto> jobs = new ArrayList<Dto>();
		for(Dto dto : trans){
			Dto<String,Object> dto1 = new BaseDto();
			dto1.put("transName", dto.getAsString("name")+"@" + dto.getAsString("taskId"));
			dto1.put("transDisplayName", dto.getAsString("name"));
			jobs.add(dto1);
		}
		String jsonString = JsonHelper.encodeObject2Json(jobs);	
		write(jsonString, resp);
		return null;
	}
	
	public ModelAndView getSources(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		List<Datasource> sources = this.interfaceService.getAllDatasource();
		List<Dto> jobs = new ArrayList<Dto>();
		for(Datasource source : sources){
			Dto<String,Object> dto1 = new BaseDto();
			dto1.put("sourceName",source.getSourceId());
			dto1.put("dispalySourceName", source.getSourceName());
			jobs.add(dto1);
		}
		String jsonString = JsonHelper.encodeObject2Json(jobs);	
		write(jsonString, resp);
		return null;
	}
	
	public ModelAndView getTables(HttpServletRequest req,HttpServletResponse resp) throws Exception{ 
		String sourceId = req.getParameter("sourceId");
		Datasource source = this.interfaceService.getDatasourceById(sourceId);
		List<String> tableNames = DBHelper.getAllTableName(source);
		List<Dto> jobs = new ArrayList<Dto>();
		for(String tableName : tableNames){
			Dto<String,Object> dto1 = new BaseDto();
			dto1.put("tableName",tableName);
			dto1.put("dispalyTableName", tableName);
			jobs.add(dto1);
		}
		String jsonString = JsonHelper.encodeObject2Json(jobs);	
		write(jsonString, resp);
		return null;
	}
	
	Connection connection=null;
	PreparedStatement smt=null;
	ResultSet rs=null;
	public ModelAndView getDataSourceList(HttpServletRequest req,HttpServletResponse resp) throws Exception{ 
		List<Dto>  dataSourceList= new ArrayList<Dto>();
		try{
			String sql = "SELECT ID_DATABASE,NAME FROM R_DATABASE";
			connection = ConnectionPool.getConnection();
			smt=  connection.prepareStatement(sql);
			rs=smt.executeQuery();
	        while (rs.next()) {
	        	 Dto<String,Object> dto1 = new BaseDto();
	        	 String IdDatabase = String.valueOf(rs.getInt("ID_DATABASE"));	
	        	 String dbSource = rs.getString("NAME");
	        		dto1.put("idDatabase",IdDatabase);
					dto1.put("dispalySourceName",dbSource);
					dataSourceList.add(dto1);
	        }
			String jsonString = JsonHelper.encodeObject2Json(dataSourceList);	
			write(jsonString, resp);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.close();
		}	
		return null;
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
	
	
	
	
	public ModelAndView getAllServiceList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		List<Dto> service = this.interfaceService.getAllService();
		String jsonString = JsonHelper.encodeObject2Json(service);	
		write(jsonString, resp);
		return null;
	}
	
	public ModelAndView viewService(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String serviceId = req.getParameter("serviceId");
		Service service = this.interfaceService.getServiceById(serviceId);
		if(service.getReturnType().equals("1")){
			service.setReturnType("FTP");
			service.setReturnDataFormat("FTP File");
		} else {
			service.setReturnType("Webservice");
			service.setReturnDataFormat("JSON");
		}
		System.out.println(service.getServiceIdentify());
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("service", service);
		map.put("name", "zhangfeng");
		return new ModelAndView(VIEW,map);
	}
	
	

}
