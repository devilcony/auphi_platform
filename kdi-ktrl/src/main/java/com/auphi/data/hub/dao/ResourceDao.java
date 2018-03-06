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
package com.auphi.data.hub.dao;

import java.util.List;

import com.auphi.data.hub.core.BaseDao;
import com.auphi.data.hub.core.struct.Dto;

/**
 * 资源数据访问接口
 * 
 * @author zhangjiafeng
 *
 */
public interface ResourceDao extends BaseDao {
	
	/**
	 * 查询资源配置信息
	 * @return
	 */
	public List<Dto> queryResourceList();
	
	/**
	 * 查询全局编码
	 * @return
	 */
	public List<Dto> queryCodeViewList();
	
}
