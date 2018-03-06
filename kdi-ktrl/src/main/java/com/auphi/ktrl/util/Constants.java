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
package com.auphi.ktrl.util;


import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class Constants {

	private static Logger logger = Logger.getLogger(Constants.class);
	public static String PARAM_ACTION = "action" ;
	public static String WEB_IP = "WebIP";
	
	public static Properties p = new Properties();
	static {
		try {
			p.load(Constants.class.getResourceAsStream("/constants_auphi.properties"));
			String webIP = p.getProperty(WEB_IP);
			if(webIP==null || "".equals(webIP)){
//				webIP = Constants.getWebIp();
//				p.setProperty(WEB_IP, webIP);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	public static String get(String key) {
		return p.getProperty(key, "");
	}
	
	public static String get(String key,String defaultValue) {
		return p.getProperty(key, defaultValue);
	}
	
	public static void set(String key, String value){
		try{
			Writer w=new FileWriter(Constants.class.getResource("").getFile());
			p.setProperty(key, value);
			p.store(w, key);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}

	}
	
	public static String getWebIp() {
		String ip = "";
		String chinaz = "http://ip.chinaz.com";
		
		StringBuilder inputLine = new StringBuilder();
		String read = "";
		URL url = null;
		HttpURLConnection urlConnection = null;
		BufferedReader in = null;
		try {
			url = new URL(chinaz);
			urlConnection = (HttpURLConnection) url.openConnection();
		    in = new BufferedReader( new InputStreamReader(urlConnection.getInputStream(),"UTF-8"));
			while((read=in.readLine())!=null){
				inputLine.append(read+"\r\n");
			}
			//System.out.println(inputLine.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		Pattern p = Pattern.compile("\\<dd class\\=\"fz24\">(.*?)\\<\\/dd>");
		Matcher m = p.matcher(inputLine.toString());
		if(m.find()){
			String ipstr = m.group(1);
			ip = ipstr;
			//System.out.println(ipstr);
		}
		return ip;
	}
}
