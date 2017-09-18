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
package com.auphi.ktrl.quality.compare.domain;

import com.auphi.ktrl.quality.base.BaseEntity;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @auther Tony
 * @create 2017-02-11 10:18
 */
public class CompareSqlColumn extends BaseEntity{

    private Integer compareSqlColumnId;//ID_

    private String columnName;//列名

    private String columnDesc;//描述

    private String referenceColumnName;//参照列名

    private String columnType;

    private Integer compareStyle;

    private Double minRatio;

    private Double maxRatio;

    private CompareSql  compareSql;


    private String ids;

    public CompareSqlColumn() {

    }


    public CompareSqlColumn(CompareSql sql) {
       setCompareSql(sql);
    }

    public Integer getCompareSqlColumnId() {
        return compareSqlColumnId;
    }

    public void setCompareSqlColumnId(Integer compareSqlColumnId) {
        this.compareSqlColumnId = compareSqlColumnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnDesc() {
        return columnDesc;
    }

    public void setColumnDesc(String columnDesc) {
        this.columnDesc = columnDesc;
    }

    public String getReferenceColumnName() {
        return referenceColumnName;
    }

    public void setReferenceColumnName(String referenceColumnName) {
        this.referenceColumnName = referenceColumnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public CompareSql getCompareSql() {
        return compareSql;
    }

    public void setCompareSql(CompareSql compareSql) {
        this.compareSql = compareSql;
    }

    @JsonIgnore
    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getCompareStyle() {
        return compareStyle;
    }

    public void setCompareStyle(Integer compareStyle) {
        this.compareStyle = compareStyle;
    }

    public Double getMinRatio() {
        return minRatio;
    }

    public void setMinRatio(Double minRatio) {
        this.minRatio = minRatio;
    }

    public Double getMaxRatio() {
        return maxRatio;
    }

    public void setMaxRatio(Double maxRatio) {
        this.maxRatio = maxRatio;
    }
}
