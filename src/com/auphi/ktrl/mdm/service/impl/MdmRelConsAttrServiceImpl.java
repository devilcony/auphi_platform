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
package com.auphi.ktrl.mdm.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.dao.SystemDao;

import com.auphi.ktrl.mdm.domain.MdmRelConsAttr;
import com.auphi.ktrl.mdm.service.MdmRelConsAttrService;


@Service("MdmRelConsAttrService")
public class MdmRelConsAttrServiceImpl implements MdmRelConsAttrService {

	@Autowired
	SystemDao systemDao;
	

	
	public void save(MdmRelConsAttr Object) {
		this.systemDao.save("MdmRelConsAttr.insert",Object);
	}

	public void update(MdmRelConsAttr Object) {
		this.systemDao.update("MdmRelConsAttr.update",Object);
	}

	
	public void delete(Map<String, Object> Object) {
		this.systemDao.delete("MdmRelConsAttr.delete", Object);
	}
	
	public void deletebyIdConstaint(Map<String, Object> Object) {
		this.systemDao.delete("MdmRelConsAttr.deletebyIdConstaint", Object);
	}
	
}
