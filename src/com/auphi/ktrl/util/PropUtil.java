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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropUtil {
	
	private static Logger logger = Logger.getLogger(PropUtil.class);

	private Properties p = new Properties();
	private File file = null; 
	
	public PropUtil(File file) {
		try {
			this.file = file;
			if(!file.exists()){
				file.createNewFile();
			}
			FileInputStream fis = new FileInputStream(file); 
			p.load(fis);
			fis.close();
		} catch (IOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	public String get(String key) {
		return p.getProperty(key, "");
	}
	
	public String get(String key,String defaultValue) {
		return p.getProperty(key, defaultValue);
	}
	
	public void set(String key, String value){
		try{
			Writer w = new FileWriter(file);
			p.setProperty(key, value);
			p.store(w, key);
			w.close();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public void clear(){
		this.p = null;
		this.file = null;
	}
}
