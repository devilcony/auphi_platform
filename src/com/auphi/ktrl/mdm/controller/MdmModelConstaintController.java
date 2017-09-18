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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;

import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.mdm.domain.MdmModelConstaint;
import com.auphi.ktrl.mdm.domain.MdmRelConsAttr;
import com.auphi.ktrl.mdm.service.MdmModelAttributeService;
import com.auphi.ktrl.mdm.service.MdmModelConstaintService;
import com.auphi.ktrl.mdm.service.MdmRelConsAttrService;


@Controller("mdmModelConstaint")
public class MdmModelConstaintController extends BaseMultiActionController {

	private final static String INDEX = "admin/mdmModelConstaint";
	
	@Autowired
	private MdmModelConstaintService mdmModelConstaintService;
	
	@Autowired
	private MdmModelAttributeService mdmModelAttributeService;
	
	@Autowired
	private MdmRelConsAttrService mdmRelConsAttrService;
	
	private Map<Integer,String> attributeMap = new HashMap<Integer,String>(); 
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
//			String id_attribute = req.getParameter("id_attribute");
//			if(id_attribute == null || "".equals(id_attribute)) id_attribute = "0";
//			dto.put("id_attribute", id_attribute);
			this.setPageParam(dto, req);
			PaginationSupport<MdmModelConstaint> page = mdmModelConstaintService.query(dto);
			getAttributeMap();
			List<MdmModelConstaint> list = page.getRows();
			List<MdmModelConstaint> copylist = new CopyOnWriteArrayList<MdmModelConstaint>(list);
			Iterator<MdmModelConstaint> it = copylist.iterator();
			 while (it.hasNext()) {
				 MdmModelConstaint newbean = it.next();
				 if(newbean.getId_attributes() != null && !newbean.getId_attributes().isEmpty()){
					 String[] arr = newbean.getId_attributes().split(",");
					 String showStr = "";
					 for(String str : arr){
						 Integer key = Integer.valueOf(str);
						 String attributename = attributeMap.get(key);
						 showStr += attributename + ",";
					 }
					showStr = showStr.substring(0, showStr.length() - 1);
					newbean.setId_attributes_show(showStr);
				 }
			 }
			 
			page.setRows(copylist);
			
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,MdmModelConstaint mdmModelConstaint) throws IOException{	
		try{
			Integer id = this.mdmModelConstaintService.queryMaxId(null);
	        if(id == null) id = 1;
	        else id = id+1;
	        mdmModelConstaint.setId_constaint(id);
			this.mdmModelConstaintService.save(mdmModelConstaint);
			
			String id_attributes = mdmModelConstaint.getId_attributes();
			if(id_attributes != null && !id_attributes.isEmpty()){
				String[] id_attributesArr = id_attributes.split(",");
				for(int i=0;i<id_attributesArr.length;i++){
					Integer id_attribute = Integer.valueOf(id_attributesArr[i]);
					MdmRelConsAttr mdmRelConsAttr = new MdmRelConsAttr();
					mdmRelConsAttr.setId_constaint(id);
					mdmRelConsAttr.setId_attribute(id_attribute);
					this.mdmRelConsAttrService.save(mdmRelConsAttr);
				}
			}
			this.setOkTipMsg("添加成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,MdmModelConstaint mdmModelConstaint) throws IOException{	
		try{
			this.mdmModelConstaintService.update(mdmModelConstaint);
			Integer id = mdmModelConstaint.getId_constaint();
			
			String id_attributes = mdmModelConstaint.getId_attributes();
			if(id_attributes != null && !id_attributes.isEmpty()){
				Dto dto = new BaseDto();
				dto.put("ids",id);
				this.mdmRelConsAttrService.deletebyIdConstaint(dto);
				
				String[] id_attributesArr = id_attributes.split(",");
				for(int i=0;i<id_attributesArr.length;i++){
					Integer id_attribute = Integer.valueOf(id_attributesArr[i]);
					MdmRelConsAttr mdmRelConsAttr = new MdmRelConsAttr();
					mdmRelConsAttr.setId_constaint(id);
					mdmRelConsAttr.setId_attribute(id_attribute);
					this.mdmRelConsAttrService.save(mdmRelConsAttr);
				}
			}
			this.setOkTipMsg("编辑成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("编辑失败", resp);
		}
		return null;
	}
	
	

	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			this.mdmModelConstaintService.delete(dto);
			this.mdmRelConsAttrService.deletebyIdConstaint(dto);
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}
	
	
	private void getAttributeMap() {
		//if( attributeMap == null || attributeMap.size() == 0){
			try{
				List<MdmModelAttribute> MdmModelAttributeList =  mdmModelAttributeService.queryAll();
				for(MdmModelAttribute mdmModelAttribute : MdmModelAttributeList){
					Integer id_attribute = mdmModelAttribute.getId_attribute();
					attributeMap.put(id_attribute, mdmModelAttribute.getAttribute_name());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		//}
	}
}
