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
package com.auphi.ktrl.schedule.view;

public class FastConfigDatabaseView {
	private Integer idDatase;
	private String name;
	private Integer idDatabaseType;
	private Integer idDatabaseContype;
	private String hostName;
	private String databaseName;
	private Integer port;
	private String userName;
	private String password;
	private String serverName;
	private String dataTbs;
	private String indexTbs;
	
	public FastConfigDatabaseView() {
		super();
	}

	public FastConfigDatabaseView(Integer idDatase, String name,
			Integer idDatabaseType, Integer idDatabaseContype, String hostName,
			String databaseName, Integer port, String userName,
			String password, String serverName, String dataTbs, String indexTbs) {
		super();
		this.idDatase = idDatase;
		this.name = name;
		this.idDatabaseType = idDatabaseType;
		this.idDatabaseContype = idDatabaseContype;
		this.hostName = hostName;
		this.databaseName = databaseName;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.serverName = serverName;
		this.dataTbs = dataTbs;
		this.indexTbs = indexTbs;
	}

	public Integer getIdDatase() {
		return idDatase;
	}

	public void setIdDatase(Integer idDatase) {
		this.idDatase = idDatase;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIdDatabaseType() {
		return idDatabaseType;
	}

	public void setIdDatabaseType(Integer idDatabaseType) {
		this.idDatabaseType = idDatabaseType;
	}

	public Integer getIdDatabaseContype() {
		return idDatabaseContype;
	}

	public void setIdDatabaseContype(Integer idDatabaseContype) {
		this.idDatabaseContype = idDatabaseContype;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getDataTbs() {
		return dataTbs;
	}

	public void setDataTbs(String dataTbs) {
		this.dataTbs = dataTbs;
	}

	public String getIndexTbs() {
		return indexTbs;
	}

	public void setIndexTbs(String indexTbs) {
		this.indexTbs = indexTbs;
	}

}
