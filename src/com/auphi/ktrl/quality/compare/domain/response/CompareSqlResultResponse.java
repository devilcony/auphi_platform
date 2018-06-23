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
package com.auphi.ktrl.quality.compare.domain.response;

import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @auther Tony
 * @create 2017-02-11 10:23
 */
public class CompareSqlResultResponse implements Serializable {

    private  Integer compareSqlResultId;//id ID_COMPARE_SQL_RESULT

    @ApiModelProperty(value = "名称")
    private  String compareName; //列名

    @ApiModelProperty(value = "分组名称")
    private  String groupName;

    @ApiModelProperty(value = "数据源名称")
    private String databaseName;

    @ApiModelProperty(value = "列名")
    private String columnName;

    @ApiModelProperty(value = "参考列名")
    private String referenceColumnName;

    @ApiModelProperty(value = "列描述")
    private String columnDesc;

    @ApiModelProperty(value = "实际值")
    private String columnValue;

    @ApiModelProperty(value = "参照值")
    private String referenceColumnValue;

    @ApiModelProperty(value = "结果(1=通过;0=不通过)")
    private int compareResult;//比对结果

    public CompareSqlResultResponse(CompareSqlResult compareSqlResult) {
        if(compareSqlResult!=null){
            this.compareSqlResultId = compareSqlResult.getCompareSqlResultId();
            this.compareName = compareSqlResult.getCompareSql().getCompareName();
            this.groupName = compareSqlResult.getCompareTableGroup().getProfielTableGroupName();
            this.databaseName = compareSqlResult.getCompareSql().getDatabaseName();
            this.columnName = compareSqlResult.getCompareSqlColumn().getColumnName();
            this.referenceColumnName = compareSqlResult.getCompareSqlColumn().getReferenceColumnName();
            this.columnDesc = compareSqlResult.getCompareSqlColumn().getColumnDesc();
            this.columnValue = compareSqlResult.getColumnValue();
            this.referenceColumnValue = compareSqlResult.getReferenceColumnValue();
            this.compareResult = compareSqlResult.getCompareResult();
        }
    }


    public Integer getCompareSqlResultId() {
        return compareSqlResultId;
    }

    public void setCompareSqlResultId(Integer compareSqlResultId) {
        this.compareSqlResultId = compareSqlResultId;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReferenceColumnName() {
        return referenceColumnName;
    }

    public void setReferenceColumnName(String referenceColumnName) {
        this.referenceColumnName = referenceColumnName;
    }

    public String getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        this.columnDesc = columnDesc;
    }

    public String getColumnValue() {
        return columnValue;
    }

    public void setColumnValue(String columnValue) {
        this.columnValue = columnValue;
    }

    public String getReferenceColumnValue() {
        return referenceColumnValue;
    }

    public void setReferenceColumnValue(String referenceColumnValue) {
        this.referenceColumnValue = referenceColumnValue;
    }

    public int getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(int compareResult) {
        this.compareResult = compareResult;
    }
}
