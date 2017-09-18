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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.export.JerseyClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.OracleDatasource;


/**
 * 数据源配置控制器
 * 
 * @author zhangfeng
 *
 */
@Controller("oracleDatasource")
public class OracleDatasourceController extends BaseMultiActionController {
	private static Log log = LogFactory.getLog(OracleDatasourceController.class);

	private final static String INDEX = "admin/oracleDatasource";
	
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	
	public ModelAndView saveSource(HttpServletRequest req,HttpServletResponse resp,OracleDatasource oracleDatasource) throws IOException{	
		try{
			PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
			String server_path = pHelper.getValue("SERVER_PATH"); 
			log.info("==request url="+server_path+"/datahub/rest/oracleConfig/properties");
			log.info("==request parameter="+JsonHelper.encodeObject2Json(oracleDatasource));
			JerseyClient.jerseyClient(server_path+"/datahub/rest/oracleConfig/properties",JsonHelper.encodeObject2Json(oracleDatasource));
			this.setOkTipMsg("数据集市参数,保存成功！", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加数据集市参数失败请检查后重新添加！", resp);
		}
		return null;
	}

	
	public ModelAndView getDbList(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		List<Dto> dbDataList = new ArrayList<Dto>();
		try{
			PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
			String server_path = pHelper.getValue("SERVER_PATH"); 
			String result=jerseyClient(server_path+"/datahub/rest/oracle/getConnectInfo",JsonHelper.encodeObject2Json("null"));
			log.info("result="+result);
			Dto outDto=JsonHelper.parseSingleJson2Dto(result);
			
			Dto<String,Object> dto1 = new BaseDto();
       		dto1.put("ssid",outDto.getAsString("SSID")==null?"":outDto.getAsString("SSID"));
			dto1.put("ip",outDto.getAsString("HOST")==null?"":outDto.getAsString("HOST"));
			dto1.put("port",outDto.getAsString("PORT")==null?"":outDto.getAsString("PORT"));
			dto1.put("userName",outDto.getAsString("USER_NAME")==null?"":outDto.getAsString("USER_NAME"));
			dto1.put("password",outDto.getAsString("PASSWORD")==null?"":outDto.getAsString("PASSWORD"));
			dbDataList.add(dto1);
			String jsonString = JsonHelper.encodeObject2Json(dbDataList);	
			write(jsonString, resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加数据集市参数失败请检查后重新添加！", resp);
		}
		return null;
	}
	
	
	
	public String jerseyClient(String url,Object obj){
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,Boolean.TRUE);
        Client client = Client.create(clientConfig);
        WebResource webResource = client.resource(url);
        ClientResponse response = webResource.accept("application/json").type("application/json").post(ClientResponse.class, obj);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " +  response.getStatus());
        }
        String output = response.getEntity(String.class);
        
        log.info("Server response .... \n");
        log.info(output);
        return output;
    }

	
	
	
	
	
	
}
