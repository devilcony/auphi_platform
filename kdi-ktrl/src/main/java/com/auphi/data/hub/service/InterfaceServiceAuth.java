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
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.ServiceAuth;


/**
 * 服务授权接口
 * 
 * @author yiyabo
 *
 */
public interface InterfaceServiceAuth {

	/**
	 * 查询服务权限信息，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Object> queryServiceAuths(Dto dto) throws SQLException;
	
	/**
	 * 保存服务权限信息
	 * 
	 * @param pDto
	 * @return
	 */
	public void saveServiceAuth(ServiceAuth serviceAuth);
	
	/**
	 * 修改服务权限信息
	 * 
	 * @param pDto
	 * @return
	 */
	public void updateServiceAuth(ServiceAuth serviceAuth);
	
	/**
	 * 删除服务权限信息
	 * 
	 * @param pDto
	 * @return
	 */
	public void deleteServiceAuth(String serviceAuthIds);
	
	/**
	 * 根据条件获取服务权限
	 * 
	 * @param dto
	 * @return
	 */
	public Dto getServiceAuth(Dto dto);
	
	
}
