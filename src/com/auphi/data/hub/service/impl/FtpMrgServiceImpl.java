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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.ktrl.system.user.bean.UserBean;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.FTP;
import com.auphi.data.hub.service.FtpMrgService;


@Service("FTPMrgService")
public class FtpMrgServiceImpl implements FtpMrgService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<FTP> query(Dto dto, UserBean userBean) throws SQLException {
		List<FTP> items = new ArrayList<FTP>();
		Integer total = 0;
		
		boolean isSuperAdmin = userBean.isSuperAdmin();
		if(isSuperAdmin){
			items = systemDao.queryForPage("FTP.queryAll", dto);
			total = (Integer)systemDao.queryForObject("FTP.queryCountAll",dto);
		}else {
			dto.put("organizer_id", userBean.getOrgId());
			items = systemDao.queryForPage("FTP.query", dto);
			total = (Integer)systemDao.queryForObject("FTP.queryCount",dto);
		}
		PaginationSupport<FTP> page = new PaginationSupport<FTP>(items, total);
		return page;
	}

	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("FTP.queryMaxId",dto);
	}
	
	public void save(FTP Object) {
		this.systemDao.save("FTP.insert",Object);
	}

	public void update(FTP Object) {
		this.systemDao.update("FTP.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("FTP.delete", Object);
	}
	
}
