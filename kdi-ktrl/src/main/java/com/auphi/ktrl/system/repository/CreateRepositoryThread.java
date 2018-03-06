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
package com.auphi.ktrl.system.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;

public class CreateRepositoryThread extends Thread {
	private static Logger logger = Logger.getLogger(CreateRepositoryThread.class);
	private RepositoryBean repBean;
	
	public CreateRepositoryThread(RepositoryBean repBean){
		this.repBean = repBean;
	}
	
	@Override
	public void run() {
		KettleEngine kettleEngine = new KettleEngineImpl4_3();
		kettleEngine.addNewTables(repBean);
    	kettleEngine.createRepository(repBean, false);
		RepositoryUtil.createRepository(repBean);
	}
}
