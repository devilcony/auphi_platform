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
import com.auphi.ktrl.quality.profile.domain.ProfileTableGroup;

import java.util.Date;

/**
 * @auther Tony
 * @create 2017-02-11 10:23
 */
public class CompareSqlResult extends BaseEntity {

    private  Integer compareSqlResultId;//id ID_COMPARE_SQL_RESULT

    private  CompareSqlColumn compareSqlColumn; //列名

    private  CompareSql compareSql;

    private ProfileTableGroup compareTableGroup; //分组Id

    private String columnValue; //结果值

    private String referenceColumnValue;//参照结果值

    private Integer compareResult;//比对结果

    private Date createTime;//时间

    public CompareSqlResult() {

    }

    public CompareSqlResult(CompareSqlColumn compareSqlColumn) {
        setCompareSqlColumn(compareSqlColumn);
    }


    public Integer getCompareSqlResultId() {
        return compareSqlResultId;
    }

    public void setCompareSqlResultId(Integer compareSqlResultId) {
        this.compareSqlResultId = compareSqlResultId;
    }

    public CompareSqlColumn getCompareSqlColumn() {
        return compareSqlColumn;
    }

    public void setCompareSqlColumn(CompareSqlColumn compareSqlColumn) {
        this.compareSqlColumn = compareSqlColumn;
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

    public Integer getCompareResult() {
        return compareResult;
    }

    public void setCompareResult(Integer compareResult) {
        this.compareResult = compareResult;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public CompareSql getCompareSql() {
        return compareSql;
    }

    public void setCompareSql(CompareSql compareSql) {
        this.compareSql = compareSql;
    }

    public ProfileTableGroup getCompareTableGroup() {
        return compareTableGroup;
    }

    public void setCompareTableGroup(ProfileTableGroup compareTableGroup) {
        this.compareTableGroup = compareTableGroup;
    }
}
