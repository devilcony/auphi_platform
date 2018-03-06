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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:10
 */
public class CompareSql extends BaseEntity{

    private Integer compareSqlId; //id

    private ProfileTableGroup compareTableGroup;//所属组

    private Integer databaseId; //数据源Id

    private String databaseName; //数据源名称

    private Integer referenceDbId; //参照数据源Id //

    private String referenceDbname; //参照数据名称

    private String compareName; //名称

    private String compareDesc;//描述

    private int compareType; //类型

    private String sql;//sql语句

    private String referenceSql;//参照sql

    private Date createTime;//创建时间

    private Integer userId; //用户Id

    private List<CompareSqlColumn> compareSqlColumns = new ArrayList<CompareSqlColumn>();

    public Integer getCompareSqlId() {
        return compareSqlId;
    }

    public void setCompareSqlId(Integer compareSqlId) {
        this.compareSqlId = compareSqlId;
    }

    public ProfileTableGroup getCompareTableGroup() {
        return compareTableGroup;
    }

    public void setCompareTableGroup(ProfileTableGroup compareTableGroup) {
        this.compareTableGroup = compareTableGroup;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public String getCompareName() {
        return compareName;
    }

    public void setCompareName(String compareName) {
        this.compareName = compareName;
    }

    public String getCompareDesc() {
        return compareDesc;
    }

    public void setCompareDesc(String compareDesc) {
        this.compareDesc = compareDesc;
    }

    public int getCompareType() {
        return compareType;
    }

    public void setCompareType(int compareType) {
        this.compareType = compareType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getReferenceSql() {
        return referenceSql;
    }

    public void setReferenceSql(String referenceSql) {
        this.referenceSql = referenceSql;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<CompareSqlColumn> getCompareSqlColumns() {
        return compareSqlColumns;
    }

    public void setCompareSqlColumns(List<CompareSqlColumn> compareSqlColumns) {
        this.compareSqlColumns = compareSqlColumns;
    }

    public Integer getReferenceDbId() {
        return referenceDbId;
    }

    public void setReferenceDbId(Integer referenceDbId) {
        this.referenceDbId = referenceDbId;
    }

    public String getReferenceDbname() {
        return referenceDbname;
    }

    public void setReferenceDbname(String referenceDbname) {
        this.referenceDbname = referenceDbname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompareSql that = (CompareSql) o;

        return compareSqlId != null ? compareSqlId.equals(that.compareSqlId) : that.compareSqlId == null;

    }

    @Override
    public int hashCode() {
        return compareSqlId != null ? compareSqlId.hashCode() : 0;
    }
}
