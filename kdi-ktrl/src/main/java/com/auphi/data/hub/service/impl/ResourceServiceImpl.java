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
import com.auphi.data.hub.dao.ResourceDao;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Menu;
import com.auphi.data.hub.service.ResourceService;


/**
 * 资源业务访问接口实现
 * 
 * @author zhangjiafeng
 *
 */
@Service("resourceService")
public class ResourceServiceImpl extends BaseBusinessServiceImpl implements
		ResourceService {
	@Autowired
	private ResourceDao resourceDao;
	@Autowired
	private SystemDao systemDao;
	
	
	/**
	 * 获取资源列表
	 */
	public List<Dto> getResourceList() {
		return resourceDao.queryResourceList();
	}
	
	/**
	 * 获取全局编码
	 * @return
	 */
	public List<Dto> getCodeViewList(){
		return resourceDao.queryCodeViewList();
	}

	
	/**
	 * 根据编号查询该编号对应的菜单
	 * @return
	 */
	public Dto queryEamenuByMenuID(String menuId){
		return (Dto)resourceDao.queryForObject("Resource.queryEamenuByMenuID", "01");
	}
	
	/**
	 * 查询菜单列表
	 * @param dto
	 * @return
	 */
	public List<Dto<String,Object>> queryMenuItemsByDto(Dto dto){
		return resourceDao.queryForList("Resource.queryMenuItemsByDto", dto);
	}
	
	
	/**
	 * 查询菜单列表，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryMenuItems(Dto dto){
		List<Dto<String,Object>> items = resourceDao.queryForList("Resource.queryMenuItemsForManage", dto);
		Integer total = (Integer) resourceDao.queryForObject("Resource.queryMenuItemsForManageForPageCount", dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	/**
	 * 保存
	 * @param menu
	 * @return
	 */
	public Dto saveMenuItem(Menu menu){
		String menuid = IdGenerator.getMenuIdGenerator(menu.getParentid(),systemDao);
		menu.setMenuid(menuid);
		menu.setLeaf(CloudConstants.LEAF_Y);
		String sortno = CloudUtils.isEmpty(menu.getSortno()) ? "0" : menu.getSortno();
		menu.setSortno(sortno);
		menu.setMenutype("0");
		System.out.println(JsonHelper.encodeObject2Json(menu));
		resourceDao.save("Resource.saveMenuItem", menu);
		Dto updateDto = new BaseDto();
		updateDto.put("menuid", menu.getParentid());
		updateDto.put("leaf", CloudConstants.LEAF_N);
		resourceDao.update("Resource.updateLeafFieldInEaMenu", updateDto);
		return null;
	}
	
	/**
	 * 修改
	 * @param menu
	 * @return
	 */
	public Dto updateMenuItem(Menu menu){
		if (CloudUtils.isEmpty(menu.getSortno())) {
			menu.setSortno("0");
		}
		if (menu.getParentid().equals(menu.getParentid_old())) {
			resourceDao.update("Resource.updateMenuItem", menu);
		} else {
			resourceDao.delete("Resource.deleteEamenuItem", menu);
			resourceDao.delete("Resource.deleteEarwauthorizeItem", menu);
			resourceDao.delete("Resource.deleteEausermenumapByMenuid", menu);
			saveMenuItem(menu);
			menu.setParentid(menu.getParentid_old());
			updateLeafOfDeletedParent(menu);
		}
		return null;
	}
	
	/**
	 * 调整被删除菜单的直系父级菜单的Leaf属性
	 * 
	 * @param pDto
	 */
	private void updateLeafOfDeletedParent(Menu menu) {
		menu.setMenuid(menu.getParentid());
		Integer countInteger = (Integer) resourceDao.queryForObject("Resource.prepareChangeLeafOfDeletedParent", menu);
		if (countInteger.intValue() == 0) {
			menu.setLeaf(CloudConstants.LEAF_Y);
		} else {
			menu.setLeaf(CloudConstants.LEAF_N);
		}
		resourceDao.update("Resource.updateLeafFieldInEaMenu", menu);
	}
	
	/**
	 * 删除菜单
	 * @param dto
	 * @return
	 */
	public Dto deleteMenuItem(Dto pDto){
		Dto dto = new BaseDto();
		Dto changeLeafDto = new BaseDto();
		if (pDto.getAsString("type").equals("1")) {
			String[] arrChecked = pDto.getAsString("strChecked").split(",");
			for (int i = 0; i < arrChecked.length; i++) {
				dto.put("menuid", arrChecked[i]);
				changeLeafDto.put("parentid", ((BaseDto) resourceDao.queryForObject("Resource.queryMenuItemsByDto", dto))
						.getAsString("parentid"));
				resourceDao.delete("Resource.deleteEamenuItem", dto);
				resourceDao.delete("Resource.deleteEarwauthorizeItem", dto);
				resourceDao.delete("Resource.deleteEausermenumapByMenuid", dto);
				updateLeafOfDeletedParent(changeLeafDto);
			}
		} else {
			dto.put("menuid", pDto.getAsString("menuid"));
			changeLeafDto.put("parentid", ((BaseDto) resourceDao.queryForObject("Resource.queryMenuItemsByDto", dto))
					.getAsString("parentid"));
			resourceDao.delete("Resource.deleteEamenuItem", dto);
			resourceDao.delete("Resource.deleteEarwauthorizeItem", dto);
			resourceDao.delete("Resource.deleteEausermenumapByMenuid", dto);
			updateLeafOfDeletedParent(changeLeafDto);
		}
		return null;
	}
	
	private void updateLeafOfDeletedParent(Dto pDto) {
		String parentid = pDto.getAsString("parentid");
		pDto.put("menuid", parentid);
		Integer countInteger = (Integer) resourceDao.queryForObject("Resource.prepareChangeLeafOfDeletedParent", pDto);
		if (countInteger.intValue() == 0) {
			pDto.put("leaf", CloudConstants.LEAF_Y);
		} else {
			pDto.put("leaf", CloudConstants.LEAF_N);
		}
		resourceDao.update("Resource.updateLeafFieldInEaMenu", pDto);
	}
}
