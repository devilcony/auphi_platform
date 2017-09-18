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
package com.auphi.data.hub.domain;


import com.auphi.data.hub.core.struct.BaseVo;


public class UserInfo extends BaseVo {
	
	private String userid;

	/**
	 * 用户名：varchar2(20)
	 */
	private String username;

	/**
	 * 登陆帐户：varchar2(20)
	 */
	private String account;

	/**
	 * 密码：varchar2(20)
	 */
	private String password;

	/**
	 * 性别(0:未知;1:男;2:女)：varchar2(2)
	 */
	private String sex;

	/**
	 * 部门编号：varchar2(8)
	 */
	private String deptid;

	/**
	 * 锁定标志(0:锁定;1:激活)：varchar2(2)
	 */
	private String locked;

	/**
	 * 自定部门编号
	 */
	private String customId;
	
	/**
	 * 自定义主题
	 */
	private String theme;
	
	/**
	 * 会话ID
	 */
	private String sessionID;
	
	/**
	 * 会话创建时间
	 */
	private String sessionCreatedTime;
	
	/**
	 * 登录IP
	 */
	private String loginIP;
	
	/**
	 * 浏览器
	 */
	private String explorer;
	
	/**
	 * 启用状态，1表示启用
	 */
	private String enabled;
	
	private String deptid_old;
	
	private String remark;
	
	private String usertype;

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getDeptid_old() {
		return deptid_old;
	}

	public void setDeptid_old(String deptid_old) {
		this.deptid_old = deptid_old;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}


	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSessionCreatedTime() {
		return sessionCreatedTime;
	}

	public void setSessionCreatedTime(String sessionCreatedTime) {
		this.sessionCreatedTime = sessionCreatedTime;
	}

	public String getLoginIP() {
		return loginIP;
	}

	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}

	public String getExplorer() {
		return explorer;
	}

	public void setExplorer(String explorer) {
		this.explorer = explorer;
	}
	
}
