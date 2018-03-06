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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.template.StringTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.TagHelper;

/**
 * Body标签
 * @author zhangjiafeng
 */
public class BodyTag extends TagSupport{
	
	private static final long serialVersionUID = 1557937282966382705L;
	private static Log log = LogFactory.getLog(BodyTag.class);
	private String onload;
	private String any;
	private String cls;
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		Dto dto = new BaseDto();
		dto.put("onload", TagHelper.checkEmpty(onload));
		dto.put("any", TagHelper.checkEmpty(any));
		dto.put("cls", TagHelper.checkEmpty(cls));
		String tpl = "<body #if(${cls}!=*off*)class=*${cls}*#end #if(${onload}!=*off*)onload=*${onload}*#end #if(${any}!=*off*)${any}#end>";
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new StringTemplate(TagHelper.replaceStringTemplate(tpl));
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
	 * @param onload
	 */
	public int doEndTag() throws JspException{
		try {
			pageContext.getOut().write("</body>");
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
		any = null;
		cls = null;
		onload = null;
		super.release();
	}
	
	public void setOnload(String onload) {
		this.onload = onload;
	}
	public void setAny(String any) {
		this.any = any;
	}

	public void setCls(String cls) {
		this.cls = cls;
	} 

}
