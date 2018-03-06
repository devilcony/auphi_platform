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
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.Department;


/**
 * 组织机构业务模型接口
 * @author zhangjiafeng
 *
 */
public interface OrganizationService extends BaseBusinessService{

	/**
	 * 获取用户信息
	 * @param pDto
	 * @return
	 */
	public Dto getUserInfo(Dto pDto);
	
	/**
	 * 查询部门信息生成部门树
	 * @param pDto
	 * @return
	 */
	public Dto queryDeptItems(Dto pDto);
	
	/**
	 * 保存部门
	 * @param pDto
	 * @return
	 */
	public Dto saveDeptItem(Department pDto);
	
	/**
	 * 修改部门
	 * @param pDto
	 * @return
	 */
	public Dto updateDeptItem(Department dept);
	
	/**
	 * 删除部门
	 * @param pDto
	 * @return
	 */
	public Dto deleteDeptItems(Dto pDto);
	
	/**
	 * 根据用户所属部门编号查询部门对象<br>
	 * 用于构造组织机构树的根节点
	 * @param
	 * @return
	 */
	public Dto queryDeptinfoByDeptid(Dto pDto);
	
	/**
	 * 保存用户主题信息
	 * @param pDto
	 */
	public Dto saveUserTheme(Dto pDto);
	
	/**
	 * 获取所有的部门列表，支持分页
	 * @param dept
	 * @return
	 */
	public PaginationSupport<Dto> getDeptsForManage(Department dept);
	
	/**
	 * 根据部门编号查询部门信息
	 * @param deptId
	 * @return
	 */
	public Dto getDeptById(String deptId);
	
	
}
