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
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.ServiceUser;
import com.auphi.data.hub.service.InterfaceServiceUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * 服务用户管理控制器
 * 
 * @author yiyabo
 *
 **/
@ApiIgnore
@Controller("serviceUser")
public class ServiceUserController extends BaseMultiActionController{
	
	private static Log logger = LogFactory.getLog(ServiceUserController.class);
	
	private final static String INDEX = "admin/serviceUser";
	
	@Autowired
	private InterfaceServiceUser interfaceServiceUser;
	
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
	 * 查询用户列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		try {
			String queryParam = req.getParameter("queryParam");
			System.out.println("queryParam="+queryParam);
			dto.put("queryParam", queryParam);
			this.setPageParam(dto, req);
			PaginationSupport<Object> page = interfaceServiceUser.queryServiceUsers(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存服务用户
	 * @param req
	 * @param resp
	 * @param serviceUser
	 * @return
	 * @throws IOException
	 */
	public ModelAndView saveServiceUser(HttpServletRequest req,HttpServletResponse resp,ServiceUser serviceUser) throws IOException{
		try{
			//生成服务标识
			String userName = serviceUser.getUserName();
			System.out.println("userName="+userName);
			String password = serviceUser.getPassword();
			System.out.println("password="+password);
			String systemName = serviceUser.getSystemName();
			System.out.println("systemName="+systemName);
			String systemIp = serviceUser.getSystemIp();
			System.out.println(systemIp);
			String systemDesc = serviceUser.getSystemDesc();
			System.out.println(systemDesc);
			
			interfaceServiceUser.saveServiceUser(serviceUser);
			this.setOkTipMsg("添加服务用户成功",resp); 
		}catch(Exception e){
			this.setFailTipMsg("添加服务用户失败",resp);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 更新服务用户
	 * @param req
	 * @param resp
	 * @param service
	 * @return
	 * @throws IOException
	 */
	public ModelAndView updateService(HttpServletRequest req,HttpServletResponse resp,ServiceUser serviceUser) throws IOException{
		try{
			String userId = serviceUser.getUserId();
			System.out.println("userId="+userId);
			String userName = serviceUser.getUserName();
			System.out.println("userName="+userName);
			String password = serviceUser.getPassword();
			System.out.println("password="+password);
			String systemName = serviceUser.getSystemName();
			System.out.println("systemName="+systemName);
			String systemIp = serviceUser.getSystemIp();
			System.out.println("systemIp="+systemIp);
			String systemDesc = serviceUser.getSystemDesc();
			System.out.println("systemDesc="+systemDesc);						
			
			interfaceServiceUser.updateServiceUser(serviceUser);
			this.setOkTipMsg("修改服务用户成功", resp);
		}catch(Exception e){
			this.setFailTipMsg("修改服务用户失败", resp);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 删除服务用户
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deleteServiceUser(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String serviceUserIds = req.getParameter("strChecked");
			interfaceServiceUser.deleteServiceUser(serviceUserIds);
			this.setOkTipMsg("服务用户删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("服务用户删除失败", resp);
		}
		return null;
	}
	
	/**
	 * 获取所有服务用户列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView getAllServiceUserList(HttpServletRequest req,HttpServletResponse resp) throws IOException{ 
		List<Dto> serviceUser = this.interfaceServiceUser.getAllServiceUser();
		String jsonString = JsonHelper.encodeObject2Json(serviceUser);	
		write(jsonString, resp);
		return null;
	}
}
