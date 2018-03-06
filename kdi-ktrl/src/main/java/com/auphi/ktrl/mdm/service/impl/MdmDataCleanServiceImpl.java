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
package com.auphi.ktrl.mdm.service.impl;import java.sql.SQLException;import java.util.List;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import com.auphi.data.hub.core.PaginationSupport;import com.auphi.data.hub.core.struct.Dto;import com.auphi.data.hub.dao.SystemDao;import com.auphi.ktrl.mdm.domain.MdmDataClean;import com.auphi.ktrl.mdm.domain.MdmTable;import com.auphi.ktrl.mdm.service.MdmDataCleanService;/**    * This class is used for ...    * @author Tony  * @version    *       1.0, 2016-2-25 下午8:21:28    */@Service("MdmDataCleanService")public class MdmDataCleanServiceImpl implements MdmDataCleanService{	@Autowired	SystemDao systemDao;		@Override	public void save(MdmDataClean object)  throws SQLException{				this.systemDao.save("MdmDataClean.insert",object);	}	@Override	public Integer queryMaxId(Dto dto)  throws SQLException{		return (Integer)this.systemDao.queryForObject("MdmDataClean.queryMaxId",dto);	}	@Override	public PaginationSupport<MdmTable> query(Dto<String, Object> dto) throws SQLException {				List<MdmTable> items = systemDao.queryForPage("MdmDataClean.query", dto);		Integer total = (Integer)systemDao.queryForObject("MdmDataClean.queryCount",dto);		PaginationSupport<MdmTable> page = new PaginationSupport<MdmTable>(items, total);		return page;	}	@Override	public void delete(Dto dto) throws SQLException {		this.systemDao.delete("MdmDataClean.delete", dto);	}	@Override	public MdmDataClean queryById(Dto dto) throws SQLException {				return (MdmDataClean) systemDao.queryForObject("MdmDataClean.queryById", dto);	}	@Override	public void update(MdmDataClean mdmDataClean) throws SQLException {		this.systemDao.update("MdmDataClean.update",mdmDataClean);			}}