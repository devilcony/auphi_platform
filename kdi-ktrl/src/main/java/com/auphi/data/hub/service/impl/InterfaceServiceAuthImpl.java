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
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.ServiceAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.service.InterfaceServiceAuth;

/**
 * 服务授权管理接口
 * 
 * @author yiyabo
 *
 */
@Service("interfaceServiceAuth")
public class InterfaceServiceAuthImpl implements InterfaceServiceAuth{

	@Autowired
    SystemDao systemDao;
	
	
	public PaginationSupport<Object> queryServiceAuths(Dto dto)
			throws SQLException {
		List<Object> items = systemDao.queryForPage("ServiceAuth.queryServiceAuthList", dto);
		Integer total = (Integer)systemDao.queryForObject("ServiceAuth.queryServiceAuthListCount",dto);
		PaginationSupport<Object> page = new PaginationSupport<Object>(items, total);
		return page;
	}

	public void saveServiceAuth(ServiceAuth serviceAuth) {
		systemDao.save("ServiceAuth.insertServiceAuth",serviceAuth);
	}

	public void updateServiceAuth(ServiceAuth serviceAuth) {
		systemDao.update("ServiceAuth.updateServiceAuth",serviceAuth);
	}
	
	public void deleteServiceAuth(String serviceAuthIds) {
		Dto dto = new BaseDto();
		dto.put("serviceAuthIds",serviceAuthIds);
		systemDao.delete("ServiceAuth.deleteServiceAuthByIds",dto);
	}

	
	public Dto getServiceAuth(Dto dto) {	
		return (Dto)systemDao.queryForObject("ServiceAuth.getServiceAuth",dto);
	}


}
