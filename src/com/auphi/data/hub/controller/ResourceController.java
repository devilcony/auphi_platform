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
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Menu;
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
 * 资源模型控制器
 * @author zhangjiafeng
 *
 */
@ApiIgnore
@Controller("resource")
public class ResourceController extends BaseMultiActionController {

	
	private  static Log logger = LogFactory.getLog(ResourceController.class);
	
	private final String INDEX = "admin/manageMenuResource";
	
	@Autowired
	private ResourceService resourceService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		super.removeSessionAttribute(req, "menuid");
		Dto dto = resourceService.queryEamenuByMenuID("01");
		req.setAttribute("rootMenuName", dto.getAsString("menuname"));
		return new ModelAndView(INDEX);
	}
	
	/**
	 * 查询菜单项目 生成菜单树
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView queryMenu(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto dto = new BaseDto();
		String nodeid = req.getParameter("node");
		dto.put("parentid", nodeid);
		List menuList = resourceService.queryMenuItemsByDto(dto);
		Dto menuDto = new BaseDto();
		for (int i = 0; i < menuList.size(); i++) {
			menuDto = (BaseDto) menuList.get(i);
			if (menuDto.getAsString("leaf").equals(CloudConstants.LEAF_Y))
				menuDto.put("leaf", new Boolean(true));
			else
				menuDto.put("leaf", new Boolean(false));
			if (menuDto.getAsString("id").length() == 4)
				// ID长度为4的节点自动展开
				menuDto.put("expanded", new Boolean(true));
		}
		write(JsonHelper.encodeObject2Json(menuList), resp);
		return null;
	}
	/**
	 * 分页列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView queryMenuItems(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto dto = new BaseDto();
		String menuid = req.getParameter("menuid");
		if (CloudUtils.isNotEmpty(menuid)) {
			super.setSessionAttribute(req, "menuid", menuid);
		}
		String nodeid = req.getParameter("node");
		if(CloudUtils.isNotEmpty(nodeid)){
			dto.put("parentid",nodeid);
		} else {
			dto.put("parentid", super.getSessionAttribute(req, "menuid"));
		}
		String queryParam = req.getParameter("queryParam");
		dto.put("queryParam", queryParam);
		this.setPageParam(dto, req);
		PaginationSupport<Dto<String,Object>> page = resourceService.queryMenuItems(dto);
		String jsonString = JsonHelper.encodeObject2Json(page);
		write(jsonString, resp);
		return null;
	}
	/**
	 * 保存
	 * @param req
	 * @param resp
	 * @param menu
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,Menu menu) throws IOException{
		resourceService.saveMenuItem(menu);
		setOkTipMsg("菜单数据新增成功", resp);
		return null;
	}

	/**
	 * 修改
	 * @param req
	 * @param resp
	 * @param menu
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,Menu menu) throws IOException{
		resourceService.updateMenuItem(menu);
		setOkTipMsg("菜单数据修改成功", resp);
		return null;
	}
	
	/**
	 * 修改
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String strChecked = req.getParameter("strChecked");
		String type = req.getParameter("type");
		String menuid = req.getParameter("menuid");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		inDto.put("type", type);
		inDto.put("menuid", menuid);
		resourceService.deleteMenuItem(inDto);
		setOkTipMsg("菜单数据删除成功", resp);
		return null;
	}
	
	
	
}
