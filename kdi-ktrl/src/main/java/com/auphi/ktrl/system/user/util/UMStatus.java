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
package com.auphi.ktrl.system.user.util;

import com.auphi.ktrl.i18n.Messages;

public enum UMStatus
{
    SUCCESS(0,"UserManager.Status.Success"),
    USER_NAME_EXIST(SUCCESS.statusCode+1,"UserManager.Status.UserNameExist"),
    USER_NOT_EXIST(USER_NAME_EXIST.statusCode+1,"UserManager.Status.UserNotExist"),
    UNKNOWN_ERROR(USER_NOT_EXIST.statusCode+1,"UserManager.Status.UnknownError"),
    DATABASE_EXCEPTION(UNKNOWN_ERROR.statusCode+1,"UserManager.Status.DatabaseException"),
    ROLE_NAME_EXIST(DATABASE_EXCEPTION.statusCode+1,"UserManager.Status.RoleNameExist"),
    WRONG_PASSWORD(ROLE_NAME_EXIST.statusCode+1,"UserManager.Status.WrongPassword"),
    NICK_NAME_EXIST(WRONG_PASSWORD.statusCode+1,"UserManager.Status.NickNameExist"),
	USER_NOT_ACTIVE(NICK_NAME_EXIST.statusCode+1,"UserManager.Status.UserNotActive");
    
    final int statusCode ;
    final String statusMessage ; 
    
    UMStatus(int statusCode, String messageProperty)
    {
        this.statusCode = statusCode ;
        this.statusMessage = Messages.getString(messageProperty)  ;
    }
    
    public int getStatusCode()
    {
        return this.statusCode ;
    }
    
    public String getStatusMessage()
    {
        return statusMessage ;
    }
    public String toJsonString()
    {
        StringBuffer jsonStringBuffer = new StringBuffer(512) ;
        jsonStringBuffer.append("{\"statusCode\":\"").append(statusCode).append("\"").
            append(",\"statusMessage\":\"").append(getStatusMessage()).append("\"}") ;
        return jsonStringBuffer.toString() ;
    }
    public static UMStatus getStatus(int statusCode)
    {
        UMStatus[] status = UMStatus.values() ;
        for (int i = 0 ; i < status.length ; i ++)
        {
            if (status[i].statusCode == statusCode)
                return status[i] ;
        }
        return null ;
    }
}
