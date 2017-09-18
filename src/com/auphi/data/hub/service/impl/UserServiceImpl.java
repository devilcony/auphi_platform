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
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.core.idgenerator.IDHelper;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.dao.UserDao;
import com.auphi.data.hub.service.UserService;

/**
 * @author mac
 *
 */
@Service("userService")
public class UserServiceImpl extends BaseBusinessServiceImpl implements
		UserService {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	SystemDao systemDao;
	/**
	 * 保存用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveUserItem(UserInfo userInfo){
		Dto outDto = new BaseDto();
		userInfo.setEnabled(CloudConstants.ENABLED_Y);
		Integer temp = (Integer) userDao.queryForObject("User.checkAccount", userInfo);
		if (temp.intValue() != 0) {
			outDto.put("msg", "登录账户" + outDto.getAsString("account") + "已被占用,请尝试其它帐户!");
			outDto.put("success", new Boolean(false));
			return outDto;
		}
		userInfo.setUserid(IDHelper.getUserID(systemDao));
		String mPasswor = CloudUtils.encryptBasedMd5(userInfo.getPassword());
		userInfo.setPassword(mPasswor);
		userInfo.setTheme(CloudConstants.DEFAULT_THEME);
		userDao.save("User.saveUserItem", userInfo);
		userDao.save("User.saveEausersubinfoItem", userInfo);
		outDto.put("msg", "用户数据新增成功");
		outDto.put("success", new Boolean(true));
		return outDto;
	}

	/**
	 * 删除用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto deleteUserItems(Dto pDto){
		Dto dto = new BaseDto();
		String[] arrChecked = pDto.getAsString("strChecked").split(",");
		for (int i = 0; i < arrChecked.length; i++) {
			dto.put("userid", arrChecked[i]);
			userDao.update("User.updateEauserInUserManage", dto);
			userDao.delete("User.deleteEauserauthorizeInUserManage", dto);
			userDao.delete("User.deleteEausermenumapByUserid", dto);
			userDao.delete("User.deleteEausersubinfoByUserid", dto);
		}
		return null;
	}

	/**
	 * 修改用户
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto updateUserItem(UserInfo userInfo){
		String mPasswor = CloudUtils.encryptBasedMd5(userInfo.getPassword());
		userInfo.setPassword(mPasswor);
		userDao.update("User.updateUserItem", userInfo);
		if (!userInfo.getDeptid().equals(userInfo.getDeptid_old())) {
			userDao.delete("User.deleteEauserauthorizeInUserManage", userInfo);
			userDao.delete("User.deleteEausermenumapByUserId", userInfo);
		}
		return null;
	}

	/**
	 * 保存人员角色关联信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectedRole(Dto pDto){
		userDao.delete("User.deleteEaUserAuthorizeByUserId", pDto);
		String[] roleids = pDto.getAsString("roleid").split(",");
		for (int i = 0; i < roleids.length; i++) {
			String roleid = roleids[i];
			if (CloudUtils.isEmpty(roleid)){
				continue;
			}
			pDto.put("roleid", roleid);
			pDto.put("authorizeid", IDHelper.getAuthorizeid4User(systemDao));
			userDao.save("User.saveSelectedRole", pDto);
		}
		return null;
	}

	/**
	 * 保存人员菜单关联信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto saveSelectedMenu(Dto pDto){
		userDao.delete("User.deleteEausermenumapByUserId", pDto);
		String[] menuids = pDto.getAsString("menuid").split(",");
		for (int i = 0; i < menuids.length; i++) {
			String menuid = menuids[i];
			if (CloudUtils.isEmpty(menuid))
				continue;
			pDto.put("menuid", menuid);
			pDto.put("authorizeid", IDHelper.getAuthorizeid4Usermenumap(systemDao));
			pDto.put("authorizelevel", CloudConstants.AUTHORIZELEVEL_ACCESS);
			userDao.save("User.saveSelectedMenu", pDto);
		}
		return null;
	}
	
	/**
	 * 修改用户(提供首页修改使用)
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto updateUserItem4IndexPage(Dto pDto){
		String password = pDto.getAsString("password");
		String mPasswor = CloudUtils.encryptBasedMd5(password);
		pDto.put("password", mPasswor);
		pDto.put("updatemode", "notnull");
		userDao.update("User.updateUserItem", pDto);
		return null;
	}
	/**
	 * 查询用户，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryUsers(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = userDao.queryForPage("User.queryUsersForManage", dto);
		Integer total = (Integer)userDao.queryForObject("User.queryUsersForManageForPageCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	/**
	 * 根据userId和password查询用户信息
	 * @param userId
	 * @param password
	 * @return
	 */
	public  UserInfo  getUserByUserIdAndPassword (String userId,String password){
		return null;
	}

}
