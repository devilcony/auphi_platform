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


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @auther Tony
 * @create 2017-02-08 22:28
 */
public class ProfileTableGroup {

    private Integer profielTableGroupId;

    private String profielTableGroupName;

    private String profielTableGroupDesc;

    private String ids;

    public Integer getProfielTableGroupId() {
        return profielTableGroupId;
    }

    public void setProfielTableGroupId(Integer profielTableGroupId) {
        this.profielTableGroupId = profielTableGroupId;
    }

    public String getProfielTableGroupName() {
        return profielTableGroupName;
    }

    public void setProfielTableGroupName(String profielTableGroupName) {
        this.profielTableGroupName = profielTableGroupName;
    }

    public String getProfielTableGroupDesc() {
        return profielTableGroupDesc;
    }

    public void setProfielTableGroupDesc(String profielTableGroupDesc) {
        this.profielTableGroupDesc = profielTableGroupDesc;
    }

    @JsonIgnore
    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
