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
import com.auphi.data.hub.core.struct.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Repository;
import com.auphi.data.hub.service.RepositoryService;


@Service("repositoryService")
public class RepositoryServiceImpl implements RepositoryService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<Repository> queryRepositoryList(Dto dto) throws SQLException {
		List<Repository> items = systemDao.queryForPage("Database.queryDatabaseList", dto);
		Integer total = (Integer)systemDao.queryForObject("Database.queryDatabaseListCount",dto);
		PaginationSupport<Repository> page = new PaginationSupport<Repository>(items, total);
		return page;
	}

	public void saveRepository(Repository repo) {
		this.systemDao.save("Database.insertDatabase",repo);
	}

	public void updateRepository(Repository repo) {
		this.systemDao.update("Database.updateDatabase",repo);
	}

	public List<Repository> queryRepositoryList(){
		return this.systemDao.queryForList("Database.queryDatabaseList");
	}

	
	public void deleteRepository(String repositoryID) {
		Dto dto = new BaseDto();
		dto.put("repositoryID",repositoryID);
		this.systemDao.delete("Database.deleteDatabaseByIds", dto);
	}
}
