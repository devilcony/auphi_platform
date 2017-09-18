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
package com.auphi.ktrl.quality.profile.domain;

import com.auphi.ktrl.quality.base.BaseEntity;

import java.util.Date;

/**
 * @auther Tony
 * @create 2017-02-08 22:22
 */
public class ProfileTableResult extends BaseEntity {

    private Integer profileTableResultId;


    private ProfileTableGroup profielTableGroup; //分组Id

    private ProfileTable profileTable; //表名

    private ProfileTableColumn profileTableColumn; //列ID

    private String indicatorDataType; //类型

    private Integer indicatorDataLength; //长度

    private Integer indicatorDataPrecision;//精度

    private Integer indicatorDataScale; //小数长度

    private Integer indicatorAllCount;//总数

    private Integer indicatorDistinctCount; //不同值的个数

    private Integer indicatorNullCount; //空值的个数

    private Integer indicatorZeroCount; //零的个数

    private String indicatorAggAvg;//平均值

    private String indicatorAggMax;//最大值

    private String indicatorAggMin;//最小值

    private Date createTime;//创建时间

    private String executeSql;//执行的sql


    public Integer getProfileTableResultId() {
        return profileTableResultId;
    }

    public void setProfileTableResultId(Integer profileTableResultId) {
        this.profileTableResultId = profileTableResultId;
    }

    public ProfileTableGroup getProfielTableGroup() {
        return profielTableGroup;
    }

    public void setProfielTableGroup(ProfileTableGroup profielTableGroup) {
        this.profielTableGroup = profielTableGroup;
    }

    public ProfileTable getProfileTable() {
        return profileTable;
    }

    public void setProfileTable(ProfileTable profileTable) {
        this.profileTable = profileTable;
    }

    public ProfileTableColumn getProfileTableColumn() {
        return profileTableColumn;
    }

    public void setProfileTableColumn(ProfileTableColumn profileTableColumn) {
        this.profileTableColumn = profileTableColumn;
    }

    public String getIndicatorDataType() {
        return indicatorDataType;
    }

    public void setIndicatorDataType(String indicatorDataType) {
        this.indicatorDataType = indicatorDataType;
    }

    public Integer getIndicatorDataLength() {
        return indicatorDataLength;
    }

    public void setIndicatorDataLength(Integer indicatorDataLength) {
        this.indicatorDataLength = indicatorDataLength;
    }

    public Integer getIndicatorDataPrecision() {
        return indicatorDataPrecision;
    }

    public void setIndicatorDataPrecision(Integer indicatorDataPrecision) {
        this.indicatorDataPrecision = indicatorDataPrecision;
    }

    public Integer getIndicatorDataScale() {
        return indicatorDataScale;
    }

    public void setIndicatorDataScale(Integer indicatorDataScale) {
        this.indicatorDataScale = indicatorDataScale;
    }

    public Integer getIndicatorAllCount() {
        return indicatorAllCount;
    }

    public void setIndicatorAllCount(Integer indicatorAllCount) {
        this.indicatorAllCount = indicatorAllCount;
    }

    public Integer getIndicatorDistinctCount() {
        return indicatorDistinctCount;
    }

    public void setIndicatorDistinctCount(Integer indicatorDistinctCount) {
        this.indicatorDistinctCount = indicatorDistinctCount;
    }

    public Integer getIndicatorNullCount() {
        return indicatorNullCount;
    }

    public void setIndicatorNullCount(Integer indicatorNullCount) {
        this.indicatorNullCount = indicatorNullCount;
    }

    public Integer getIndicatorZeroCount() {
        return indicatorZeroCount;
    }

    public void setIndicatorZeroCount(Integer indicatorZeroCount) {
        this.indicatorZeroCount = indicatorZeroCount;
    }

    public String getIndicatorAggAvg() {
        return indicatorAggAvg;
    }

    public void setIndicatorAggAvg(String indicatorAggAvg) {
        this.indicatorAggAvg = indicatorAggAvg;
    }

    public String getIndicatorAggMax() {
        return indicatorAggMax;
    }

    public void setIndicatorAggMax(String indicatorAggMax) {
        this.indicatorAggMax = indicatorAggMax;
    }

    public String getIndicatorAggMin() {
        return indicatorAggMin;
    }

    public void setIndicatorAggMin(String indicatorAggMin) {
        this.indicatorAggMin = indicatorAggMin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExecuteSql() {
        return executeSql;
    }

    public void setExecuteSql(String executeSql) {
        this.executeSql = executeSql;
    }
}
