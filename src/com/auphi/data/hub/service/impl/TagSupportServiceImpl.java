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

import java.util.ArrayList;
import java.util.List;

import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.tag.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.BaseBusinessServiceImpl;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.service.TagSupportService;


/**
 * 针对标签使用的业务接口实现
 * 
 * @author zhangjiafeng
 *
 */
@Service("tagSupportService")
public class TagSupportServiceImpl extends BaseBusinessServiceImpl implements
		TagSupportService {
	
	@Autowired
	private SystemDao baseDao;

	/**
	 * 获取卡片
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto getCardList(Dto pDto) {
		Dto outDto = new BaseDto();
		List resultList = new ArrayList();
		String accountType = pDto.getAsString("accountType");
		if (!accountType.equalsIgnoreCase(CloudConstants.ACCOUNTTYPE_NORMAL)) {
			resultList = baseDao.queryForList("TagSupport.getCardListBasedSuperAndDeveloper", pDto);
			outDto.setDefaultAList(resultList);
			return outDto;
		}
		List cardListBasedRole = baseDao.queryForList("TagSupport.getCardList", pDto);
		List cardListBasedUser = baseDao.queryForList("TagSupport.getCardListBasedUser", pDto);
		if (CloudUtils.isEmpty(cardListBasedRole)) {
			resultList.addAll(cardListBasedUser);
		} else {
			resultList.addAll(cardListBasedRole);
			for (int i = 0; i < cardListBasedUser.size(); i++) {
				Menu menuVoBaseUser = (Menu) cardListBasedUser.get(i);
				boolean flag = true;
				for (int j = 0; j < cardListBasedRole.size(); j++) {
					Menu menuVoBaseRole = (Menu) cardListBasedRole.get(j);
					if (menuVoBaseUser.getMenuid().equals(menuVoBaseRole.getMenuid())) {
						flag = false;
					}
				}
				if (flag)
					resultList.add(menuVoBaseUser);
			}
		}
		outDto.setDefaultAList(resultList);
		return outDto;
	}

	/**
	 * 获取卡片子树
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto getCardTreeList(Dto pDto) {
		Dto outDto = new BaseDto();
		List resultList = new ArrayList();
		String accountType = pDto.getAsString("accountType");
		if (!accountType.equalsIgnoreCase(CloudConstants.ACCOUNTTYPE_NORMAL)) {
			resultList = baseDao.queryForList("TagSupport.getCardTreeListBasedSuperAndDeveloperMysql", pDto);
			outDto.setDefaultAList(resultList);
			return outDto;
		}
		List cardTreeListBasedRole = baseDao.queryForList("TagSupport.getCardTreeListMysql", pDto);
		List cardTreeListBasedUser  = baseDao.queryForList("TagSupport.getCardTreeListBasedUserMysql", pDto);
		if (CloudUtils.isEmpty(cardTreeListBasedRole)) {
			resultList.addAll(cardTreeListBasedUser);
		} else {
			resultList.addAll(cardTreeListBasedRole);
			for (int i = 0; i < cardTreeListBasedUser.size(); i++) {
				Menu menuVoBaseUser = (Menu) cardTreeListBasedUser.get(i);
				boolean flag = true;
				for (int j = 0; j < cardTreeListBasedRole.size(); j++) {
					Menu menuVoBaseRole = (Menu) cardTreeListBasedRole.get(j);
					if (menuVoBaseUser.getMenuid().equals(menuVoBaseRole.getMenuid())) {
						flag = false;
					}
				}
				if (flag)
					resultList.add(menuVoBaseUser);
			}
		}
		for (int i = 0; i < resultList.size(); i++) {
			Menu menu = (Menu) resultList.get(i);
			if (menu.getMenuid().equals(CloudConstants.ROORID_MENU)) {
				resultList.remove(i);
			}
		}
		outDto.setDefaultAList(resultList);
		return outDto;
	}

	/**
	 * 获取登录人员所属部门信息
	 * 
	 * @return
	 */
	public Dto getDepartmentInfo(Dto pDto) {
		Dto outDto = (BaseDto) baseDao.queryForObject("TagSupport.getDepartmentInfo", pDto);
		String deptname = ((BaseDto)baseDao.queryForObject("TagSupport.getDepartmentInfo", pDto)).getAsString("deptname");
		return outDto;
	}

	/**
	 * 获取登录人员附加信息
	 * 
	 * @param pDto
	 * @return
	 */
	public Dto getEauserSubInfo(Dto pDto) {
		return (BaseDto)baseDao.queryForObject("TagSupport.getEauserSubInfo", pDto);
	}
}
