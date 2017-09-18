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
package com.auphi.ktrl.system.role.bean;

import com.auphi.ktrl.system.priviledge.bean.PriviledgeType;

public class RoleBean
{
    private String role_id  ;
    private String role_name ;
    private String description ;
    private long priviledges ;
    private int isSystemRole ;
    
    public RoleBean()
    {
        
    }
    
    public RoleBean(String role_id, String role_name, String description,
            long priviledges, int isSystemRoel)
    {
        this.role_id = role_id;
        this.role_name = role_name;
        this.description = description;
        this.priviledges = priviledges;
        this.isSystemRole = isSystemRoel;
    }

    public String getRole_id()
    {
        return role_id;
    }

    public String getRole_name()
    {
        return role_name;
    }

    public String getDescription()
    {
        return description;
    }

    public long getPriviledges()
    {
        return priviledges;
    }

    public boolean isSystemRole()
    {
        return isSystemRole == 1;
    }
    
    public boolean hasPrivilege(PriviledgeType priviledge)
    {
        return (priviledges & priviledge.getPriviledge_id()) ==  priviledge.getPriviledge_id();
    }

    public int getIsSystemRole()
    {
        return isSystemRole;
    }

    public void setIsSystemRole(int isSystemRole)
    {
        this.isSystemRole = isSystemRole;
    }

    public void setRole_id(String role_id)
    {
        this.role_id = role_id;
    }

    public void setRole_name(String role_name)
    {
        this.role_name = role_name;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setPriviledges(long priviledges)
    {
        this.priviledges = priviledges;
    }
}
