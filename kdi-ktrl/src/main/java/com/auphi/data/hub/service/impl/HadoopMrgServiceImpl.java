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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.Hadoop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.ktrl.system.user.bean.UserBean;

import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.service.HadoopMrgService;


@Service("HadoopMrgService")
public class HadoopMrgServiceImpl implements HadoopMrgService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<Hadoop> query(Dto dto, UserBean userBean) throws SQLException {
		List<Hadoop> items = new ArrayList<Hadoop>();
		Integer total = 0;
		
		boolean isSuperAdmin = userBean.isSuperAdmin();
		if(isSuperAdmin){
			items = systemDao.queryForPage("Hadoop.queryAll", dto);
			total = (Integer)systemDao.queryForObject("Hadoop.queryCountAll",dto);
		}else {
			dto.put("organizer_id", userBean.getOrgId());
			items = systemDao.queryForPage("Hadoop.query", dto);
			total = (Integer)systemDao.queryForObject("Hadoop.queryCount",dto);
		}
		PaginationSupport<Hadoop> page = new PaginationSupport<Hadoop>(items, total);
		return page;
	}
	
	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("Hadoop.queryMaxId",dto);
	}
	
	public void save(Hadoop Object) {
		this.systemDao.save("Hadoop.insert",Object);
	}

	public void update(Hadoop Object) {
		this.systemDao.update("Hadoop.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("Hadoop.delete", Object);
	}
	
}
