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
package com.auphi.ktrl.schedule.template;

import java.util.Date;


public class TemplateFactory {
	
	public static Template createTemplate(String repName, String middlePath, Date date, boolean isReload) throws Exception{
		Template template = null;
		
		//if the class name equals template middlePath, instantiation the class
		if(Template431.class.getName().endsWith(middlePath)){
			template = new Template431(repName, middlePath, date, isReload);
		}else if(Template411.class.getName().endsWith(middlePath)){
			template = new Template411(repName, middlePath, date, isReload);
		}else if(Template341.class.getName().endsWith(middlePath)){
			template = new Template341(repName, middlePath, date, isReload);
		}else if(Template111.class.getName().endsWith(middlePath)){
			template = new Template111(repName, middlePath, date, isReload);
		}else if(Template131.class.getName().endsWith(middlePath)){
			template = new Template131(repName, middlePath, date, isReload);
		}else if(Template231.class.getName().endsWith(middlePath)){
			template = new Template231(repName, middlePath, date, isReload);
		}else if(Template141.class.getName().endsWith(middlePath)){
			template = new Template141(repName, middlePath, date, isReload);
		}
		
		return template;
	}
}
