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
import com.auphi.ktrl.mdm.domain.MdmModel;
import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.mdm.service.MdmModelAttributeService;
import com.auphi.ktrl.mdm.service.MdmModelConstaintService;
import com.auphi.ktrl.mdm.service.MdmModelService;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.StringUtil;
import com.auphi.ktrl.util.excel.ExportExcel;
import org.apache.poi.ss.usermodel.Row;
import org.pentaho.di.core.row.ValueMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller("mdmModel")
public class MdmModelController extends BaseMultiActionController {

	private final static String INDEX = "admin/mdmModel";
	
	@Autowired
	private MdmModelService mdmModelService;
	
	@Autowired
	private MdmModelAttributeService mdmModelAttributeService;
	
	@Autowired
	private MdmModelConstaintService mdmModelConstaintService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	public ModelAndView getAuthor(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		try {
			UserBean user = (UserBean)req.getSession().getAttribute("userBean");
			String jsonString = JsonHelper.encodeObject2Json(user);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ModelAndView query4ComboBox(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			List<MdmModel> list = mdmModelService.query4ComboBox(dto);
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	

	public ModelAndView checkModelCode(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		try{
			String model_code = req.getParameter("model_code");
			int id_model = ServletRequestUtils.getIntParameter(req,"id_model",-1);

			dto.put("model_code", model_code);
			dto.put("id_model", id_model);
			Integer list = mdmModelService.queryCheckModelCode(dto);
			if(list == null || list ==0 ){
				this.setOkTipMsg("failure", resp);
			}else{
				this.setFailTipMsg("failure", resp);
			}
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("failure", resp);
		}
		return null;
	}
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
//			String queryFTPName = req.getParameter("queryFTPName");
//			dto.put("queryFTPName", queryFTPName);
			this.setPageParam(dto, req);
			PaginationSupport<MdmModel> page = mdmModelService.query(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,MdmModel mdmModel) throws IOException{	
		try{
//			Integer id = this.mdmModelService.queryMaxId(null);
//	        if(id == null) id = 1;
//	        else id = id+1;
//	        mdmModel.setId_model(id);
			if(mdmModel.getId_model()==null || mdmModel.getId_model() == 0){
				this.mdmModelService.save(mdmModel);
			}else{
				this.mdmModelService.update(mdmModel);
			}

			this.setOkTipMsg("保存成功",mdmModel, resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,MdmModel mdmModel) throws IOException{	
		try{
			this.mdmModelService.update(mdmModel);
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
			this.mdmModelService.delete(dto);
			//获取要删除属性id
			String id_attributes = "";
			String[] id_modelArr = ids.split(",");
			for(String id_model : id_modelArr){
				Dto idModelDto = new BaseDto();
				idModelDto.put("id_model", id_model);
				List<MdmModelAttribute> list = mdmModelAttributeService.query4ComboBox(idModelDto);
				for(MdmModelAttribute bean:list){
					int id_attribute = bean.getId_attribute();
					id_attributes += id_attribute + ",";
				}
			}
			Dto idAttributeDto = new BaseDto();
			idAttributeDto.put("ids",(id_attributes.length() == 0) ? "0":id_attributes.substring(0, id_attributes.length()-1));
			this.mdmModelConstaintService.deleteByIdAttribute(idAttributeDto);
			this.mdmModelAttributeService.deleteByIdModel(dto);
			
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}

	public ModelAndView exportExcel(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids =  ServletRequestUtils.getStringParameter(req,"ids",null);
			Dto dto = new BaseDto();
			dto.put("ids",ids);

			List<Map<String,Object>> list = mdmModelService.queryExportList(dto);

			String fileName = "主数据模型"+ StringUtil.DateToString(new Date(),"yyyyMMddHHmmss")+".xlsx";

			String[] headerList = {"模型ID","模型名称","表名","字段描述","字段名称","字段类型","字段长度"};
			String[] keys = {"ID_MODEL","MODEL_NAME","TABLE_NAME","ATTRIBUTE_NAME","FIELD_NAME","FIELD_TYPE","FIELD_LENGTH"};
			ExportExcel ee = new ExportExcel(null, headerList);


			for (int i = 0; i < list.size(); i++) {
				Row row = ee.addRow();
				Map<String,Object> map = list.get(i);
				for (int j = 0; j < keys.length; j++) {
					if("FIELD_TYPE".equals(keys[j]) ){
						ee.addCell(row, j, ValueMeta.getTypeDesc((Integer) map.get(keys[j])));
					}else{
						ee.addCell(row, j, map.get(keys[j]));
					}
				}
			}
			ee.write(resp, fileName).dispose();

		}catch(Exception e){

		}
		return null;
	}
	
}
