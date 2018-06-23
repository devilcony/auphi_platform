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
package com.auphi.data.hub.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Organizer;
import com.auphi.data.hub.service.OrganizerService;
import com.auphi.ktrl.system.organizer.util.OrganizerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@ApiIgnore
@Controller("organizerMrg")
public class OrganizerMrgController extends BaseMultiActionController {

	private final static String INDEX = "admin/organizerMrg";
	
	@Autowired
	private OrganizerService organizerService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			PaginationSupport<Organizer> page = organizerService.query(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp, Organizer organizer) throws IOException{
		try{
			organizer.setOrganizer_status(0);
			this.organizerService.save(organizer);
			OrganizerUtil.registOrganizerUser(organizer);
			this.setOkTipMsg("添加成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp, Organizer organizer) throws IOException{	
		try{
			this.organizerService.update(organizer);
			this.setOkTipMsg("编辑成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("编辑失败", resp);
		}
		return null;
	}
	
	public ModelAndView activation(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			dto.put("organizer_status",1);
			this.organizerService.active(dto);
			this.setOkTipMsg("激活成功", resp);
		}catch(Exception e){
			e.printStackTrace();
			this.setOkTipMsg("激活失败", resp);
		}
		return null;
	}

	public ModelAndView stop(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			dto.put("organizer_status",2);
			this.organizerService.active(dto);
			this.setOkTipMsg("停用成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("停用失败", resp);
		}
		return null;
	}
	
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			dto.put("organizer_status",2);
			this.organizerService.active(dto);
			this.organizerService.delete(dto);
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}
}
