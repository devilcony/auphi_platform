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
import java.util.Map;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.ktrl.system.organizer.util.OrganizerUtil;

import com.auphi.data.hub.domain.Organizer;


@Service("organizerService")
public class OrganizerServiceImpl implements OrganizerService {

	@Autowired
    SystemDao systemDao;
	
	public PaginationSupport<Organizer> query(Dto dto) throws SQLException {
		List<Organizer> items = systemDao.queryForPage("Organizer.query", dto);
		Integer total = (Integer)systemDao.queryForObject("Organizer.queryListCount",dto);
		PaginationSupport<Organizer> page = new PaginationSupport<Organizer>(items, total);
		return page;
	}

	public void save(Organizer source) {
		this.systemDao.save("Organizer.insert",source);
	}

	public void update(Organizer source) {
		this.systemDao.update("Organizer.update",source);
	}

	public List<Organizer> queryAll(){
		return this.systemDao.queryForList("Organizer.query");
	}

	
	public void delete(Map<String, Object> params) {
		this.systemDao.delete("Organizer.delete", params);
	}
	
	public void active(Map<String, Object> params) {
		OrganizerUtil.activeUser(String.valueOf(params.get("ids")), String.valueOf(params.get("organizer_status")));
		this.systemDao.update("Organizer.active", params);
	}
	
}
