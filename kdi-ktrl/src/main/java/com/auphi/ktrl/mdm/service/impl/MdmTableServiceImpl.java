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
package com.auphi.ktrl.mdm.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;

import com.auphi.ktrl.mdm.domain.MdmTable;
import com.auphi.ktrl.mdm.service.MdmTableService;


@Service("MdmTableService")
public class MdmTableServiceImpl implements MdmTableService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<MdmTable> query(Dto dto) throws SQLException {
		List<MdmTable> items = systemDao.queryForPage("MdmTable.query", dto);
		Integer total = (Integer)systemDao.queryForObject("MdmTable.queryCount",dto);
		PaginationSupport<MdmTable> page = new PaginationSupport<MdmTable>(items, total);
		return page;
	}

	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("MdmTable.queryMaxId",dto);
	}
	
	public void save(MdmTable Object) {
		this.systemDao.save("MdmTable.insert",Object);
	}

	public void update(MdmTable Object) {
		this.systemDao.update("MdmTable.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("MdmTable.delete", Object);
	}

	@Override
	public List<MdmTable> queryByMdmModel(Dto<String, Object> dto) {
		return this.systemDao.queryForList("MdmTable.queryByMdmModel", dto);
	}

	@Override
	public MdmTable queryById(Dto<String, Object> dto) {
		
		return (MdmTable) this.systemDao.queryForObject("MdmTable.queryById", dto);
	}
	
}
