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



import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessService;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;


/**
 * 用户业务接口 
 * 
 * @author skyform
 *
 */
@Service("importService")
public class DataImportService {

	@Autowired
	private SystemDao systemDao;
	
	/**
	 * 查询公司，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryImportTask(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = systemDao.queryForPage("Import.queryImportTask", dto);
		Integer total = (Integer)systemDao.queryForObject("Import.queryImportTaskCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	public void saveImportTask(Map<String,Object> params){
		systemDao.save("Import.insertImportTask",params);
	}
	
	public void updateImportTask(Map<String,Object> params){
		systemDao.update("Import.updateImportTask",params);
	}
	
	public void deleteImportTask(Map<String,Object> params){
		systemDao.delete("Import.deleteImportTask",params);
	}
	
	
}
