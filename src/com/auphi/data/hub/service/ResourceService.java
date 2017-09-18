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

import java.util.List;

import com.auphi.data.hub.core.BaseBusinessService;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.Menu;


/**
 * 资源业务接口
 * @author mac
 *
 */
public interface ResourceService extends BaseBusinessService {
	
	/**
	 * 获取全局配置的配置资源
	 * @return
	 */
	public List<Dto> getResourceList();
	
	/**
	 * 获取全局编码
	 * @return
	 */
	public List<Dto> getCodeViewList();
	
	/**
	 * 根据编号查询该编号对应的菜单
	 * @return
	 */
	public Dto queryEamenuByMenuID(String menuId);
	
	/**
	 * 查询菜单列表
	 * @param dto
	 * @return
	 */
	public List<Dto<String,Object>> queryMenuItemsByDto(Dto dto);
	
	/**
	 * 查询菜单列表，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryMenuItems(Dto dto);
	
	/**
	 * 保存
	 * @param menu
	 * @return
	 */
	public Dto saveMenuItem(Menu menu);
	
	/**
	 * 修改
	 * @param menu
	 * @return
	 */
	public Dto updateMenuItem(Menu menu);
	
	/**
	 * 删除菜单
	 * @param dto
	 * @return
	 */
	public Dto deleteMenuItem(Dto dto);

}
