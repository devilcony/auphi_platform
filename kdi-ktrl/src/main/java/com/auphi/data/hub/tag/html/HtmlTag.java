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
package com.auphi.data.hub.tag.html;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.FileTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import com.auphi.data.hub.core.util.TagConstant;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.domain.UserInfo;
import com.auphi.data.hub.service.impl.TagSupportServiceImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.TagHelper;
import com.auphi.data.hub.service.TagSupportService;

/**
 * html 标签
 *
 * @author mac
 *
 */
public class HtmlTag extends TagSupport{
	
	private static final long serialVersionUID = 2784250893748035663L;

	private TagSupportService tagSupportService;
	
	private static Log log = LogFactory.getLog(HtmlTag.class);
	private String extDisabled;
	private String title;
	private String jqueryEnabled;
	private String showLoading;
	private String uxEnabled = "true";
	private String fcfEnabled = "false";
	private String doctypeEnable="false";  //带有时分秒选择的控件的页面需要设置为:true
	private String exportParams = "false";
	private String exportUserinfo = "false";
	private String isSubPage = "true";
	private String urlSecurity2 = "true";
	private String exportExceptionWindow = "false";
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		tagSupportService = WebUtils.getBean(request,TagSupportServiceImpl.class);
		UserInfo userInfo = WebUtils.getSessionContainer(request).getUserInfo();
		String contextPath = request.getContextPath();
		request.setAttribute("webContext", contextPath);
		Dto dto = new BaseDto();
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.CC2);
		String micolor = pHelper.getValue("micolor", "blue");
		dto.put("micolor", micolor);
		String urlSecurity = pHelper.getValue("urlSecurity", "1");
		dto.put("urlSecurity", urlSecurity);
		dto.put("urlSecurity2", urlSecurity2);
		dto.put("userInfo", userInfo);
		dto.put("ajaxErrCode", CloudConstants.Ajax_Timeout);
		dto.put("requestURL", request.getRequestURL());
		dto.put("contextPath", contextPath);
		dto.put("doctypeEnable", doctypeEnable);
		dto.put("extDisabled", CloudUtils.isEmpty(extDisabled) ? "false" : extDisabled);
		dto.put("title", CloudUtils.isEmpty(title) ? "eRedG4" : title);
		dto.put("jqueryEnabled", CloudUtils.isEmpty(jqueryEnabled) ? "false" : jqueryEnabled);
		dto.put("showLoading", CloudUtils.isEmpty(showLoading) ? "true" : showLoading);
		dto.put("uxEnabled", uxEnabled);
		dto.put("exportExceptionWindow", exportExceptionWindow);
		dto.put("fcfEnabled", fcfEnabled);
		dto.put("exportParams", exportParams);
		dto.put("exportUserinfo", exportUserinfo);
		dto.put("isSubPage", isSubPage);
		dto.put("pageLoadMsg", WebUtils.getParamValue("PAGE_LOAD_MSG", request));
		String titleIcon = WebUtils.getParamValue("TITLE_ICON", request);
		dto.put("titleIcon", CloudUtils.isEmpty(titleIcon) ? "eredg4.ico" : titleIcon);
		if (exportParams.equals("true")) {
			dto.put("paramList", WebUtils.getParamList(request));
		}
		PropertiesHelper p = PropertiesFactory.getPropertiesHelper(PropertiesFile.CC2);
		dto.put("extMode", p.getValue("extMode", TagConstant.Ext_Mode_Run));
		dto.put("runMode", p.getValue("runMode", TagConstant.RUN_MODE_NORMAL));
		Dto themeDto = new BaseDto();
		Dto resultDto = new BaseDto();
		if(CloudUtils.isNotEmpty(userInfo)){
			themeDto.put("userid", userInfo.getUserid());
			resultDto = tagSupportService.getEauserSubInfo(themeDto);
		}
		String theme = null;
		if(CloudUtils.isNotEmpty(resultDto))
			theme = resultDto.getAsString("theme");
		String defaultTheme = WebUtils.getParamValue("SYS_DEFAULT_THEME", request);
		theme = CloudUtils.isEmpty(theme) ? defaultTheme : theme;
		dto.put("theme", theme);
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
		return super.EVAL_BODY_INCLUDE;
	}
	
	/**
	 * 标签结束
	 */
	public int doEndTag() throws JspException{
		try {
			pageContext.getOut().write("</html>");
		} catch (IOException e) {
			log.error(CloudConstants.Exception_Head + e.getMessage());
			e.printStackTrace();
		}
		return super.EVAL_PAGE;
	}
	
	/**
	 * 释放资源
	 */
	public void release(){
		extDisabled = null;
		title = null;
		jqueryEnabled = null;
		uxEnabled = null;
		fcfEnabled = null;
		doctypeEnable = null;
		exportParams = null;
		exportUserinfo = null;
		isSubPage = null;
		urlSecurity2 = null;
		super.release();
	}

	public void setExtDisabled(String extDisabled) {
		this.extDisabled = extDisabled;
	}

	public void setJqueryEnabled(String jqueryEnabled) {
		this.jqueryEnabled = jqueryEnabled;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setShowLoading(String showLoading) {
		this.showLoading = showLoading;
	}

	public void setUxEnabled(String uxEnabled) {
		this.uxEnabled = uxEnabled;
	}

	public String getFcfEnabled() {
		return fcfEnabled;
	}

	public void setFcfEnabled(String fcfEnabled) {
		this.fcfEnabled = fcfEnabled;
	}

	public void setDoctypeEnable(String doctypeEnable) {
		this.doctypeEnable = doctypeEnable;
	}

	public void setExportParams(String exportParams) {
		this.exportParams = exportParams;
	}

	public void setExportUserinfo(String exportUserinfo) {
		this.exportUserinfo = exportUserinfo;
	}

	public void setIsSubPage(String isSubPage) {
		this.isSubPage = isSubPage;
	}

	public void setUrlSecurity2(String urlSecurity2) {
		this.urlSecurity2 = urlSecurity2;
	}
}
