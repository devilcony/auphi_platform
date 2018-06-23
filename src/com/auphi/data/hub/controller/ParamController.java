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
import com.auphi.data.hub.domain.Param;
import com.auphi.data.hub.service.ParamService;
import com.auphi.data.hub.service.ResourceService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 全局参数维护
 * 
 * @author zhangjiafeng
 *
 */
@ApiIgnore
@Controller("param")
public class ParamController extends BaseMultiActionController {
	
	private Log logger = LogFactory.getLog(ParamController.class);
	
	private final static String INDEX = "admin/manageParam";
	
	@Autowired
	private ParamService paramService ;
	@Autowired
	private ResourceService resourceService;
	
	/**
	 * 跳转到首页
	 * @param req
	 * @param resp
	 * @return
	 */
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp){
		String paramid = req.getParameter("paramid");
		String paramkey = req.getParameter("paramkey");
		String queryParam = req.getParameter("queryParam");
		Dto dto = new BaseDto();
		dto.put("paramid", paramid);
		dto.put("paramkey", paramkey);
		dto.put("queryParam", queryParam);
		setPageParam(dto, req);
		try {
			PaginationSupport<Dto<String,Object>> page = paramService.queryParamsForManage(dto);
			write(JsonHelper.encodeObject2Json(page), resp);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
		
	}
	
	/**
	 * 新增
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,Param param) throws IOException{
		paramService.saveParamItem(param);
		setOkTipMsg("参数数据新增成功", resp);
		return null;
	}

	/**
	 * 修改
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,Param param) throws IOException{
		paramService.updateParamItem(param);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		outDto.put("msg", "参数数据修改成功!");
		write(outDto.toJson(), resp);		
		return null;
	}

	/**
	 * 删除
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String strChecked = req.getParameter("strChecked");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		paramService.deleteParamItem(inDto);
		setOkTipMsg("参数数据删除成功", resp);
		return null;
	}
	
	/**
	 * 内存同步
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView synMemory(HttpServletRequest req,HttpServletResponse resp) throws IOException{
	    List paramList = resourceService.getResourceList();
	    getServletContext().removeAttribute("EAPARAMLIST");
	    getServletContext().setAttribute("EAPARAMLIST", paramList);
		Dto outDto = new BaseDto();
		outDto.put("success", new Boolean(true));
		write(JsonHelper.encodeObject2Json(outDto), resp);		
		return null;
	}
}
