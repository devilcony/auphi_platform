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

import com.auphi.data.hub.core.struct.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.idgenerator.IDHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Param;
import com.auphi.data.hub.service.ParamService;

/**
 * 全局参数业务实现
 * 
 * @author zhangjiafeng
 *
 */
@Service("paramService")
public class ParamServiceImpl extends BaseBusinessServiceImpl implements
		ParamService {
	
	@Autowired
	private SystemDao systemDao;
	
	/**
	 * 保存参数信息表
	 */
	public Dto saveParamItem(Param param){
		param.setParamid(IDHelper.getParamID(systemDao));
		systemDao.save("Param.saveParamItem", param);
		return null;
	}

	/**
	 * 删除参数信息
	 * 
	 * @param pDto
	 */
	public Dto deleteParamItem(Dto pDto){
		Dto dto = new BaseDto();
		String[] arrChecked = pDto.getAsString("strChecked").split(",");
		for(int i = 0; i < arrChecked.length; i++){
			dto.put("paramid", arrChecked[i]);
			systemDao.delete("Param.deletParamItem", dto);
		}
		return null;
	}

	/**
	 * 修改参数信息
	 * 
	 * @param pDto
	 */
	public Dto updateParamItem(Param param){
		systemDao.update("Param.updateParamItem", param);
		return null;
	}
	/**
	 * 查询全局参数列表，支持分页
	 * @param dto
	 * @return
	 * @throws SQLException 
	 */
	public PaginationSupport<Dto<String,Object>> queryParamsForManage(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = systemDao.queryForPage("Param.queryParamsForManage", dto);
		Integer total = (Integer)systemDao.queryForObject("Param.queryParamsForManageForPageCount", dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
}
