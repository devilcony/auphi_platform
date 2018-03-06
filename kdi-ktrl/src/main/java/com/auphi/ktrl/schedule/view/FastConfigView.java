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


/**
 * 
 * 源数据+目标数据
 * **/
public class FastConfigView {
	private Integer idConfig;
	private Integer isFirstLineFieldName;
	private Integer idSourceType;
	private Integer idSourceDatabase;
	private Integer idSourceFTP;
	private Integer idSourceHadoop;
	private String sourceSchenaName;
	private String sourceTableName;
	private String sourceCondition;
	private String sourceFilePath;  
	private String sourceFileName;
	private String sourceSeperator;
	private Integer idDestType;
	private Integer idDestDatabase;
	private Integer idDestFTP;
	private Integer idDestHadoop;
	private String destSchenaName;
	private String destTableName;
	private String destFilePath;
	private String destFileName;
	private Integer loadType;


	
	
	public Integer getIsFirstLineFieldName() {
		return isFirstLineFieldName;
	}

	public void setIsFirstLineFieldName(Integer isFirstLineFieldName) {
		this.isFirstLineFieldName = isFirstLineFieldName;
	}

	public String getSourceSchenaName() {
		return sourceSchenaName;
	}

	public void setSourceSchenaName(String sourceSchenaName) {
		this.sourceSchenaName = sourceSchenaName;
	}

	public String getDestSchenaName() {
		return destSchenaName;
	}

	public void setDestSchenaName(String destSchenaName) {
		this.destSchenaName = destSchenaName;
	}

	public Integer getIdConfig() {
		return idConfig;
	}

	public void setIdConfig(Integer idConfig) {
		this.idConfig = idConfig;
	}

	public Integer getIdSourceType() {
		return idSourceType;
	}

	public void setIdSourceType(Integer idSourceType) {
		this.idSourceType = idSourceType;
	}

	public Integer getIdSourceDatabase() {
		return idSourceDatabase;
	}

	public void setIdSourceDatabase(Integer idSourceDatabase) {
		this.idSourceDatabase = idSourceDatabase;
	}

	public Integer getIdSourceFTP() {
		return idSourceFTP;
	}

	public void setIdSourceFTP(Integer idSourceFTP) {
		this.idSourceFTP = idSourceFTP;
	}

	public Integer getIdSourceHadoop() {
		return idSourceHadoop;
	}

	public void setIdSourceHadoop(Integer idSourceHadoop) {
		this.idSourceHadoop = idSourceHadoop;
	}

	public String getSourceTableName() {
		return sourceTableName;
	}

	public void setSourceTableName(String sourceTableName) {
		this.sourceTableName = sourceTableName;
	}

	public String getSourceCondition() {
		return sourceCondition;
	}

	public void setSourceCondition(String sourceCondition) {
		this.sourceCondition = sourceCondition;
	}

	public String getSourceFilePath() {
		return sourceFilePath;
	}

	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	public String getSourceSeperator() {
		return sourceSeperator;
	}

	public void setSourceSeperator(String sourceSeperator) {
		this.sourceSeperator = sourceSeperator;
	}

	public Integer getIdDestType() {
		return idDestType;
	}

	public void setIdDestType(Integer idDestType) {
		this.idDestType = idDestType;
	}

	public Integer getIdDestDatabase() {
		return idDestDatabase;
	}

	public void setIdDestDatabase(Integer idDestDatabase) {
		this.idDestDatabase = idDestDatabase;
	}

	public Integer getIdDestFTP() {
		return idDestFTP;
	}

	public void setIdDestFTP(Integer idDestFTP) {
		this.idDestFTP = idDestFTP;
	}

	public Integer getIdDestHadoop() {
		return idDestHadoop;
	}

	public void setIdDestHadoop(Integer idDestHadoop) {
		this.idDestHadoop = idDestHadoop;
	}

	public String getDestTableName() {
		return destTableName;
	}

	public void setDestTableName(String destTableName) {
		this.destTableName = destTableName;
	}

	public String getDestFilePath() {
		return destFilePath;
	}

	public void setDestFilePath(String destFilePath) {
		this.destFilePath = destFilePath;
	}

	public String getDestFileName() {
		return destFileName;
	}

	public void setDestFileName(String destFileName) {
		this.destFileName = destFileName;
	}

	public Integer getLoadType() {
		return loadType;
	}

	public void setLoadType(Integer loadType) {
		this.loadType = loadType;
	}
}
