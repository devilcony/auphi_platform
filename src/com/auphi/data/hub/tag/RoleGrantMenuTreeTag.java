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

import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import com.auphi.data.hub.domain.tag.Menu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.template.FileTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.TagHelper;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;

/**
 * RoleGrantMenuTree标签
 * @author zhangjiafeng
 *
 */
public class RoleGrantMenuTreeTag extends TagSupport {

	private static final long serialVersionUID = -6357529543149276866L;
	
	private static Log log = LogFactory.getLog(RoleGrantMenuTreeTag.class);
	private String key = "";
	private String authorizelevel = "1";
	
	private SystemDao baseDao;

	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		baseDao = WebUtils.getBean(request,SystemDaoImpl.class);
		Dto<String,Object> grantDto = new BaseDto();
		grantDto.put("roleid", request.getParameter("roleid"));
		grantDto.put("authorizelevel", authorizelevel);
		List grantedList = baseDao.queryForList("TagSupport.queryGrantedMenusByRoleId", grantDto);
		List menuList = new ArrayList();
		String account = WebUtils.getSessionContainer(request).getUserInfo().getAccount();
		String developerAccount = WebUtils.getParamValue("DEFAULT_DEVELOP_ACCOUNT", request);
		String superAccount = WebUtils.getParamValue("DEFAULT_ADMIN_ACCOUNT", request);
		Dto qDto = new BaseDto();
		String userid = WebUtils.getSessionContainer(request).getUserInfo().getUserid();
		qDto.put("userid", userid);
		String roletype = request.getParameter("roletype");
		String menutype = CloudConstants.MENUTYPE_SYSTEM;
		if (roletype.equals(CloudConstants.ROLETYPE_BUSINESS)) {
			menutype = CloudConstants.MENUTYPE_BUSINESS;
		}
		if (authorizelevel.equals(CloudConstants.AUTHORIZELEVEL_ADMIN)) {
			menutype = CloudConstants.MENUTYPE_BUSINESS;
		}
		qDto.put("roleid", roletype);
		qDto.put("menutype", menutype);
		if (account.equalsIgnoreCase(developerAccount) || account.equalsIgnoreCase(superAccount)) {
			menuList = baseDao.queryForList("TagSupport.queryMenusForRoleGrant", qDto);
		} else {
			qDto.put("menutype", CloudConstants.MENUTYPE_BUSINESS);
			menuList = baseDao.queryForList("TagSupport.queryMenusForGrant", qDto);
		}
		for (int i = 0; i < menuList.size(); i++) {
			Menu menuVo = (Menu) menuList.get(i);
			if (checkGeant(grantedList, menuVo.getMenuid()).booleanValue()) {
				menuVo.setChecked("true");
			} else {
				menuVo.setChecked("false");
			}
			if (menuVo.getParentid().equals("0")) {
				menuVo.setIsRoot("true");
			}
			if (menuVo.getMenuid().length() < 6) {
				menuVo.setExpanded("true");
			}
		}
		Dto dto = new BaseDto();
		dto.put("menuList", menuList);
		dto.put("key", key);
		dto.put("authorizelevel", authorizelevel);
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
	 * 
	 * @param grantList
	 * @param pMenuid
	 * @return
	 */
	private Boolean checkGeant(List grantList, String pMenuid) {
		Boolean result = new Boolean(false);
		for (int i = 0; i < grantList.size(); i++) {
			Dto dto = (BaseDto) grantList.get(i);
			if (pMenuid.equals(dto.getAsString("menuid"))) {
				result = new Boolean(true);
			}
		}
		return result;
	}

	/**
	 * 标签结束
	 */
	public int doEndTag() throws JspException {
		return super.EVAL_PAGE;
	}

	/**
	 * 释放资源
	 */
	public void release() {
		setKey(null);
		setAuthorizelevel(null);
		super.release();
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setAuthorizelevel(String authorizelevel) {
		this.authorizelevel = authorizelevel;
	}
}
