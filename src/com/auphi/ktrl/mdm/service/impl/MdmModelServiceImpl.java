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

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.ktrl.mdm.domain.MdmModel;
import com.auphi.ktrl.mdm.service.MdmModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Service("MdmModelService")
public class MdmModelServiceImpl implements MdmModelService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<MdmModel> query(Dto dto) throws SQLException {
		List<MdmModel> items = systemDao.queryForPage("MdmModel.query", dto);
		Integer total = (Integer)systemDao.queryForObject("MdmModel.queryCount",dto);
		PaginationSupport<MdmModel> page = new PaginationSupport<MdmModel>(items, total);
		return page;
	}

	public List<MdmModel> query4ComboBox(Dto dto) throws SQLException {
		List<MdmModel> items = systemDao.queryForList("MdmModel.query4ComboBox", dto);
		return items;
	}
	
	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("MdmModel.queryMaxId",dto);
	}
	
	public void save(MdmModel Object) {

		Integer maxId = queryMaxId(null);
		if(maxId == null){
			Object.setId_model(1);
		}else{
			Object.setId_model(maxId+1);
		}
		this.systemDao.save("MdmModel.insert",Object);
	}

	public void update(MdmModel Object) {
		this.systemDao.update("MdmModel.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("MdmModel.delete", Object);
	}

	@Override
	public Integer queryCheckModelCode(Dto<String, Object> dto) throws SQLException {
		Integer count = (Integer) systemDao.queryForObject("MdmModel.queryCheckModelCode", dto);

		return count;
	}

	@Override
	public List<Map<String,Object>> queryExportList(Dto dto) {
		List<Map<String,Object>> items = systemDao.queryForList("MdmModel.queryExportList", dto);
		return items;
	}


	public MdmModel queryById(Dto dto) {
		// TODO Auto-generated method stub
		return (MdmModel)this.systemDao.queryForObject("MdmModel.queryById",dto);
	}
	
}
