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

import com.auphi.ktrl.mdm.domain.MdmModelConstaint;
import com.auphi.ktrl.mdm.service.MdmModelConstaintService;


@Service("MdmModelConstaintService")
public class MdmModelConstaintServiceImpl implements MdmModelConstaintService {

	@Autowired
	SystemDao systemDao;
	
	public PaginationSupport<MdmModelConstaint> query(Dto dto) throws SQLException {
		List<MdmModelConstaint> items = systemDao.queryForPage("MdmModelConstaint.query", dto);
		Integer total = (Integer)systemDao.queryForObject("MdmModelConstaint.queryCount",dto);
		PaginationSupport<MdmModelConstaint> page = new PaginationSupport<MdmModelConstaint>(items, total);
		return page;
	}

	public Integer queryMaxId(Dto dto) {
		return (Integer)this.systemDao.queryForObject("MdmModelConstaint.queryMaxId",dto);
	}
	
	public void save(MdmModelConstaint Object) {
		this.systemDao.save("MdmModelConstaint.insert",Object);
	}

	public void update(MdmModelConstaint Object) {
		this.systemDao.update("MdmModelConstaint.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("MdmModelConstaint.delete", Object);
	}

	public void deleteByIdAttribute(Map<String, Object> Object) {
		this.systemDao.delete("MdmModelConstaint.deleteByIdAttribute", Object);
	}
	
}
