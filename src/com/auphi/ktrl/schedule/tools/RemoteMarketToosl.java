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
package com.auphi.ktrl.schedule.tools;

import java.io.IOException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;

import com.alibaba.fastjson.JSONObject;
/**
 * 刘林勤 2014-10-16
 * */
@SuppressWarnings({ "deprecation" })
public class RemoteMarketToosl {
	
	public static String remoteMarketJson(String url,JSONObject jsonParam)
	{
		//System.out.println("========1");
		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		String server_path = pHelper.getValue("SERVER_PATH")+url; 	
		String json = "";
		//CloseableHttpClient httpClient = HttpClients.createDefault();
		DefaultHttpClient httpClient = new DefaultHttpClient();		
		// HTTP请求
		HttpPost httpPost = new HttpPost(server_path);
	    HttpResponse response =null;
		try {
			if (jsonParam!=null) {
		        StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题    
		        entity.setContentEncoding("UTF-8");
		        
		        entity.setContentType("application/json"); 
		        httpPost.setEntity(entity);			
	            //JSONObject resJson = json.parseObject(resData);
			}
			response = httpClient.execute(httpPost);	
			if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {// 请求成功  31. //取得请求内容
				// 从response中取出HttpEntity对象
				HttpEntity httpEntity = response.getEntity();
	            json=EntityUtils.toString(httpEntity,"UTF-8");

	            
			}else {
				return "remote Market Json error";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return json;
	//return null;
	}
   public static void main(String[] args) {
	 
       String json =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTable", null);	
       System.out.println(json);
	
}
}
