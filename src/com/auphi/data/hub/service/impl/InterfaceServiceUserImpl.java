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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import com.auphi.data.hub.domain.ServiceUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.service.InterfaceServiceUser;

/**
 * 
 * 服务用户管理接口 
 * @author yiyabo
 *
 */
@Service("interfaceServiceUser")
public class InterfaceServiceUserImpl implements InterfaceServiceUser,
		Serializable {
   
	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<Object> queryServiceUsers(Dto dto)
			throws SQLException {
		List<Object> items = systemDao.queryForPage("ServiceUser.queryServiceUserList", dto);
		Integer total = (Integer)systemDao.queryForObject("ServiceUser.queryServiceUserListCount",dto);
		PaginationSupport<Object> page = new PaginationSupport<Object>(items, total);
		return page;
	}

	@Override
	public void saveServiceUser(ServiceUser serviceUser) {
		systemDao.save("ServiceUser.insertServiceUser",serviceUser);
	}

	@Override
	public void updateServiceUser(ServiceUser serviceUser) {
	   systemDao.update("ServiceUser.updateServiceUser",serviceUser);
	}
	
	public void deleteServiceUser(String serviceUserIds){
		Dto dto = new BaseDto();
		dto.put("serviceUserIds",serviceUserIds);
		systemDao.delete("ServiceUser.deleteServiceUserByIds",dto);
	}

	@Override
	public Dto queryServiceUser(Dto dto){
		return (Dto)systemDao.queryForObject("ServiceUser.queryServiceUser",dto);
	}

	//获得所有服务用户列表
	public List<Dto> getAllServiceUser(){
		return systemDao.queryForList("ServiceUser.getAllServiceUser");
	}

}
