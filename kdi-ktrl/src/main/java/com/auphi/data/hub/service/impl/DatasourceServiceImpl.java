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
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("datasourceService")
public class DatasourceServiceImpl implements DatasourceService {

	@Autowired
    SystemDao systemDao;
	
	public PaginationSupport<Datasource> queryDatasourceList(Dto dto) throws SQLException {
		List<Datasource> items = systemDao.queryForPage("Database.queryDatabaseListF", dto);
		Integer total = (Integer)systemDao.queryForObject("Database.queryDatabaseListCount",dto);
		PaginationSupport<Datasource> page = new PaginationSupport<Datasource>(items, total);
		return page;
	}

	public void saveDatasource(Datasource source) {
		this.systemDao.save("Database.insertDatabase",source);
	}

	public void updateDatasource(Datasource source) {
		this.systemDao.update("Database.updateDatabase",source);
	}

	public List<Datasource> querySourceList(){
		return this.systemDao.queryForList("Database.queryDatabaseList");
	}

	
	public void deleteDatasource(Map<String, Object> params) {
		this.systemDao.delete("Database.deleteDatabaseByIds", params);
	}
	
}
