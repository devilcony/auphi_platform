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

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.template.StringTemplate;
import com.auphi.data.hub.core.template.TemplateEngine;
import com.auphi.data.hub.core.template.TemplateEngineFactory;
import com.auphi.data.hub.core.template.TemplateType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.template.DefaultTemplate;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.TagHelper;

/**
 * 导入标签
 * @author mac
 *
 */
public class ImportTag extends TagSupport{
	
	private static final long serialVersionUID = -8575316307921772430L;
	private static Log log = LogFactory.getLog(ImportTag.class);
	private String src;
	
	/**
	 * 标签开始
	 */
	public int doStartTag() throws JspException{
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		String tpl = "#if($type==*css*)<link rel=*stylesheet* type=*text/css* href=*$src*/>#elseif($type==*js*)<script type=*text/javascript* src=*$src* ></script>#end";
		Dto dto = new BaseDto();
		dto.put("type", src.indexOf(".css") == -1 ? "js" : "css");
		dto.put("src", request.getContextPath() + src);
		TemplateEngine engine = TemplateEngineFactory.getTemplateEngine(TemplateType.VELOCITY);
		DefaultTemplate template = new StringTemplate(TagHelper.replaceStringTemplate(tpl));
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
	 * 标签结束
	 */
	public int doEndTag() throws JspException{
		return super.EVAL_PAGE;
	}
	
	/**
	 * 释放资源
	 */
	public void release(){
		src = null;
		super.release();
	}

	public void setSrc(String src) {
		this.src = src;
	}
}
