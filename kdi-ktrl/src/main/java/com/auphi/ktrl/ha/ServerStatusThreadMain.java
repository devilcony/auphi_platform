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
package com.auphi.ktrl.ha;

import java.util.List;

import org.apache.log4j.Logger;

import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.util.Constants;

public class ServerStatusThreadMain extends Thread {
	private static Logger logger = Logger.getLogger(ServerStatusThreadMain.class);
	private static int INTERVAL = Constants.get("HAMonitorInterval")==null||"".equals(Constants.get("HAMonitorInterval"))?30000:Integer.parseInt(Constants.get("HAMonitorInterval"));
	
	@Override
	public void run() {
		while(true){
			try{
				List<SlaveServerBean> listSlaveServer = SlaveServerUtil.findAll();
				for(SlaveServerBean slaveServer : listSlaveServer){
					ServerStatusThread serverStatusThread = new ServerStatusThread(slaveServer);
					serverStatusThread.start();
				}
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}finally {
				try {
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public static void start_minitor(){
		Thread serverStatusThreadMain = new ServerStatusThreadMain();
		serverStatusThreadMain.start();
	}
}
