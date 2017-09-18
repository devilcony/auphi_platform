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
package com.auphi.data.hub.core.util;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;

public class SessionContainer implements HttpSessionBindingListener {
	
	/**
	 * 登陆用户对象
	 */
	private UserInfo userInfo;

	/**
	 * 报表对象集
	 */
	private Dto reportDto;
	
	public SessionContainer() {
		super();
		reportDto =  new BaseDto();
	}
	

	/**
	 * 清除会话容器缓存对象
	 */
	public void cleanUp() {
		reportDto.clear();
	}



	/**
	 * 获取用户会话对象
	 * @return UserInfo
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * 设置用户会话对象
	 * @param userInfo
	 */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		
	}

	public void valueBound(HttpSessionBindingEvent event) {
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
	}
}
