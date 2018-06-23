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
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.domain.Role;
import com.auphi.data.hub.domain.UserInfo;
import com.auphi.data.hub.service.OrganizationService;
import com.auphi.data.hub.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 角色与授权操作控制
 * 
 * @author mac
 *
 */
@ApiIgnore
@Controller("role")
public class RoleController extends BaseMultiActionController {
	
	private final static String INDEX = "admin/manageRole";
	
	private final static String OPERATOR = "admin/grantroletab/operatorTab";
	
	private final static String SELECTUSER = "admin/grantroletab/selectUserTab";
	
	private final static String MANAGER = "admin/grantroletab/managerTab";

	@Autowired
	private RoleService roleService;
	
	@Autowired
	private OrganizationService organizationService;
	
	/**
	 * 跳转到首页
	 * @param req
	 * @param resp
	 * @return
	 */
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		super.removeSessionAttribute(req, "deptid");
		Dto<String,Object> inDto = new BaseDto();
		String deptid = super.getSessionContainer(req).getUserInfo().getDeptid();
		inDto.put("deptid", deptid);
		Dto<String,Object> outDto = organizationService.queryDeptinfoByDeptid(inDto);
		outDto.put("rootDeptid", outDto.getAsString("deptid"));
		outDto.put("rootDeptname", outDto.getAsString("deptname"));
		UserInfo userInfo = getSessionContainer(req).getUserInfo();
		outDto.put("login_account", userInfo.getAccount());
		return new ModelAndView(INDEX,outDto);
	}
	
	/**
	 * 部门树初始化
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView deptTreeInit(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		String nodeid = req.getParameter("node");
		dto.put("parentid", nodeid);
		Dto<String,Object> outDto = organizationService.queryDeptItems(dto);
		write(outDto.getAsString("jsonString"), resp);
		return null;
	}
	
	/**
	 * 查询角色列表
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView queryRoles(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String deptid = req.getParameter("deptid");
		String queryParam = req.getParameter("queryParam");
		Dto<String,Object> dto = new BaseDto();
		if (CloudUtils.isNotEmpty(deptid)) {
			super.setSessionAttribute(req, "deptid", deptid);
		}
		if(!CloudUtils.isEmpty(req.getParameter("firstload"))){
			dto.put("deptid", super.getSessionContainer(req).getUserInfo().getDeptid());
		}else{
			dto.put("deptid", super.getSessionAttribute(req, "deptid"));
		}
		dto.put("roletype", CloudConstants.ROLETYPE_ADMIN);
		UserInfo userInfoVo = getSessionContainer(req).getUserInfo();
		if (WebUtils.getParamValue("DEFAULT_ADMIN_ACCOUNT", req).equals(userInfoVo.getAccount())) {
			dto.remove("roletype");
		}
		if (WebUtils.getParamValue("DEFAULT_DEVELOP_ACCOUNT", req).equals(userInfoVo.getAccount())) {
			dto.remove("roletype");
		}
		dto.put("queryParam", queryParam);
		this.setPageParam(dto, req);
		try {
			PaginationSupport<Dto<String, Object>> page = roleService.queryRolesForManage(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存角色信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,Role role) throws IOException{
		roleService.saveRoleItem(role);
		setOkTipMsg("角色新增成功", resp);
		return null;
	}
	
	/**
	 * 修改角色信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,Role role) throws IOException{
		roleService.updateRoleItem(role);
		setOkTipMsg("角色修改成功", resp);
		return null;
	}
		
	/**
	 * 删除角色
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String strChecked = req.getParameter("strChecked");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		roleService.deleteRoleItems(inDto);
		setOkTipMsg("角色删除成功", resp);
		return null;
	}
	
	/**
	 * 操作权限授权
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView operator(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		super.removeSessionAttribute(req, "ROLEID_ROLEACTION");
		String roleid = req.getParameter("roleid");
		super.setSessionAttribute(req, "ROLEID_ROLEACTION", roleid);
		return new ModelAndView(OPERATOR);
	}
	
	/**
	 * 选择用户
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView selectUser(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		return new ModelAndView(SELECTUSER);
	}
	/**
	 * 管理权限授权
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView manager(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		return new ModelAndView(MANAGER);
	}
	/**
	 * 保存角色授权信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView saveGrant(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto inDto = new BaseDto();
		inDto.put("menuid", req.getParameter("menuid"));
		inDto.put("authorizelevel", req.getParameter("key"));
		inDto.put("roleid", super.getSessionAttribute(req, "ROLEID_ROLEACTION"));
		roleService.saveGrant(inDto);
		String msg = "";
		if(inDto.getAsString("authorizelevel").equals(CloudConstants.AUTHORIZELEVEL_ACCESS))
			msg = "经办权限授权成功";
		if(inDto.getAsString("authorizelevel").equals(CloudConstants.AUTHORIZELEVEL_ADMIN))
			msg = "管理权限授权成功";
		setOkTipMsg(msg, resp);		
		return null;
	}
	/**
	 * 保存角色用户关联信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView saveUser(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto inDto = new BaseDto();
		inDto.put("userid", req.getParameter("userid"));
		inDto.put("roleid", super.getSessionAttribute(req, "ROLEID_ROLEACTION"));
		roleService.saveSelectUser(inDto);
		setOkTipMsg("您选择的角色人员关联数据保存成功", resp);
		return null;
	}
	
}
