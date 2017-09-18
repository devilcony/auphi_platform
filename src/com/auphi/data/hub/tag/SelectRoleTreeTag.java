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
package com.auphi.data.hub.tag;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.FileTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import com.auphi.data.hub.domain.tag.Dept;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.TagHelper;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;
import com.auphi.data.hub.domain.tag.Role;

/**
 * 选择角色树标签
 * @author zhangjiafeng
 *
 */
public class SelectRoleTreeTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(SelectRoleTreeTag.class);
	
	private SystemDao baseDao;
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		baseDao = WebUtils.getBean(request,SystemDaoImpl.class);
		String deptid = request.getParameter("deptid");
		String usertype = request.getParameter("usertype");
		Dto deptDto = new BaseDto();
		deptDto.put("deptid", deptid);
		List deptList = baseDao.queryForList("TagSupport.queryDeptsForUserGrant", deptDto);
		List roleList = new ArrayList();
		Dto roleDto = new BaseDto();
		//角色类型和用户类型代码是对应的
		//经办员和注册人员属于业务经办角色
		if (usertype.equals("4")) {
			//usertype = "1";
		}
		roleDto.put("roletype", usertype);
		for(int i = 0; i < deptList.size(); i++){
			Dept deptVo = (Dept)deptList.get(i);
			if(deptVo.getDeptid().equals(deptid)){
				deptVo.setIsroot("true");
			}
			roleDto.put("deptid", deptVo.getDeptid());
			List tempList = baseDao.queryForList("TagSupport.queryRolesForUserGrant", roleDto);
			roleList.addAll(tempList);
		}
		Dto grantDto = new BaseDto();
		grantDto.put("userid", request.getParameter("userid"));
		List grantList = baseDao.queryForList("TagSupport.queryGrantedRolesByUserId", grantDto);
		for(int i = 0; i < roleList.size(); i++){
			Role roleVo = (Role)roleList.get(i);
			String roletypeString = WebUtils.getCodeDesc("ROLETYPE", roleVo.getRoletype(), request);
			String rolenameString = roleVo.getRolename();
			rolenameString += "[" + roletypeString + "]"; 
			roleVo.setRolename(rolenameString);
			if(checkGrant(grantList, roleVo.getRoleid())){
				roleVo.setChecked("true");
			}
		}
        Dto dto = new BaseDto();
        dto.put("deptList", deptList);
        dto.put("roleList", roleList);
        dto.put("deptid", deptid);
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new FileTemplate();
		template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName()));
		StringWriter writer = engine.mergeTemplate(template, dto);
		try {
			pageContext.getOut().write(writer.toString());
		} catch (IOException e) {
			log.error(CloudConstants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.SKIP_BODY;
	}
	
	/**
	 * 检查授权
	 * @param grantList
	 * @param pRoleid
	 * @return
	 */
	private boolean checkGrant(List grantList, String pRoleid){
		Boolean result = new Boolean(false);
		for(int i = 0; i < grantList.size(); i++){
			Dto dto = (BaseDto)grantList.get(i);
			if(pRoleid.equals(dto.getAsString("roleid"))){
				result = new Boolean(true);
			}
		}
		return result.booleanValue();
	}
	
	/**
	 * 标签结束
	 */
	public int doEndTag() throws JspException{
		return super.EVAL_PAGE;
	}
	
	/**
	 * 释放资源
	 */
	public void release(){
		super.release();
	}

}
