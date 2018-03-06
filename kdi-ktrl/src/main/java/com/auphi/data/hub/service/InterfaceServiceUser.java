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
package com.auphi.data.hub.service;

import java.sql.SQLException;
import java.util.List;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.ServiceUser;

/**
 * 服务用户管理接口
 * @author yiyabo
 *
 */
public interface InterfaceServiceUser {

	/**
	 * 查询服务用户，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Object> queryServiceUsers(Dto dto) throws SQLException;
	
	/**
	 * 保存服务用户
	 * 
	 * @param pDto
	 * @return
	 */
	public void saveServiceUser(ServiceUser serviceUser);
	
	/**
	 * 修改服务用户
	 * 
	 * @param pDto
	 * @return
	 */
	public void updateServiceUser(ServiceUser serviceUser);
	
	/**
	 * 删除用户
	 * 
	 * @param pDto
	 * @return
	 */
	public void deleteServiceUser(String serviceUserIds);
	
	
	/**
	 * 根据条件查询服务用户
	 */
	public Dto queryServiceUser(Dto dto);
	
	/**
	 * 获得所有的用户
	 */
	public List<Dto> getAllServiceUser();
	
}
