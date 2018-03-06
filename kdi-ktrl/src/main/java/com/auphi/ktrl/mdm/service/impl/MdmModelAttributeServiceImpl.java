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

import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.mdm.service.MdmModelAttributeService;


@Service("MdmModelAttributeService")
public class MdmModelAttributeServiceImpl implements MdmModelAttributeService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<MdmModelAttribute> query(Dto dto) throws SQLException {
		List<MdmModelAttribute> items = systemDao.queryForPage("MdmModelAttribute.query", dto);
		Integer total = (Integer)systemDao.queryForObject("MdmModelAttribute.queryCount",dto);
		PaginationSupport<MdmModelAttribute> page = new PaginationSupport<MdmModelAttribute>(items, total);
		return page;
	}
	
	public List<MdmModelAttribute> query4ComboBox(Dto dto) throws SQLException {
		List<MdmModelAttribute> items = systemDao.queryForList("MdmModelAttribute.query4ComboBox", dto);
		return items;
	}

	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("MdmModelAttribute.queryMaxId",dto);
	}
	
	public void save(MdmModelAttribute Object) {
		this.systemDao.save("MdmModelAttribute.insert",Object);
	}

	public void update(MdmModelAttribute Object) {
		this.systemDao.update("MdmModelAttribute.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("MdmModelAttribute.delete", Object);
	}

	public void deleteByIdModel(Map<String, Object> Object) {
		this.systemDao.delete("MdmModelAttribute.deleteByIdModel", Object);
		
	}

	@Override
	public List<MdmModelAttribute> queryAll() throws SQLException {
		List<MdmModelAttribute> items = systemDao.queryForList("MdmModelAttribute.queryAll");
		return items;
	}
	
}
