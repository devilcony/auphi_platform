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
package com.auphi.data.hub.core.idgenerator;


import com.auphi.data.hub.core.BaseDao;
import com.auphi.data.hub.core.idgenerator.id.SequenceStorer;
import com.auphi.data.hub.core.struct.Dto;
import org.springframework.beans.factory.annotation.Autowired;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.dao.SystemDao;

/**
 * ID数据库逻辑存储器
 * @author zhangjiafeng
 * @since 2010-03-21
 */
public class DBSequenceStorer implements SequenceStorer {
	
	@Autowired
	private BaseDao baseDao;
	
	/**
	 * 返回当前最大序列号
	 */
	public long load(String pIdColumnName,SystemDao baseDao) throws GeneratorSequenceException {
		Dto dto = new BaseDto();
		dto.put("fieldname", pIdColumnName);
		dto = (BaseDto)baseDao.queryForObject("IdGenerator.getEaSequenceByFieldName", dto);
		Long maxvalue = dto.getAsLong("maxid");
		return maxvalue.longValue();
	}
	
	/**
	 * 写入当前生成的最大序列号值
	 */
	public void  updateMaxValueByFieldName(long pMaxId, String pIdColumnName,SystemDao baseDao) throws GeneratorSequenceException {
		Dto dto = new BaseDto();
		dto.put("maxid", String.valueOf(pMaxId));
		dto.put("fieldname", pIdColumnName);
		baseDao.update("IdGenerator.updateMaxValueByFieldName", dto);
	}
}
