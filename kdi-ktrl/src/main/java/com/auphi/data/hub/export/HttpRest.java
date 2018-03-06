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
package com.auphi.data.hub.export;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author anx
 * @version
 */
public class HttpRest {
	
	private Log log = LogFactory.getLog(HttpRest.class);
    /**
     * REST请求POST方法
     * @param strUrl 请求的资源地址
     * @param params 请求的POST数据
     * @return string
     * @throws IOException
     */
    public String restPost(String strUrl, Map params) {
        if("".equals(strUrl)) {
        	log.warn("rest 请求 url 地址为空或者错误");
//        	return "0";
        }
        String paramStr = "";
        URL url = null;
        HttpURLConnection urlconn = null;
        OutputStream outs = null;
        Writer writer = null;
		try {
			url = new URL(strUrl);
	        //实例一个http资源链接
			urlconn = (HttpURLConnection) url.openConnection();
			urlconn.setRequestMethod("POST");
//			urlconn.setConnectTimeout(1000 * 5);  
	        urlconn.setDoOutput(true);
	        urlconn.setDoInput(true);
	        urlconn.setUseCaches(false);
	        urlconn.setAllowUserInteraction(false);
	        urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        
	        //实例一个输出流对象
	        outs = urlconn.getOutputStream();
	        
	      //实例一个Writer，并初始化
	        writer = new OutputStreamWriter(outs, "UTF-8");
	        
	        if(params.size() > 0){
	            Iterator ups = params.entrySet().iterator();
	            while (ups.hasNext()){
	                Map.Entry upskv = (Map.Entry) ups.next();
	                try {
						paramStr += upskv.getKey() + "=" + URLEncoder.encode(upskv.getValue().toString().trim(), "UTF-8") + "&";
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            }
	            paramStr = paramStr.substring(0, paramStr.length() - 1);
	        }
	        
	      //写入字符串
	        writer.write(paramStr);
	        
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			//结束Writer和OutputStream
			try {
				writer.close();
				outs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
       		
        BufferedReader bufferedReader = null;
        String line;
        StringBuilder  stringBuilder = null;
        //获得请求的响应状态
        try {
			if(urlconn.getResponseCode() != 200){
				System.out.println("urlconn.getResponseCode() != 200 , 请求不成功。urlconn.getResponseCode()=="+urlconn.getResponseCode());
			    throw new IOException(urlconn.getResponseMessage());
			}else{
				System.out.println("============urlconn.getResponseCode() = 200 , 请求发送成功.");
			}
//			//实例一个Buffer读取和字符串Builder
//			bufferedReader = new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
//			stringBuilder = new StringBuilder();
//			//将读取到的数据装载到line当中
//			while((line = bufferedReader.readLine()) != null){
//			    stringBuilder.append(line);
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
//			try {
//				bufferedReader.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			urlconn.disconnect();
		}
//        return stringBuilder.toString();
        return "";

    }

    //检测
    public static void main(String[] args) throws IOException {
        Map params = new HashMap();
        params.put("postType", "1");
        params.put("username", "developer");
        params.put("password", "111111");
        
        Long endTime = System.currentTimeMillis(); 
        
        params.put("p_CONFIG_ID", 2);
        params.put("p_STARTTIME", "2111-07-04 13:51:20");
        params.put("p_ENDTIME", "2011-07-04 13:51:20");
        params.put("p_EXPORT_COUNT", 33);
        params.put("p_STATUS", 1);
        params.put("p_DATA_PATH", "55555");
        
        HttpRest httpRest = new HttpRest();
//        String str = httpRest.restPost("http://localhost:8080/datahub/dataExportMonitor/save.shtml", params);
       
    }
}