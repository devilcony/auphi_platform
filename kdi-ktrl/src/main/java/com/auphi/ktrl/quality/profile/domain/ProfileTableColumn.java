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

/**
 * @auther Tony
 * @create 2017-02-09 08:45
 */
public class ProfileTableColumn extends BaseEntity{

    private Integer profileTableColumnId; //id

    private String  profileTableColumnName; //列名

    private String  profileTableColumnDesc; //列描述

    private ProfileTableGroup profielTableGroup;//所属组

    private ProfileTable profileTable; //所属表

    private Integer  profileTableColumnOrder;


    private String  ids; //删除时选择的ids

    public ProfileTableColumn() {

    }

    public ProfileTableColumn(ProfileTable obj) {
        setProfileTable(obj);
    }


    public Integer getProfileTableColumnId() {
        return profileTableColumnId;
    }

    public void setProfileTableColumnId(Integer profileTableColumnId) {
        this.profileTableColumnId = profileTableColumnId;
    }

    public String getProfileTableColumnName() {
        return profileTableColumnName;
    }

    public void setProfileTableColumnName(String profileTableColumnName) {
        this.profileTableColumnName = profileTableColumnName;
    }

    public String getProfileTableColumnDesc() {
        return profileTableColumnDesc;
    }

    public void setProfileTableColumnDesc(String profileTableColumnDesc) {
        this.profileTableColumnDesc = profileTableColumnDesc;
    }

    public ProfileTable getProfileTable() {
        return profileTable;
    }

    public void setProfileTable(ProfileTable profileTable) {
        this.profileTable = profileTable;
    }

    public ProfileTableGroup getProfielTableGroup() {
        return profielTableGroup;
    }

    public void setProfielTableGroup(ProfileTableGroup profielTableGroup) {
        this.profielTableGroup = profielTableGroup;
    }

    public Integer getProfileTableColumnOrder() {
        return profileTableColumnOrder;
    }

    public void setProfileTableColumnOrder(Integer profileTableColumnOrder) {
        this.profileTableColumnOrder = profileTableColumnOrder;
    }


    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
