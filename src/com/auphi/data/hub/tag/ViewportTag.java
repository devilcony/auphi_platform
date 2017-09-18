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

import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.template.FileTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.SessionContainer;
import com.auphi.data.hub.core.util.TagHelper;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.impl.SystemDaoImpl;
import com.auphi.data.hub.domain.tag.Menu;
import com.auphi.data.hub.service.TagSupportService;
import com.auphi.data.hub.service.impl.TagSupportServiceImpl;

/**
 * viewport标签主要用户根据不同用户生成左边的功能菜单树及框架West的title
 * 
 * @author zhangjiafeng
 *
 */
public class ViewportTag extends TagSupport {

	private static final long serialVersionUID = 8666209486549436044L;

	private static Log log = LogFactory.getLog(ViewportTag.class);

	@Autowired
	private TagSupportService tagSupportService ;
	@Autowired
	private SystemDao baseDao;
	
	private String northTitle = "";
	private String westTitle = "";
	private String scriptStart = "<script type=\"text/javascript\">";
	private String scriptEnd = "</script>";

	/**
	 * 标签初始方法
	 * 
	 * @return
	 * @throws JspException
	 */
	public int doStartTag() throws JspException {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		baseDao = WebUtils.getBean(request,SystemDaoImpl.class);
		tagSupportService = WebUtils.getBean(request,TagSupportServiceImpl.class);
		return super.SKIP_BODY;
	}

	/**
	 * 标签主体
	 * 
	 * @return
	 * @throws JspException
	 */
	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			writer.print(getPanelScript());
		} catch (Exception e) {
			log.error(CloudConstants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}

	/**
	 * 获取Viewport标记脚本
	 * 
	 * @return 返回Viewport标记脚本
	 */
	private String getPanelScript() {
		HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
		String contextPath = request.getContextPath();
		Dto<String,Object> dto = new BaseDto();
		dto.put("northTitle", northTitle);
		dto.put("centerTitle",CloudUtils.isEmpty(WebUtils.getParamValue("MENU_FIRST", request)) ? "请配置" : WebUtils.getParamValue(
						"MENU_FIRST", request));
		dto.put("welcomePageTitle", CloudUtils.isEmpty(WebUtils.getParamValue("WELCOME_PAGE_TITLE", request)) ? "请配置" : WebUtils.getParamValue(
				"WELCOME_PAGE_TITLE", request));
		dto.put("banner", request.getContextPath() + WebUtils.getParamValue("INDEX_BANNER", request));
		dto.put("westTitle", westTitle);
		dto.put("scriptStart", scriptStart);
		dto.put("scriptEnd", scriptEnd);
		dto.put("copyright", WebUtils.getParamValue("BOTTOM_COPYRIGHT", request));
		String activeOnTop = "true";
		if ("0".equals(WebUtils.getParamValue("WEST_CARDMENU_ACTIVEONTOP", request))) {
			activeOnTop = "false";
		}
		dto.put("activeOnTop", activeOnTop);
		SessionContainer sessionContainer = WebUtils.getSessionContainer(request);
		String userid = sessionContainer.getUserInfo().getUserid();
		Dto dto2 = new BaseDto();
		dto2.put("userid", userid);
		String account = sessionContainer.getUserInfo().getAccount();
		account = account == null ? "" : account;
		String accountType = CloudConstants.ACCOUNTTYPE_NORMAL;
		if (account.equalsIgnoreCase(WebUtils.getParamValue("DEFAULT_ADMIN_ACCOUNT", request))) {
			accountType = CloudConstants.ACCOUNTTYPE_SUPER;
		} else if (account.equalsIgnoreCase(WebUtils.getParamValue("DEFAULT_DEVELOP_ACCOUNT", request))) {
			accountType = CloudConstants.ACCOUNTTYPE_DEVELOPER;
		}
		dto2.put("accountType", accountType);
		dto.put("accountType", accountType);
		List cardList = tagSupportService.getCardList(dto2).getDefaultAList();
		for (int i = 0; i < cardList.size(); i++) {
			Menu cardVo = (Menu) cardList.get(i);
			if (i != cardList.size() - 1) {
				cardVo.setIsNotLast("true");
			}
		}
		dto.put("date", CloudUtils.getCurDate());
		dto.put("week", CloudUtils.getWeekDayByDate(CloudUtils.getCurDate()));
		dto.put("welcome", getWelcomeMsg());
		dto.put("cardList", cardList);
		dto.put("username", sessionContainer.getUserInfo().getUsername());
		dto.put("account", sessionContainer.getUserInfo().getAccount());
		dto.put("contextPath", contextPath);
		Dto qDto = new BaseDto();
		qDto.put("deptid", sessionContainer.getUserInfo().getDeptid());
		dto.put("deptname", tagSupportService.getDepartmentInfo(qDto).getAsString("deptname"));
		Dto themeDto = new BaseDto();
		themeDto.put("userid", WebUtils.getSessionContainer(request).getUserInfo().getUserid());
		Dto resultDto = new BaseDto();
		resultDto = tagSupportService.getEauserSubInfo(themeDto);
		String theme = resultDto.getAsString("theme");
		theme = CloudUtils.isEmpty(theme) ? "default" : theme;
		dto.put("theme", theme);
		dto.put("themeColor", getThemeColor(theme));
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new FileTemplate();
		template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName()));
		StringWriter writer = engine.mergeTemplate(template, dto);
		String treesString = generateCardTrees(dto);
		return treesString + "\n" + writer.toString();
	}

	/**
	 * 生成卡片树
	 * 
	 * @param rootMenuId
	 */
	private String generateCardTrees(Dto pDto) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		SessionContainer sessionContainer = WebUtils.getSessionContainer(request);
		String userid = sessionContainer.getUserInfo().getUserid();
		Dto qDto = new BaseDto();
		qDto.put("userid", userid);
		List cardList = (List) pDto.get("cardList");
		String treesString = scriptStart + "Ext.onReady(function(){";
		for (int i = 0; i < cardList.size(); i++) {
			Menu cardVo = (Menu) cardList.get(i);
			qDto.put("menuid", cardVo.getMenuid());
			qDto.put("accountType", pDto.getAsString("accountType"));
			List menuList = tagSupportService.getCardTreeList(qDto).getDefaultAList();
			String rootName = (String)baseDao.queryForObject("Organization.getMenuNameForCNPath", "01");
			Dto pathDto = new BaseDto();
			pathDto.put("01", rootName);
			Dto dto = new BaseDto();
			dto.put("menuList", generateMenuPathName(menuList, pathDto));
			dto.put("menuid", cardVo.getMenuid());
			TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
			DefaultTemplate template = new FileTemplate();
			template.setTemplateResource(TagHelper.getTemplatePath(getClass().getName(), "CardTreesTag.tpl"));
			StringWriter writer = engine.mergeTemplate(template, dto);
			treesString = treesString + "\n" + writer.toString();
		}
		return treesString + "\n});" + scriptEnd;
	}

	/**
	 * 生成菜单路径对应中文名
	 * 
	 * @param pMenuList 菜单列表
	 * @return
	 */
	public List generateMenuPathName(List pMenuList, Dto pDto) {
		for (int i = 0; i < pMenuList.size(); i++) {
			Menu vo = (Menu) pMenuList.get(i);
			pDto.put(vo.getMenuid(), vo.getMenuname());
		}
		for (int i = 0; i < pMenuList.size(); i++) {
			String path = "";
			Menu vo = (Menu) pMenuList.get(i);
			String menuId = vo.getMenuid();
			vo.setIcon(CloudUtils.replaceBr(vo.getIcon()));
			int temp = menuId.length() / 2;
			int m = 0, k = 2;
			for (int j = 0; j < temp; j++) {
				path += pDto.getAsString(menuId.substring(m, k)) + " -> ";
				k += 2;
			}
			vo.setMenupath(path.substring(0, path.length() - 4));
		}
		return pMenuList;
	}
	
	
	

	/**
	 * 释放资源
	 */
	public void release() {
		super.release();
		northTitle = null;
		westTitle = null;
	}

	/**
	 * 生成问候信息
	 * 
	 * @return
	 */
	private String getWelcomeMsg() {
		String welcome = "晚上好";
		Integer timeInteger = new Integer(CloudUtils.getCurrentTime("HH"));
		if (timeInteger.intValue() >= 7 && timeInteger.intValue() <= 12) {
			welcome = "上午好";
		} else if (timeInteger.intValue() > 12 && timeInteger.intValue() < 19) {
			welcome = "下午好";
		}
		return welcome;
	}

	/**
	 * 获取和主题对应匹配的颜色值
	 */
	private String getThemeColor(String theme) {
		String color = "slategray";
		if (theme.equalsIgnoreCase("default")) {
			color = "4798D7";
		} else if (theme.equalsIgnoreCase("lightRed")) {
			color = "F094C9";
		} else if (theme.equalsIgnoreCase("lightYellow")) {
			color = "EAAA85";
		} else if (theme.equalsIgnoreCase("gray")) {
			color = "969696";
		} else if (theme.equalsIgnoreCase("lightGreen")) {
			color = "53E94E";
		} else if (theme.equalsIgnoreCase("purple2")) {
			color = "BC5FD8";
		}
		return color;
	}

	public void setNorthTitle(String northTitle) {
		this.northTitle = northTitle;
	}

	public void setWestTitle(String westTitle) {
		this.westTitle = westTitle;
	}

	public void setScriptStart(String scriptStart) {
		this.scriptStart = scriptStart;
	}

	public void setScriptEnd(String scriptEnd) {
		this.scriptEnd = scriptEnd;
	}
}
