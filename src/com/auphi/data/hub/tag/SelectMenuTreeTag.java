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
import com.auphi.data.hub.domain.tag.Menu;


/**
 * 选择菜单标签
 * @author zhangjiafeng
 *
 */
public class SelectMenuTreeTag extends TagSupport {
	
	private static Log log = LogFactory.getLog(SelectMenuTreeTag.class);

	private SystemDao baseDao;
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		baseDao = WebUtils.getBean(request,SystemDaoImpl.class);
		Dto grantDto = new BaseDto();
		grantDto.put("userid", request.getParameter("userid"));
		grantDto.put("authorizelevel", CloudConstants.AUTHORIZELEVEL_ACCESS);
		List grantedList = baseDao.queryForList("TagSupport.queryGrantedMenusByUserId", grantDto);
		List menuList = new ArrayList();
		String account = WebUtils.getSessionContainer(request).getUserInfo().getAccount();
		String developerAccount = WebUtils.getParamValue("DEFAULT_DEVELOP_ACCOUNT", request);
		String superAccount = WebUtils.getParamValue("DEFAULT_ADMIN_ACCOUNT", request);
		Dto qDto = new BaseDto();
		String userid = WebUtils.getSessionContainer(request).getUserInfo().getUserid();
		qDto.put("userid", userid);
		String userType = request.getParameter("usertype");
		if (userType.equals(CloudConstants.USERTYPE_ADMIN)) {
			qDto.put("menutype", CloudConstants.MENUTYPE_SYSTEM);
		}
		if (account.equalsIgnoreCase(developerAccount) || account.equalsIgnoreCase(superAccount)) {
			menuList = baseDao.queryForList("TagSupport.queryMenusForUserGrant", qDto);
		}else {
			menuList = baseDao.queryForList("TagSupport.queryMenusForGrant", qDto);
		}
		for(int i = 0; i < menuList.size(); i++){
			Menu menu = (Menu)menuList.get(i);
			if(checkGeant(grantedList, menu.getMenuid()).booleanValue()){
				menu.setChecked("true");
			}else {
				menu.setChecked("false");
			}
			if(menu.getParentid().equals("0")){
				menu.setIsRoot("true");
			}
			if(menu.getMenuid().length() < 6){
				menu.setExpanded("true");
			}
		}
		Dto dto = new BaseDto();
		dto.put("menuList", menuList);
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
	 * @param pMenuid
	 * @return
	 */
	private Boolean checkGeant(List grantList, String pMenuid){
		Boolean result = new Boolean(false);
		for(int i = 0; i < grantList.size(); i++){
			Dto dto = (BaseDto)grantList.get(i);
			if(pMenuid.equals(dto.getAsString("menuid"))){
				result = new Boolean(true);
			}
		}
		return result;
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
