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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.pentaho.di.www.GetStatusServlet;

import com.auphi.ktrl.ha.bean.ServerStatusBean;
import com.auphi.ktrl.ha.bean.SlaveServerBean;
import com.auphi.ktrl.ha.util.SlaveServerUtil;
import com.auphi.ktrl.util.Constants;

public class ServerStatusThread extends Thread {
	private static Logger logger = Logger.getLogger(ServerStatusThread.class);
	private static int CONN_TIMEOUT = Constants.get("HAMonitorConnectTimeOut")==null||"".equals(Constants.get("HAMonitorConnectTimeOut"))?5000:Integer.parseInt(Constants.get("HAMonitorConnectTimeOut"));
	private static int SO_TIMEOUT = Constants.get("HAMonitorSoTimeOut")==null||"".equals(Constants.get("HAMonitorSoTimeOut"))?15000:Integer.parseInt(Constants.get("HAMonitorSoTimeOut"));
	private static String USER_NAME = Constants.get("HALoginUser")==null?"cluster":Constants.get("HALoginUser");
	private static String PASS_WORD = Constants.get("HALoginPassword")==null?"cluster":Constants.get("HALoginPassword");
	
	private String host = "";
	private String port = "80";
	private int id_slave;
	
	public ServerStatusThread(SlaveServerBean slaveServer){
		this.host = slaveServer.getHost_name();
		this.port = slaveServer.getPort();
		this.id_slave = slaveServer.getId_slave();
	}
	
	@Override
	public void run() {
		ServerStatusBean serverStatusBean = new ServerStatusBean();
		GetMethod getMethod = new GetMethod("http://" + host + ":" + port + GetStatusServlet.CONTEXT_PATH + "?action=getServerStatus");
	    
		try{
			MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		    manager.getParams().setConnectionTimeout(CONN_TIMEOUT);
		    manager.getParams().setSoTimeout(SO_TIMEOUT);
			
			HttpClient httpClient = new HttpClient(manager);
		       
		    Credentials defaultcreds = new UsernamePasswordCredentials(USER_NAME, PASS_WORD);
		    httpClient.getState().setCredentials(AuthScope.ANY, defaultcreds);
		    httpClient.getParams().setAuthenticationPreemptive(true);
		       
		    getMethod.setDoAuthentication( true ); 
		       
		    httpClient.executeMethod(getMethod);
		    
		    String resString = getMethod.getResponseBodyAsString();
		    String res[] = resString.split(",");
		    serverStatusBean.setId_slave(id_slave);
		    serverStatusBean.setIs_running(1);
		    serverStatusBean.setMemory_usage(Float.parseFloat(res[0]));
		    serverStatusBean.setCpu_usage(Float.parseFloat(res[1]));
		    serverStatusBean.setRunning_jobs_num(Integer.parseInt(res[2]));
		}catch(Exception e){
//			logger.error(e.getMessage(), e);
			logger.error("Failed to get slave server status![id_slave:" + id_slave + "]");
		    serverStatusBean.setId_slave(id_slave);
			serverStatusBean.setIs_running(0);
			serverStatusBean.setMemory_usage(0);
		    serverStatusBean.setCpu_usage(0);
		    serverStatusBean.setRunning_jobs_num(0);
		}finally {
			getMethod.releaseConnection();
			SlaveServerUtil.updateServerStatus(serverStatusBean);
		}
	}
}
