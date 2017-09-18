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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Department;
import com.auphi.data.hub.service.OrganizationService;

/**
 * 组织管理操作
 * @author zhangjiafeng
 *
 */
@Controller("organization")
public class OrganizationController extends BaseMultiActionController {
	
	private static Log logger = LogFactory.getLog(OrganizationController.class);

	private final static String INDEX = "admin/manageDepartment";
	
	@Autowired
	private OrganizationService organizationService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		super.removeSessionAttribute(req, "deptid");
		Dto inDto = new BaseDto();
		String deptid = super.getSessionContainer(req).getUserInfo().getDeptid();
		inDto.put("deptid", deptid);
		Dto outDto = organizationService.queryDeptinfoByDeptid(inDto);
		inDto.put("rootDeptid", outDto.getAsString("deptid"));
		inDto.put("rootDeptname", outDto.getAsString("deptname"));
		return new ModelAndView(INDEX,inDto);
	}
	/**
	 * 初始化部门树
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 */
	public ModelAndView departmentTreeInit(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		Dto dto = new BaseDto();
		String nodeid = req.getParameter("node");
		dto.put("parentid", nodeid);
		Dto outDto = organizationService.queryDeptItems(dto);
		write(outDto.getAsString("jsonString"), resp);
		return null;
	}
	
	/**
	 * 查询部门信息
	 * @param req
	 * @param resp
	 * @param dept
	 * @return
	 * @throws IOException
	 */
	public ModelAndView queryDeptsForManage(HttpServletRequest req,HttpServletResponse resp,Department dept) throws IOException{
		String deptid = req.getParameter("deptid");
		String queryParam = req.getParameter("queryParam");
		if (CloudUtils.isNotEmpty(deptid)) {
			super.setSessionAttribute(req, "deptid", deptid);
		}
		dept.setDeptid(deptid);
		dept.setQueryParam(queryParam);
		String start = req.getParameter("start");
		String pageSize = req.getParameter("limit");
		dept.setStart(Integer.parseInt(start) );
		dept.setEnd((Integer.parseInt(start) + Integer.parseInt(pageSize)));
		PaginationSupport<Dto> page = organizationService.getDeptsForManage(dept);
		write(JsonHelper.encodeObject2Json(page), resp);
		return  null;
	}

	/**
	 * 保存部门数据
	 * @param req
	 * @param resp
	 * @param dept
	 * @return
	 * @throws IOException
	 */
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,Department dept) throws IOException{
		organizationService.saveDeptItem(dept);
		setOkTipMsg("部门数据新增成功", resp);
		return null;
	}
	
	/**
	 * 修改部门数据
	 * @param req
	 * @param resp
	 * @param dept
	 * @return
	 * @throws IOException
	 */
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,Department dept) throws IOException{
		organizationService.updateDeptItem(dept);
		setOkTipMsg("部门数据修改成功", resp);
		return null;
	}

	/**
	 * 删除部门数据
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String strChecked = req.getParameter("strChecked");
		String type = req.getParameter("type");
		String deptid = req.getParameter("deptid");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		inDto.put("type", type);
		inDto.put("deptid", deptid);
		organizationService.deleteDeptItems(inDto);
		setOkTipMsg("部门数据删除成功", resp);
		return null;
	}
	
		
	
	public ModelAndView view(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String deptId = req.getParameter("deptId");
		Dto dto = organizationService.getDeptById(deptId);
		String json = JsonHelper.encodeObject2Json(dto);
		write(json, resp);
		return null;
	}

	
}
