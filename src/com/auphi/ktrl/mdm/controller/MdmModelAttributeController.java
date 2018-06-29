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
package com.auphi.ktrl.mdm.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.FieldType;
import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.mdm.service.MdmModelAttributeService;
import com.auphi.ktrl.mdm.service.MdmModelConstaintService;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@ApiIgnore
@Controller("mdmModelAttribute")
public class MdmModelAttributeController extends BaseMultiActionController {

	private final static String INDEX = "admin/mdmModelAttribute";
	
	@Autowired
	private MdmModelAttributeService mdmModelAttributeService;
	
	@Autowired
	private MdmModelConstaintService mdmModelConstaintService;
	
	private Map<Integer,String> fieldTypeMap = new HashMap<Integer,String>(); 
	private List<FieldType> fieldTypelist = new ArrayList<FieldType>();
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	public ModelAndView getFieldType(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		try {
			getFieldType();
			String jsonString = JsonHelper.encodeObject2Json(fieldTypelist);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ModelAndView query4ComboBox(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			String id_model = req.getParameter("id_model");
			dto.put("id_model", id_model);
			List<MdmModelAttribute> list = mdmModelAttributeService.query4ComboBox(dto);
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			
			String id_model = req.getParameter("id_model");
			if(id_model == null || "".equals(id_model)) id_model = "0";
			dto.put("id_model", id_model);
			PaginationSupport<MdmModelAttribute> page = mdmModelAttributeService.query(dto);
			getFieldType();
			List<MdmModelAttribute> list = page.getRows();
			List<MdmModelAttribute> copylist = new CopyOnWriteArrayList<MdmModelAttribute>(list);
			Iterator<MdmModelAttribute> it = copylist.iterator();
			 while (it.hasNext()) {
				 MdmModelAttribute newbean = it.next();
				 newbean.setField_type_show(fieldTypeMap.get(newbean.getField_type()));
				String is_primary_show = (newbean.getIs_primary() != null && newbean.getIs_primary().equals("Y")) ? "是" : "否";
				newbean.setIs_primary_show(is_primary_show);
			 }
			 
			page.setRows(copylist);
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,MdmModelAttribute mdmModelAttribute) throws IOException{	
		try{
			Integer id = this.mdmModelAttributeService.queryMaxId(null);
	        if(id == null) id = 1;
	        else id = id+1;
	        mdmModelAttribute.setId_attribute(id);
			this.mdmModelAttributeService.save(mdmModelAttribute);
			this.setOkTipMsg("添加成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,MdmModelAttribute mdmModelAttribute) throws IOException{	
		try{
			this.mdmModelAttributeService.update(mdmModelAttribute);
			this.setOkTipMsg("编辑成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg(e.getMessage(), resp);
		}
		return null;
	}
	
	

	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			this.mdmModelAttributeService.delete(dto);
			mdmModelConstaintService.deleteByIdAttribute(dto);
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}
	
	private List<FieldType> getFieldType() {
		
		if(fieldTypelist == null || fieldTypelist.size() == 0 || fieldTypeMap == null || fieldTypeMap.size() == 0){
			String[] typeCodes = ValueMetaInterface.typeCodes;
			for(String str : typeCodes){
				int valtype = ValueMeta.getType(str);
				FieldType fieldType = new FieldType();
				fieldType.setFieldTypeShow(str);
				fieldType.setFieldTypeValue(valtype);
				fieldTypelist.add(fieldType);
				fieldTypeMap.put(valtype, str);
			}
		}
		return fieldTypelist;
	}
}
