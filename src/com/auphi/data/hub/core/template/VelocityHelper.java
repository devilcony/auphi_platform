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

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;


/**
 * Velocity模板引擎辅助类
 * @author zhangjiafeng
 */
public class VelocityHelper {
	
	private static Log log = LogFactory.getLog(VelocityHelper.class);
	
	/**
	 * 实例化Velocity模板引擎并返回<br>
	 * <b>说明:</b>为了避免VelocityEngine跟其他项目用到的VelocityEngine产生冲突,所以这里使用多实例引擎,
	 * 而不是单一实例(singleton)引擎.
	 * @return 返回VelocityEngine实例
	 * @throws InitVelocityEngineException 
	 */
	public static VelocityEngine getVelocityEngine() throws InitVelocityEngineException{
		VelocityEngine ve = new VelocityEngine();
		try {
			ve.init(getDefaultProperties());
		} catch (Exception e) {
			throw new InitVelocityEngineException(e.getMessage());
		}
		return ve;
	}
	
	/**
	 * 加载Velocity模板引擎属性配置文件
	 * @return
	 */
	public static Properties getDefaultProperties() {
	   	InputStream is = VelocityHelper.class.getResourceAsStream("velocity.properties");
	   	Properties props = new Properties();
		try {
			props.load(is);
			is.close();
		} catch (Exception e) {
		    log.error(CloudConstants.Exception_Head + "导入Velocity模板引擎属性配置文件出错");
		    log.error(e.getMessage());
		    }
			return props;
		}
	
	/**
	 * 将Dto对象转换为VelocityContext对象
	 * @param pDto 传入的Dto对象
	 * @return 返回VelocityContext对象
	 */
	public static VelocityContext convertDto2VelocityContext(Dto pDto){
		if(CloudUtils.isEmpty(pDto))
			return null;
		Iterator it = pDto.keySet().iterator();
		VelocityContext context = new VelocityContext();
		while(it.hasNext()){
			String key = (String)it.next();
			Object value = pDto.get(key);
			context.put(key, value);
		}
		return context;
	}
}
