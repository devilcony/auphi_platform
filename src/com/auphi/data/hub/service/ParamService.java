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

import com.auphi.data.hub.core.BaseBusinessService;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.Param;

/**
 * 全局参数业务接口
 * 
 * @author zhangjiafeng
 *
 */
public interface ParamService extends BaseBusinessService {
	/**
	 * 保存参数信息表
	 * @param param
	 */
	public Dto saveParamItem(Param param);

	/**
	 * 删除参数信息
	 * 
	 * @param pDto
	 */
	public Dto deleteParamItem(Dto pDto);

	/**
	 * 修改参数信息
	 * 
	 * @param param
	 */
	public Dto updateParamItem(Param param);
	
	/**
	 * 查询全局参数列表，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryParamsForManage(Dto dto)throws SQLException;
}
