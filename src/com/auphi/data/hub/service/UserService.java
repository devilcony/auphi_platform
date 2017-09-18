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

import com.auphi.data.hub.core.BaseBusinessService;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;

/**
 * 用户业务接口 
 * 
 * @author zhangjiafeng
 *
 */
public interface UserService extends BaseBusinessService {
	/**
	 * 保存用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveUserItem(UserInfo userInfo);

	/**
	 * 删除用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto deleteUserItems(Dto pDto);

	/**
	 * 修改用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto updateUserItem(UserInfo userInfo);

	/**
	 * 保存人员角色关联信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectedRole(Dto pDto);

	/**
	 * 保存人员菜单关联信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectedMenu(Dto pDto);
	
	/**
	 * 修改用户(提供首页修改使用)
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto updateUserItem4IndexPage(Dto pDto);
	
	/**
	 * 查询用户，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryUsers(Dto dto) throws SQLException;
	
	/**
	 * 根据userId和password查询用户信息
	 * @param userId
	 * @param password
	 * @return
	 */
	public  UserInfo  getUserByUserIdAndPassword (String userId,String password);
	
}
