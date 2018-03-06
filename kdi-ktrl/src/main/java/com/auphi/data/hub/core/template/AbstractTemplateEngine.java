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
package com.auphi.data.hub.core.template;

import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;

/**
 * 模板引擎抽象实现的基类
 * @author zhangjiafeng
 *
 */
public abstract class AbstractTemplateEngine implements TemplateEngine {
	
	private Log log = LogFactory.getLog(AbstractTemplateEngine.class);
	
	/**
	 * 驱动模板
	 * @param pTemplate 模板对象
	 * @param pDto 合并参数集合(将模板中所需变量全部压入Dto)
	 * @return writer引擎驱动后的StringWriter对象
	 */
	public StringWriter mergeTemplate(DefaultTemplate pTemplate, Dto dto) {
		StringWriter writer = null;
		if(pTemplate instanceof StringTemplate){
			writer = mergeStringTemplate(pTemplate, dto);
		}else if(pTemplate instanceof FileTemplate){
			writer = mergeFileTemplate(pTemplate, dto);
		}else{
			throw new IllegalArgumentException(CloudConstants.Exception_Head + "不支持的模板" );
		}
		return writer;
	}
	
	/**
	 * 驱动字符串模板
	 * @param pTemplate 模板对象
	 * @return 返回StringWriter对象
	 * @param pDto 合并参数集合(将模板中所需变量全部压入Dto)
	 * @throws Exception 
	 */
	protected abstract StringWriter mergeStringTemplate(DefaultTemplate pTemplate, Dto pDto);
	
	/**
	 * 驱动文件模板
	 * @param pTemplate 模板对象
	 * @param pDto 合并参数集合(将模板中所需变量全部压入Dto)
	 * @return 返回StringWriter对象
	 * @throws Exception 
	 */
	protected abstract StringWriter mergeFileTemplate(DefaultTemplate pTemplate, Dto pDto);

}
