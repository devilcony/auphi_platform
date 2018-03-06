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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.service.DataExportService;
import com.auphi.data.hub.service.DataImportService;

/**
 * 数据集市Oracle数据导入
 * 
 * @author anx
 *
 */
@Controller("dataImport")
public class DataImportController extends BaseMultiActionController {

	private final static String INDEX = "admin/dataImport";
	
	@Autowired
	private DataImportService importService;
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	public ModelAndView list(HttpServletRequest req,HttpServletResponse resp){
		Dto<String,Object> dto = new BaseDto();
		try {
			this.setPageParam(dto, req);
			PaginationSupport<Dto<String, Object>> page = importService.queryImportTask(dto);
			String jsonString = JsonHelper.encodeObject2Json(page,"yyyy-MM-dd HH:mm:ss");	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp) throws IOException {
		String jsonStr = req.getParameter("datas");
		
		if(jsonStr !=null){
			Map<String, Object> params = new HashMap<String, Object>();
			params = this.parseJSON2Map(jsonStr);
			try{
				importService.saveImportTask(params);
				setOkTipMsg("创建表成功", resp);
			} catch(Exception e){
				e.printStackTrace();
				setFailTipMsg("创建表失败", resp);
			}
		}
		
		
		return null;
	}
	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Map<String, Object> params = WebUtils.getParametersStartingWith(req, "p_");
		importService.updateImportTask(params);
		setOkTipMsg("修改数据导出任务配置成功", resp);
		return null;
	}
	
	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		String strChecked = req.getParameter("strChecked");
		Dto inDto = new BaseDto();
		inDto.put("strChecked", strChecked);
		importService.deleteImportTask(inDto);
		setOkTipMsg("数据导出任务配置删除成功", resp);
		return null;
	}
	
	public Map<String, Object> parseJSON2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k); 
            //如果内层还是数组的话，继续解析
            if(v instanceof JSONArray){
                List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
                Iterator<JSONObject> it = ((JSONArray)v).iterator();
                while(it.hasNext()){
                    JSONObject json2 = it.next();
                    list.add(parseJSON2Map(json2.toString()));
                }
                map.put(k.toString(), list);
            } else {
                map.put(k.toString(), v);
            }
        }
        return map;
    }

}
