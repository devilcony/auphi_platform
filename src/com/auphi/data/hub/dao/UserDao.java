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
package com.auphi.data.hub.dao;

import java.util.List;

import com.auphi.data.hub.core.BaseDao;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;

/**
 * 
 * 
 * @author zhangjiafeng
 *
 */
public interface UserDao extends BaseDao {
	/**
	 * 新增或者修改用户信息
	 * @param user
	 */
	public void saveOrUpdateUser(UserInfo user);
	/**
	 * 根据用户编号查询用户信息
	 * @param dto
	 * @return
	 */
	public UserInfo getUserById(Dto dto);
	
	/**
	 * 根据用户编号删除用户信息
	 * @param dto
	 */
	public void deleteUserById(Dto dto);
	
	/**
	 * 根据条件查询用户的总记录数
	 * @param dto
	 * @return
	 */
	public int getUserTotal(Dto dto);
	
	/**
	 * 根据条件查询用户列表
	 * @param dto
	 * @return
	 */
	public List<UserInfo> getUserList(Dto dto);
	
	
}
