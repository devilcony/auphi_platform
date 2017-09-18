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
package com.auphi.ktrl.system.priviledge.bean;

import com.auphi.ktrl.i18n.Messages;

enum ResourceType
{
    
    File(1,"Priviledge.ResourceType.File"),
    
    Directory(2,"Priviledge.ResourceType.Directory"),
    
    User(4,"Priviledge.ResourceType.User"),
    
    Role(8,"Priviledge.ResourceType.Role"),
    
    Cluster(16,"Priviledge.ResourceType.Cluster");
    

    final int resource_type_id ;
    final String resource_name;

    ResourceType(
        int resource_type_id,
        String resource_name_property)
    {
        this.resource_type_id = resource_type_id ;
        this.resource_name = Messages.getString(resource_name_property) ;
    }
    
    public int getResourceTypeId()
    {
        return this.resource_type_id ;
    }
    
    public String getResourceName()
    {
        return this.resource_name ;
    }
    
    public static ResourceType getResourceType(int resource_type_id)
    {
        switch(resource_type_id)
        {
            case 1:
                return File ;
            case 2:
                return Role ;
            case 4:
                return User ;
            case 8:
                return Role ;
            case 16:
                return Cluster ;
        }
        return null ;
    }    

    public static void main(String[] args)
    {
        for (int i = 0 ; i < ResourceType.values().length ; i ++)
        {
            //INSERT kdi_t_resource_type VALUES (1,'目录');
            System.out.println("INSERT INTO kdi_t_resource_type VALUES("+ResourceType.values()[i].resource_type_id
                +",'"+ResourceType.values()[i].resource_name+"');");
        }
        return ;
    }
}
