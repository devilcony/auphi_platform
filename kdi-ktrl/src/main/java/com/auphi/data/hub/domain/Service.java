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

import java.io.Serializable;
import java.util.Date;

public class Service implements Serializable{

	private static final long serialVersionUID = -27380612247048005L;

	private String serviceId;
	
	private String serviceName;
	
	private String transName;
	
	private String jobType;
	
	private String returnType;
	
	private String isCompress;
	
	private String datasource;
	
	private String timeout;
	
	private String tableName;
	
	private String delimiter;
	
	private String fields;
	
	private String conditions;
	
	private String interfaceDesc;
	
	private String serviceIdentify;
	
	private String serviceUrl;
	
	private Date createDate;
	
	private String jobConfigId;
	
	private String returnDataFormat;
	
	private Integer idDatabase;
	

	public String getReturnDataFormat() {
		return returnDataFormat;
	}

	public void setReturnDataFormat(String returnDataFormat) {
		this.returnDataFormat = returnDataFormat;
	}

	public String getJobConfigId() {
		return jobConfigId;
	}

	public void setJobConfigId(String jobConfigId) {
		this.jobConfigId = jobConfigId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getTransName() {
		return transName;
	}

	public void setTransName(String transName) {
		this.transName = transName;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getIsCompress() {
		return isCompress;
	}

	public void setIsCompress(String isCompress) {
		this.isCompress = isCompress;
	}

	public String getDatasource() {
		return datasource;
	}

	public void setDatasource(String datasource) {
		this.datasource = datasource;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public String getInterfaceDesc() {
		return interfaceDesc;
	}

	public void setInterfaceDesc(String interfaceDesc) {
		this.interfaceDesc = interfaceDesc;
	}

	public String getServiceIdentify() {
		return serviceIdentify;
	}

	public void setServiceIdentify(String serviceIdentify) {
		this.serviceIdentify = serviceIdentify;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getIdDatabase() {
		return idDatabase;
	}

	public void setIdDatabase(Integer id) {
		this.idDatabase = id;
	}
	
	
}
