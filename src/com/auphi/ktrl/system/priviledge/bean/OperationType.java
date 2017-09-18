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

enum OperationType
{
    
    Create(1,"Priviledge.OperationType.Create"),
    
    Delete(2,"Priviledge.OperationType.Delete"),
    
    Modify(4,"Priviledge.OperationType.Modify"),
    
    Execute(8,"Priviledge.OperationType.Execute"),
    
    Read(16,"Priviledge.OperationType.Read") ;
    

    final int operation_type_id ;
    final String operation_name;

    OperationType(
        int operation_type_id,
        String operation_name)
    {
        this.operation_type_id = operation_type_id ;
        this.operation_name = Messages.getString(operation_name) ;
    }
    
    public int getOperationTypeId()
    {
        return this.operation_type_id ;
    }
    
    public String getOperationName()
    {
        return this.operation_name ;
    }
    
    public static OperationType getOperationType(int operation_type_id) throws Exception
    {
        switch(operation_type_id)
        {
            case 1:
                return Create ;
            case 2:
                return Delete ;
            case 4:
                return Execute ;
            case 8:
                return Modify ;
            case 16:
                return Read ;
            default:
                throw new Exception("Unsupported operation type!") ;
                
        }
    }

}
