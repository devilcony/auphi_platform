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
package com.auphi.ktrl.system.organizer.bean;

public class OrganizerBean {
	private int organizer_id;
	private String organizer_name;
	private String organizer_contact;
	private String organizer_email;
	private String organizer_telphone;
	private String organizer_mobile;
	private String organizer_passwd;
	private String organizer_address;
	private String organizer_verify_code;
	private int organizer_status;
	
	public int getOrganizer_id() {
		return organizer_id;
	}
	public void setOrganizer_id(int organizer_id) {
		this.organizer_id = organizer_id;
	}
	public String getOrganizer_name() {
		return organizer_name;
	}
	public void setOrganizer_name(String organizer_name) {
		this.organizer_name = organizer_name;
	}
	public String getOrganizer_contact() {
		return organizer_contact;
	}
	public void setOrganizer_contact(String organizer_contact) {
		this.organizer_contact = organizer_contact;
	}
	public String getOrganizer_email() {
		return organizer_email;
	}
	public void setOrganizer_email(String organizer_email) {
		this.organizer_email = organizer_email;
	}
	public String getOrganizer_telphone() {
		return organizer_telphone;
	}
	public void setOrganizer_telphone(String organizer_telphone) {
		this.organizer_telphone = organizer_telphone;
	}
	public String getOrganizer_mobile() {
		return organizer_mobile;
	}
	public void setOrganizer_mobile(String organizer_mobile) {
		this.organizer_mobile = organizer_mobile;
	}
	public String getOrganizer_address() {
		return organizer_address;
	}
	public void setOrganizer_address(String organizer_address) {
		this.organizer_address = organizer_address;
	}
	public String getOrganizer_verify_code() {
		return organizer_verify_code;
	}
	public void setOrganizer_verify_code(String organizer_verify_code) {
		this.organizer_verify_code = organizer_verify_code;
	}
	public int getOrganizer_status() {
		return organizer_status;
	}
	public void setOrganizer_status(int organizer_status) {
		this.organizer_status = organizer_status;
	}
	public String getOrganizer_passwd() {
		return organizer_passwd;
	}
	public void setOrganizer_passwd(String organizer_passwd) {
		this.organizer_passwd = organizer_passwd;
	}
	public OrganizerBean(int organizer_id, String organizer_name, String organizer_contact, String organizer_email,
			String organizer_passwd, String organizer_telphone, String organizer_mobile, String organizer_address, 
			String organizer_verify_code, int organizer_status) {
		super();
		this.organizer_id = organizer_id;
		this.organizer_name = organizer_name;
		this.organizer_contact = organizer_contact;
		this.organizer_email = organizer_email;
		this.organizer_passwd = organizer_passwd;
		this.organizer_telphone = organizer_telphone;
		this.organizer_mobile = organizer_mobile;
		this.organizer_address = organizer_address;
		this.organizer_verify_code = organizer_verify_code;
		this.organizer_status = organizer_status;
	}
	public OrganizerBean() {
		this.organizer_name = "";
		this.organizer_contact = "";
		this.organizer_email = "";
		this.organizer_passwd = "";
		this.organizer_telphone = "";
		this.organizer_mobile = "";
		this.organizer_address = "";
	}
}
