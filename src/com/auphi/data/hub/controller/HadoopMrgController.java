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
import com.auphi.data.hub.core.util.HadoopUtil;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Hadoop;
import com.auphi.data.hub.service.HadoopMrgService;
import com.auphi.ktrl.system.user.bean.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@ApiIgnore
@Controller("hadoopMrg")
public class HadoopMrgController extends BaseMultiActionController {

	private final static String INDEX = "admin/hadoopMrg";
	
	@Autowired
	private HadoopMrgService hadoopMrgService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
//			String queryHadoopName = req.getParameter("queryHadoopName");
//			dto.put("queryHadoopName", queryHadoopName);
			UserBean userBean = req.getSession().getAttribute("userBean")==null?null:(UserBean)req.getSession().getAttribute("userBean");
			this.setPageParam(dto, req);
			PaginationSupport<Hadoop> page = hadoopMrgService.query(dto, userBean);
			String jsonString = JsonHelper.encodeObject2Json(page);
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,Hadoop hadoop) throws IOException{
		try{
			UserBean userBean = req.getSession().getAttribute("userBean")==null?null:(UserBean)req.getSession().getAttribute("userBean");
			Integer id = this.hadoopMrgService.queryMaxId(null);
	        if(id == null) id = 1;
	        else id = id+1;
	        hadoop.setId(id);
	        hadoop.setOrganizer_id(userBean.getOrgId());
			this.hadoopMrgService.save(hadoop);
			this.setOkTipMsg("添加成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,Hadoop hadoop) throws IOException{	
		try{
			this.hadoopMrgService.update(hadoop);
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
			this.hadoopMrgService.delete(dto);
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}
	
	public ModelAndView test(HttpServletRequest req,HttpServletResponse resp,Hadoop hadoop) throws IOException{	
		try{
			boolean isconn = HadoopUtil.getInstence().test(hadoop.getServer(),hadoop.getPort(),hadoop.getUserid(),hadoop.getPassword());
			if(isconn)	this.setOkTipMsg("连接成功", resp);
			else this.setFailTipMsg("连接失败", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("连接失败", resp);
		}
		return null;
	}
	
}
