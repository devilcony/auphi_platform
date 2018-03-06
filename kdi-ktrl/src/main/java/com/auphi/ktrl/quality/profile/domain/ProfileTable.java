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
import com.auphi.ktrl.schedule.template.TemplateUtil;
import org.codehaus.jackson.map.annotate.JacksonInject;

import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-09 08:44
 *
 * 数据剖析-选择表
 */
public class ProfileTable  extends BaseEntity {

    private Integer profileTableId; //主键-自增长

    private Integer databaseId;//数据源id

    private String databaseName;//数据源显示名称

    private ProfileTableGroup profielTableGroup;//所属组

    private String profielName; //名称

    private String profielDesc;//描述

    private String schemaName;//模式名称

    private int tableNameTag;//1:表名 2:sql

    private String sql;//1:表名 2:sql

    private String tableName;//表名称

    private String condition;//条件//where 语句

    private Date createTime;//创建时间

    private Integer userId;//创建人Id

    List<ProfileTableColumn> profileTableColumns ;



    public Integer getProfileTableId() {
        return profileTableId;
    }

    public void setProfileTableId(Integer profileTableId) {
        this.profileTableId = profileTableId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Integer getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(Integer databaseId) {
        this.databaseId = databaseId;
    }

    public ProfileTableGroup getProfielTableGroup() {
        return profielTableGroup;
    }

    public void setProfielTableGroup(ProfileTableGroup profielTableGroup) {
        this.profielTableGroup = profielTableGroup;
    }

    public String getProfielName() {
        return profielName;
    }

    public void setProfielName(String profielName) {
        this.profielName = profielName;
    }

    public String getProfielDesc() {
        return profielDesc;
    }

    public void setProfielDesc(String profielDesc) {
        this.profielDesc = profielDesc;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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



    public List<ProfileTableColumn> getProfileTableColumns() {
        return profileTableColumns;
    }

    public void setProfileTableColumns(List<ProfileTableColumn> profileTableColumns) {
        this.profileTableColumns = profileTableColumns;
    }

    public int getTableNameTag() {
        return tableNameTag;
    }

    public void setTableNameTag(int tableNameTag) {
        this.tableNameTag = tableNameTag;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfileTable that = (ProfileTable) o;

        return profileTableId != null ? profileTableId.equals(that.profileTableId) : that.profileTableId == null;

    }

    @Override
    public int hashCode() {
        return profileTableId != null ? profileTableId.hashCode() : 0;
    }

    @JacksonInject
    public String getSqlTableName() throws Exception {

        String tableName =  this.tableName;

        if(1 == getTableNameTag()){
            if(getSchemaName() !=null &&  !"".equals(getSchemaName())){
                tableName =  getSchemaName()+"."+tableName;
            }
        }else if(2 == getTableNameTag()){
            tableName = "("+ TemplateUtil.replaceVariable(tableName,new Date(),true)+")";
        }

        return tableName;
    }
}
