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


/*
 * 
 * 字段
 * ***/
public class FieldMappingView {
	private String sourceColumnName;
	private String reference;
	private Integer startIndex;
	private Integer endIndex;
	private String sourceColumnType;
	private String destColumnType;
	private String destColumuName;
	private String destLength;
	private String destScale;
	private boolean isPrimary;
	private boolean isNullable;
	//private List<DestColumuTypeView> destColumuTypeViews;

	public String getDestColumuName() {
		return destColumuName;
	}

	public void setDestColumuName(String destColumuName) {
		this.destColumuName = destColumuName;
	}



	public String getSourceColumnName() {
		return sourceColumnName;
	}

	public void setSourceColumnName(String sourceColumnName) {
		this.sourceColumnName = sourceColumnName;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

	public Integer getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(Integer endIndex) {
		this.endIndex = endIndex;
	}

	public String getSourceColumnType() {
		return sourceColumnType;
	}

	public void setSourceColumnType(String sourceColumnType) {
		this.sourceColumnType = sourceColumnType;
	}

	public String getDestColumnType() {
		return destColumnType;
	}

	public void setDestColumnType(String destColumnType) {
		this.destColumnType = destColumnType;
	}

	public String getDestLength() {
		return destLength;
	}

	public void setDestLength(String destLength) {
		this.destLength = destLength;
	}

	public boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public boolean getIsNullable() {
		return isNullable;
	}

	public void setIsNullable(boolean isNullable) {
		this.isNullable = isNullable;
	}

	public String getDestScale() {
		return destScale;
	}

	public void setDestScale(String destScale) {
		this.destScale = destScale;
	}







}
