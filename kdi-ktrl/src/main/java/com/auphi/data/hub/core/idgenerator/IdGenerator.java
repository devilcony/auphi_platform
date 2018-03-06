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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.auphi.data.hub.core.idgenerator.id.DefaultSequenceFormater;
import com.auphi.data.hub.core.idgenerator.id.DefaultSequenceGenerator;
import com.auphi.data.hub.core.idgenerator.id.SequenceStorer;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.dao.SystemDao;

/**
 * ID生成器
 * @author zhangjiafeng
 */
public class IdGenerator {
	private static Log log = LogFactory.getLog(IdGenerator.class);
	private static int catche = 1;
	
	/**
	 * 字段名称
	 */
	private String fieldname;
	
	public IdGenerator(String pFieldName){
		setFieldname(pFieldName);
	}
	
	public IdGenerator(){
	}
	
	/**
	 * 获取ID生成器实例
	 * @return DefaultIDGenerator
	 */
	public DefaultIDGenerator getDefaultIDGenerator(SystemDao baseDao){
		Dto dto = new BaseDto();
		dto.put("fieldname", getFieldname());
		dto = (BaseDto)baseDao.queryForObject("IdGenerator.getEaSequenceByFieldName", dto);
		DefaultIDGenerator idGenerator = new DefaultIDGenerator(); 
		DefaultSequenceFormater sequenceFormater = new DefaultSequenceFormater(); 
		sequenceFormater.setPattern(dto.getAsString("pattern"));
		DefaultSequenceGenerator sequenceGenerator = new DefaultSequenceGenerator(getFieldname());
		SequenceStorer sequenceStorer = new DBSequenceStorer();
		sequenceGenerator.setSequenceStorer(sequenceStorer);
		sequenceGenerator.setCache(catche);
		idGenerator.setSequenceFormater(sequenceFormater);
		idGenerator.setSequenceGenerator(sequenceGenerator);
		return idGenerator;
	}
	
    /**
     * 菜单编号ID生成器(自定义)
     * @param pParentid 菜单编号的参考编号
     * @return
     */
	public static String getMenuIdGenerator(String pParentid,SystemDao systemDao){
		String maxSubMenuId = (String)systemDao.queryForObject("IdGenerator.getMaxSubMenuId", pParentid);
		String menuId = null;
		if(CloudUtils.isEmpty(maxSubMenuId)){
			menuId = "01";
		}else{
			int length = maxSubMenuId.length();
			String temp = maxSubMenuId.substring(length-2, length);
			int intMenuId = Integer.valueOf(temp).intValue() + 1;
			if(intMenuId > 0 && intMenuId < 10){
				menuId = "0" + String.valueOf(intMenuId);
			}else if(10 <= intMenuId && intMenuId <= 99){
				menuId = String.valueOf(intMenuId);
			}else if(intMenuId > 99){
				log.error(CloudConstants.Exception_Head + "生成菜单编号越界了.同级兄弟节点编号为[01-99]\n请和您的系统管理员联系!");
			}else{
				log.error(CloudConstants.Exception_Head + "生成菜单编号发生未知错误,请和开发人员联系!");
			}
		}
		return pParentid + menuId;
	}
	
    /**
     * 部门编号ID生成器(自定义)
     * @param pParentid 菜单编号的参考编号
     * @return
     */
	public static String getDeptIdGenerator(String pParentid,SystemDao systemDao){
		String maxSubDeptId = (String)systemDao.queryForObject("IdGenerator.getMaxSubDeptId", pParentid);
		String deptid = null;
		if(CloudUtils.isEmpty(maxSubDeptId)){
			deptid = "001";
		}else{
			int length = maxSubDeptId.length();
			String temp = maxSubDeptId.substring(length-3, length);
			int intDeptId = Integer.valueOf(temp).intValue() + 1;
			if(intDeptId > 0 && intDeptId < 10){
				deptid = "00" + String.valueOf(intDeptId);
			}else if(10 <= intDeptId && intDeptId <= 99){
				deptid = "0" + String.valueOf(intDeptId);
			}else if (100 <= intDeptId && intDeptId <= 999) {
				deptid = String.valueOf(intDeptId);
			}else if(intDeptId >999){
				log.error(CloudConstants.Exception_Head + "生成部门编号越界了.同级兄弟节点编号为[001-999]\n请和您的系统管理员联系!");
			}else{
				log.error(CloudConstants.Exception_Head + "生成部门编号发生未知错误,请和开发人员联系!");
			}
		}
		return pParentid + deptid;
	}
	
	public String getFieldname() {
		return fieldname;
	}
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
}
