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

import com.auphi.data.hub.core.util.CloudConstants;


/**
 * 初始模板引擎异常类
 * @author zhangjiafeng
 * @see RuntimeException
 */
public class InitVelocityEngineException extends RuntimeException{
	
	/**
	 * 缺省串行版本标识
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 构造函数1
	 * @param 
	 */
	public InitVelocityEngineException(){
		super(CloudConstants.Exception_Head + "初始化平台缺省模板引擎失败.\n");
	}
	
	/**
	 * 构造函数2
	 * @param 
	 */
	public InitVelocityEngineException(String msg){
		super(CloudConstants.Exception_Head + "初始化平台缺省模板引擎失败\n" + msg);
	}

}
