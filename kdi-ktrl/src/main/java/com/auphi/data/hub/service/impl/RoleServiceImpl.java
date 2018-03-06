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
package com.auphi.data.hub.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.idgenerator.IDHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.dao.SystemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.domain.Role;
import com.auphi.data.hub.service.RoleService;

/**
 * 角色与授权业务管理接口实现
 * 
 * @author zhangjiafeng
 *
 */
@Service("roleService")
public class RoleServiceImpl extends BaseBusinessServiceImpl implements
		RoleService {
	
	@Autowired
	private SystemDao systemDao;

	/**
	 * 保存角色
	 * @param pDto
	 * @return
	 */
	public Dto saveRoleItem(Role role){
		role.setRoleid(IDHelper.getRoleID(systemDao));
		systemDao.save("Role.saveRoleItem", role);
		return null;
	}
	
	/**
	 * 删除角色
	 * @param pDto
	 * @return
	 */
	public Dto deleteRoleItems(Dto pDto){
		Dto dto = new BaseDto();
		String[] arrChecked = pDto.getAsString("strChecked").split(",");
		for(int i = 0; i < arrChecked.length; i++){
			dto.put("roleid", arrChecked[i]);
			systemDao.delete("Role.deleteEaroleAuthorizeInRoleManage", dto);
			systemDao.delete("Role.deleteEauserauthorizeInRoleManage", dto);
			systemDao.delete("Role.deleteEarolemenupartInRoleManage", dto);
			systemDao.delete("Role.deleteEaroleInRoleManage", dto);
		}
		return null;
	}
	
	/**
	 * 修改角色
	 * @param pDto
	 * @return
	 */
	public Dto updateRoleItem(Role role){
		systemDao.update("Role.updateRoleItem", role);
		if(!role.getDeptid().equals(role.getDeptid_old())){
			systemDao.delete("Role.deleteEaroleAuthorizeInRoleManage", role);
		}
		return null;
	}
	
	/**
	 * 保存角色授权信息
	 * @param pDto
	 * @return
	 */
	public Dto saveGrant(Dto pDto){
		systemDao.delete("Role.deleteERoleGrants", pDto);
		String[] menuids = pDto.getAsString("menuid").split(",");
		for(int i = 0; i < menuids.length; i++){
			String menuid = menuids[i];
			if(CloudUtils.isEmpty(menuid))
				continue;
			pDto.put("menuid", menuid);
			pDto.put("authorizeid", IDHelper.getAuthorizeid4Role(systemDao));
			systemDao.save("Role.saveRoleGrantItem", pDto);
		}
		return null;
	}
	
	/**
	 * 保存角色用户关联信息
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectUser(Dto pDto){
		systemDao.delete("Role.deleteEaUserAuthorizeByRoleId", pDto);
		String[] userids = pDto.getAsString("userid").split(",");
		for(int i = 0; i < userids.length; i++){
			String userid = userids[i];
			if(CloudUtils.isEmpty(userid))
				continue;
			pDto.put("userid", userid);
			pDto.put("authorizeid", IDHelper.getAuthorizeid4User(systemDao));
			systemDao.save("Role.saveSelectUser", pDto);
		}
		return null;
	}
	
	
	/**
	 * 查询角色列表，支持分页
	 * @param dto
	 * @return
	 * @throws SQLException 
	 */
	public PaginationSupport<Dto<String,Object>> queryRolesForManage(Dto<String,Object> dto) throws SQLException{
		List<Dto<String,Object>> roleList = systemDao.queryForPage("Role.queryRolesForManage", dto);
		Integer total = (Integer)systemDao.queryForObject("Role.queryRolesForManageForPageCount", dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(roleList,total);
		return page;
	}
}
