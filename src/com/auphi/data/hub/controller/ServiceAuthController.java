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
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.ServiceAuth;
import com.auphi.data.hub.service.InterfaceServiceAuth;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

/**
 * 服务用户权限管理控制器
 * 
 * @author yiyabo
 *
 */
@Controller("serviceAuth")
public class ServiceAuthController extends BaseMultiActionController {
	
	private static Log logger = LogFactory.getLog(ServiceAuthController.class);
	
	private final static String INDEX = "admin/serviceAuth";
	
	@Autowired
	private InterfaceServiceAuth interfaceServiceAuth;
	
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
	 * 查询服务授权列表
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
			PaginationSupport<Object> page = interfaceServiceAuth.queryServiceAuths(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存服务授权信息
	 * @param req
	 * @param resp
	 * @param serviceUser
	 * @return
	 * @throws IOException
	 */
	public ModelAndView saveServiceAuth(HttpServletRequest req,HttpServletResponse resp,ServiceAuth serviceAuth) throws IOException{
		try{
			//生成服务标识
			String serviceId = serviceAuth.getServiceId();
			System.out.println("serviceId="+serviceId);
			String userId = serviceAuth.getUserId();
			System.out.println("userId="+userId);
			String authIP = serviceAuth.getAuthIP();
			System.out.println("authIP="+authIP);
			String use_dept = serviceAuth.getUse_dept();
			System.out.println(use_dept);
			String user_name = serviceAuth.getUser_name();
			System.out.println(user_name);
			
			interfaceServiceAuth.saveServiceAuth(serviceAuth);
			
			this.setOkTipMsg("服务授权成功",resp); 
		}catch(Exception e){
			this.setFailTipMsg("服务授权失败",resp);
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
	public ModelAndView updateServiceAuth(HttpServletRequest req,HttpServletResponse resp,ServiceAuth serviceAuth) throws IOException{
		try{
			String serviceId = serviceAuth.getServiceId();
			System.out.println("serviceId="+serviceId);
			String userId = serviceAuth.getUserId();
			System.out.println("userId="+userId);
			String authIP = serviceAuth.getAuthIP();
			System.out.println("authIP="+authIP);
			String use_dept = serviceAuth.getUse_dept();
			System.out.println(use_dept);
			String user_name = serviceAuth.getUser_name();
			System.out.println(user_name);					
			
			interfaceServiceAuth.updateServiceAuth(serviceAuth);
			
			this.setOkTipMsg("修改服务权限成功", resp);
		}catch(Exception e){
			this.setFailTipMsg("修改服务权限成功", resp);
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
	public ModelAndView deleteServiceAuth(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String serviceAuthIds = req.getParameter("strChecked");
			
			System.out.println("serviceAuthIds="+serviceAuthIds);
			
			interfaceServiceAuth.deleteServiceAuth(serviceAuthIds);
			this.setOkTipMsg("服务权限删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("服务权限删除失败", resp);
		}
		return null;
	}
	
}
