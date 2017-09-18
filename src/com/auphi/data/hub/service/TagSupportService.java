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

import com.auphi.data.hub.core.BaseBusinessService;
import com.auphi.data.hub.core.struct.Dto;


/**
 * 提供标签使用的业务访问接口
 * 
 * @author zhangjiafeng
 *
 */
public interface TagSupportService extends BaseBusinessService {
		/**
		 * 获取卡片
		 * @param pDto
		 * @return
		 */
		public Dto getCardList(Dto pDto);
		
		/**
		 * 获取卡片子树
		 * @param pDto
		 * @return
		 */
		public Dto getCardTreeList(Dto pDto);
		
		/**
		 * 获取登录人员所属部门信息
		 * @return
		 */
		public Dto getDepartmentInfo(Dto pDto);
		
		/**
		 * 获取登录人员附加信息
		 * @param pDto
		 * @return
		 */
		public Dto getEauserSubInfo(Dto pDto);
		
		/**
		 * 根据路径获取菜单名称
		 * @param path
		 * @return
		 */
		//public String getMenuNameForCNPath(String path);
}
