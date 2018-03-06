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
package com.auphi.ktrl.schedule;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.pentaho.di.core.encryption.Encr;

public class Test {
	public static void main(String[] args) {
	//	System.out.println(Encr.encryptPasswordIfNotUsingVariables("myblog"));
	  //  System.out.println(Encr.decryptPasswordOptionallyEncrypted("Encrypted 2be98afc86aa7f29ea318a077d897a1dd"));  
    Date date= new Date();
    Date date2=null;
    List<Date> dates= new ArrayList<Date>();
    for (int i = 1; i <13; i++) {
    	if(date2!=null)
    	{
    		date.setDate(date2.getDate());
    	}
        date.setDate(date.getDate()+i);
        date2= date;	
       // System.out.println(date2.ge);
	 }
    for (int i = 0; i < dates.size(); i++) {
		System.out.println(dates.get(i).getMonth());
	}
	}

}
