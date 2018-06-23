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
import com.auphi.data.hub.service.InterfaceService;
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

/**
 *服务接口监控控制器
 * 
 * @author yiyabo
 *
 */
@ApiIgnore
@Controller("serviceMonitor")
public class ServiceMonitorController extends BaseMultiActionController {

	private static Log logger = LogFactory.getLog(ServiceMonitorController.class);
	
	private final static String INDEX = "admin/serviceMonitor";
	
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
	 * 查询服务接口监控列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		try {
			String queryParam = req.getParameter("queryParam");
			logger.info("queryParam=" +queryParam);
			dto.put("queryParam", queryParam);
			this.setPageParam(dto, req);
			PaginationSupport<Object> page = this.interfaceService.queryServiceMonitorList(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
