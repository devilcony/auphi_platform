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
package com.auphi.data.hub.rest;

import java.util.ArrayList;
import java.util.List;

import com.auphi.data.hub.core.util.JsonHelper;

public class ResultData {

	private String tableFields;
	
	private long recordCount;
	
	private List<String> records;
	
	private int statuscode;
	
	private String msg;

	public String getTableFields() {
		return tableFields;
	}

	public void setTableFields(String tableFields) {
		this.tableFields = tableFields;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public List<String> getRecords() {
		return records;
	}

	public void setRecords(List<String> records) {
		this.records = records;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	public static void main(String args[]){
		ResultData data = new  ResultData();
		data.setTableFields("starttime,fr,qy,wd,sbbm,czr,gzh,lj,peek");
		data.setRecordCount(200);
		data.setStatuscode(200);
		data.setMsg("OK");
		List<String> records = new ArrayList<String>();
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		records.add("1406044800,00,28,0000,1,0;0;0,CNY,9999,100,VEJF592592,CCB13/GLY/001060165,/20140723/00/0028/0028_0.zip,132");
		data.setRecords(records);
		
		System.out.println(JsonHelper.encodeObject2Json(data));
		
	}
	
}
