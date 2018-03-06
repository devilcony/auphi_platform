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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.idgenerator.IdGenerator;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Department;
import com.auphi.data.hub.domain.UserInfo;
import com.auphi.data.hub.service.OrganizationService;

/**
 * 组织机构业务模型接口实现
 * 
 * @author zhangjiafeng
 *
 */
@Service("organizationService")
public class OrganizationServiceImpl extends BaseBusinessServiceImpl implements
		OrganizationService {
	
	@Autowired
	private SystemDao baseDao;

	/**
	 * 获取用户信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto getUserInfo(Dto pDto) {
		Dto outDto = new BaseDto();
		pDto.put("lock", CloudConstants.LOCK_N);
		pDto.put("enabled", CloudConstants.ENABLED_Y);
		UserInfo userInfo = (UserInfo) baseDao.queryForObject("Organization.getUserInfo", pDto);
		outDto.put("userInfo", userInfo);		
		return outDto;
	}

	/**
	 * 查询部门信息生成部门树
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto queryDeptItems(Dto pDto) {
		Dto outDto = new BaseDto();
		List deptList = baseDao.queryForList("Organization.queryDeptItemsByDto", pDto);
		Dto deptDto = new BaseDto();
		for (int i = 0; i < deptList.size(); i++) {
			deptDto = (BaseDto) deptList.get(i);
			if (deptDto.getAsString("leaf").equals(CloudConstants.LEAF_Y))
				deptDto.put("leaf", new Boolean(true));
			else
				deptDto.put("leaf", new Boolean(false));
			if (deptDto.getAsString("id").length() == 6)
				deptDto.put("expanded", new Boolean(true));
		}
		outDto.put("jsonString", JsonHelper.encodeObject2Json(deptList));
		return outDto;
	}

	/**
	 * 保存部门
	 * 
	 * @param dept
	 * @return
	 */
	public synchronized Dto saveDeptItem(Department dept) {
		String deptid = IdGenerator.getDeptIdGenerator(dept.getParentid(),baseDao);
		dept.setDeptid(deptid);

		dept.setLeaf(CloudConstants.LEAF_Y);
		// MYSQL下int类型字段不能插入空字符
		dept.setSortno(CloudUtils.isEmpty(dept.getSortno()) ? "0": dept.getSortno());
		dept.setEnabled(CloudConstants.ENABLED_Y);
		baseDao.save("Organization.saveDeptItem", dept);
		Dto updateDto = new BaseDto();
		updateDto.put("deptid", dept.getParentid());
		updateDto.put("leaf", CloudConstants.LEAF_N);
		baseDao.update("Organization.updateLeafFieldInEaDept", updateDto);
		return null;
	}

	/**
	 * 修改部门
	 * 
	 * @param dept
	 * @return
	 */
	public Dto updateDeptItem(Department dept) {
		if (CloudUtils.isEmpty(dept.getSortno())) {
			dept.setSortno("0");
		}
		if (dept.getParentid().equals(dept.getParentid_old())) {
			baseDao.update("Organization.updateDeptItem", dept);
		} else {
			baseDao.update("Organization.updateEadeptItem", dept);
			saveDeptItem(dept);
			dept.setParentid(dept.getParentid_old());
			updateLeafOfDeletedParent(dept);
		}
		return null;
	}

	/**
	 * 调整被删除部门的直系父级部门的Leaf属性
	 * 
	 * @param dept
	 */
	private void updateLeafOfDeletedParent(Department dept) {
		Integer countInteger = (Integer) baseDao.queryForObject("Organization.prepareChangeLeafOfDeletedParentForEadept", dept);
		if (countInteger.intValue() == 0) {
			dept.setLeaf(CloudConstants.LEAF_Y);
		} else {
			dept.setLeaf(CloudConstants.LEAF_N);
		}
		baseDao.update("Organization.updateLeafFieldInEaDept", dept);
	}

	/**
	 * 删除部门项
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto deleteDeptItems(Dto pDto) {
		Dto dto = new BaseDto();
		if (pDto.getAsString("type").equals("1")) {
			// 列表复选删除
			String[] arrChecked = pDto.getAsString("strChecked").split(",");
			for (int i = 0; i < arrChecked.length; i++) {
				dto.put("deptid", arrChecked[i]);
				deleteDept(dto);
			}
		} else {
			// 部门树右键删除
			dto.put("deptid", pDto.getAsString("deptid"));
			deleteDept(dto);
		}
		return null;
	}

	/**
	 * 删除部门 类内部调用
	 * 
	 * @param pDto
	 */
	private void deleteDept(Dto pDto) {
		Department dept = new Department();
		Dto tempDto = (BaseDto) baseDao.queryForObject("Organization.queryDeptItemsByDto", pDto);
		if (CloudUtils.isNotEmpty(tempDto)) {
			dept.setParentid(tempDto.getAsString("parentid"));
		}
		//删除角色授权表
		baseDao.delete("Organization.deleteEaroleAuthorizeInDeptManage", pDto);
		//删除角色表
		baseDao.delete("Organization.deleteEaroleInDeptManage", pDto);
		//删除人员授权表
		baseDao.delete("Organization.deleteEauserauthorizeInDeptManage", pDto);
		//删除人员授权表2
		baseDao.delete("Organization.deleteEauserauthorizeInDeptManage2", pDto);
		//删除人员菜单映射表 
		baseDao.delete("Organization.deleteEausermenumapInDeptManage", pDto);
		//删除人员附属信息表
		baseDao.delete("Organization.deleteEausersubinfoInDeptManage", pDto);
		//删除人员表
		baseDao.update("Organization.updateEauserInDeptManage", pDto);
		//删除部门
		baseDao.update("Organization.updateEadeptItem", pDto);
		if (CloudUtils.isNotEmpty(tempDto)) {
			updateLeafOfDeletedParent(dept);
		}
	}

	/**
	 * 根据用户所属部门编号查询部门对象<br>
	 * 用于构造组织机构树的根节点
	 * 
	 * @param
	 * @return
	 */
	public Dto queryDeptinfoByDeptid(Dto pDto) {
		Dto outDto = new BaseDto();
		outDto.putAll((BaseDto) baseDao.queryForObject("Organization.queryDeptinfoByDeptid", pDto));
		outDto.put("success", new Boolean(true));
		return outDto;
	}

	/**
	 * 保存用户主题信息
	 * 
	 * @param pDto
	 */
	public Dto saveUserTheme(Dto pDto) {
		Dto outDto = new BaseDto();
		baseDao.update("Organization.saveUserTheme", pDto);
		outDto.put("success", new Boolean(true));
		return outDto;
	}

	/**
	 * 获取所有的部门列表，支持分页
	 * @return
	 */
	public PaginationSupport<Dto> getDeptsForManage(Department dept){
		List<Dto> menuList = baseDao.queryForList("Organization.queryDeptsForManage", dept);
		Integer total = (Integer) baseDao.queryForObject("Organization.queryDeptsForManageForPageCount", dept);
		PaginationSupport<Dto> page = new PaginationSupport<Dto>(menuList,total);
		return page;
	}
	
	/**
	 * 根据部门编号查询部门信息
	 * @param deptId
	 * @return
	 */
	public Dto getDeptById(String deptId){
		return (Dto) baseDao.queryForObject("Organization.selectDeptById",deptId);
	}
	
}
