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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.template.FileTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.TagHelper;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;
import com.auphi.data.hub.domain.tag.Dept;
import com.auphi.data.hub.domain.tag.User;


/**
 * 选择用户树标签
 * @author zhangjiafeng
 *
 */
public class SelectUserTreeTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(SelectUserTreeTag.class);

	private SystemDao baseDao;
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		baseDao = WebUtils.getBean(request,SystemDaoImpl.class);
		String deptid = request.getParameter("deptid");
		String roletype = request.getParameter("roletype");
		Dto deptDto = new BaseDto();
		deptDto.put("deptid", deptid);
		List deptList = baseDao.queryForList("TagSupport.queryDeptsForRoleGrant", deptDto);
		List userList = new ArrayList();
		Dto userDto = new BaseDto();
		//角色类型和用户类型代码是对应的
		userDto.put("usertype", roletype);
		if (roletype.equals("1")) {
			//注册用户
			//userDto.put("usertype4", "4");
		}
		for(int i = 0; i < deptList.size(); i++){
			Dept deptVo = (Dept)deptList.get(i);
			if(deptVo.getDeptid().equals(deptid)){
				deptVo.setIsroot("true");
			}
			userDto.put("deptid", deptVo.getDeptid());
			List tempList = baseDao.queryForList("TagSupport.queryUsersForRoleGrant", userDto);
			userList.addAll(tempList);
		}
		Dto grantDto = new BaseDto();
		grantDto.put("roleid", request.getParameter("roleid"));
		List grantList = baseDao.queryForList("TagSupport.queryGrantedUsersByRoleId", grantDto);
		for(int i = 0; i < userList.size(); i++){
			User userVo = (User)userList.get(i);
			String usertypeString = WebUtils.getCodeDesc("USERTYPE", userVo.getUsertype(), request);
			String usernameString = userVo.getUsername();
			usernameString += "[" + userVo.getAccount() + ", " + usertypeString + "]"; 
			userVo.setUsername(usernameString);
			if(checkGrant(grantList, userVo.getUserid())){
				userVo.setChecked("true");
			}
		}
        Dto dto = new BaseDto();
        dto.put("deptList", deptList);
        dto.put("userList", userList);
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
	 * @param pUserid
	 * @return
	 */
	private boolean checkGrant(List grantList, String pUserid){
		Boolean result = new Boolean(false);
		for(int i = 0; i < grantList.size(); i++){
			Dto dto = (BaseDto)grantList.get(i);
			if(pUserid.equals(dto.getAsString("userid"))){
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
