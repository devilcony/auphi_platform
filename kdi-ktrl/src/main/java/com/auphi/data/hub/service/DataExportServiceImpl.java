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
package com.auphi.data.hub.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.dao.SystemDao;

@Service("exportService")
public class DataExportServiceImpl implements DataExportService,Serializable {
	private static final long serialVersionUID = -1596352243268130649L;
	@Autowired
	private SystemDao systemDao;
	
	public PaginationSupport<Dto<String,Object>> queryExportTask(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = systemDao.queryForPage("Export.queryExportTask", dto);
		Integer total = (Integer)systemDao.queryForObject("Export.queryExportTaskCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	public void saveExportTask(Map<String,Object> params){
		systemDao.save("Export.insertExportTask",params);
	}
	
	public void updateExportTask(Map<String,Object> params){
		systemDao.update("Export.updateExportTask",params);
	}
	
	public void deleteExportTask(Map<String,Object> params){
		systemDao.delete("Export.deleteExportTask",params);
	}
	
	public Object getDataExportById(Map<String, Object> dto){
		Object dataExportInfo = systemDao.queryForObject("Export.queryDataExportInfo",dto);
		return dataExportInfo;
	}
	
	public List<Dto> getTable(){
		List<Dto> tableData = systemDao.queryForList("Export.queryTable");
		return tableData;
	}
	
	public List<Dto> getReturnField(Map<String, Object> dto){
		List<Dto> tableData = systemDao.queryForList("Export.queryReturnField", dto);
		return tableData;
	}
	
}
