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

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.export.HttpRest;
import com.auphi.data.hub.export.JdbcExport;
import com.auphi.data.hub.service.ExportOraTabService;

/**
 * 导出oracle下表结构管理控制器
 * 
 * @author anx
 *
 */
@Controller("exportOracleTable")
public class ExportOracleTableController extends BaseMultiActionController {
	
	private final static String INDEX = "admin/exportOracleTable";
	
	@Autowired
	private ExportOraTabService exportOraTabService;
	
	/**
	 * 跳转到首页
	 * @param req
	 * @param resp
	 * @return
	 */
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView exportTable(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		
		Map<String,Object> pram = new HashMap<String,Object>();
		pram.put("postType", "1");
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String server_path = pHelper.getValue("SERVER_PATH"); 
		HttpRest httpRest = new HttpRest();
		String str = httpRest.restPost(server_path+"/datahub/exportOracleTable/exportTableData.shtml", pram);
		System.out.println(str);
		setOkTipMsg("数据导出成功", resp);
		return null;
	}
	
	public void exportTableData(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		JdbcExport jets = new JdbcExport();
		jets.getTableData();
		return ;
	}
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			PaginationSupport<Dto<String, Object>> page = null;
			try {
				page = exportOraTabService.query(dto);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 保存表信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		String jsonStr = req.getParameter("jsonStr");
		
		if(jsonStr !=null){
			String [] ar = jsonStr.split("&");
			for(int i=0;i<ar.length;i++){
				Dto params = new BaseDto();
				params = JsonHelper.parseSingleJson2Dto(ar[i]);
				try{
					exportOraTabService.save(params);
					setOkTipMsg("保存表结构成功", resp);
				} catch(Exception e){
					e.printStackTrace();
					setFailTipMsg("保存表结构失败", resp);
				}
			}
			
		}
		
		return null;
	}

	
}
