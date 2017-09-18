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
import com.auphi.data.hub.service.TransformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.dao.SystemDao;


/**
 * @author anx
 *
 */
@Service("TransformationService")
public class TransformationServiceImpl extends BaseBusinessServiceImpl implements TransformationService {
	
	@Autowired
	SystemDao systemDao;

	public void saveTransformation(Dto Transformation) {
		systemDao.save("Transformation.insertTransformation",Transformation);
	}

	public void deleteTransformation(Dto pDto) {
		systemDao.update("Transformation.deleteTransformation",pDto);
	}

	public void updateTransformation(Dto Transformation) {
		systemDao.update("Transformation.updateTransformation",Transformation);
	}

	public PaginationSupport<Dto<String, Object>> queryTransformation(Dto dto)
			throws SQLException {
		List<Dto<String,Object>> items = systemDao.queryForPage("Transformation.queryTransformation", dto);
		Integer total = (Integer)systemDao.queryForObject("Transformation.queryTransformationCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);

		return page;
	}

	

	

}
