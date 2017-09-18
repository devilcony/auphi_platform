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
package com.auphi.ktrl.system.user.bean;

import java.sql.Timestamp;


public class UserBean
{
    private int user_id ;
    private String user_name ;
    private String password ;
    private String nick_name ;
    private String email ;
    private String mobilephone;
    private String description ;
    private int isSystemUser ;
    private int status ;
    private Timestamp lastLogin ;
    private int orgId;
    private boolean isAdmin ;
    private boolean isSuperAdmin ;
    
	public UserBean()
    {
        isSystemUser = 0 ;
    }

    public UserBean(int user_id, String user_name, String password,
            String nick_name, String email, String mobilephone,
            String description, int isSystemUser, int status, Timestamp lastLogin)
    {
        this.user_id = user_id;
        this.user_name = user_name;
        this.password = password;
        this.nick_name = nick_name;
        this.email = email;
        this.mobilephone = mobilephone;
        this.description = description;
        this.isSystemUser = isSystemUser;
        this.status = status;
        this.lastLogin = lastLogin;
    }    
    
    public int getUser_id()
    {
        return user_id;
    }
    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }
    public void setUser_id(String user_id)
    {
        if (user_id == null)
            return ;
        this.user_id = Integer.parseInt(user_id) ;
    }
    public String getUser_name()
    {
        return user_name;
    }
    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getNick_name()
    {
        return nick_name;
    }
    public void setNick_name(String nick_name)
    {
        this.nick_name = nick_name;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public boolean isSystemUser()
    {
        return isSystemUser == 1;
    }
    public void setIsSystemUser(int isSystemUser)
    {
        this.isSystemUser = isSystemUser;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getMobilephone()
    {
        return mobilephone;
    }

    public void setMobilephone(String mobilephone)
    {
        this.mobilephone = mobilephone;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public Timestamp getLastLogin()
    {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public int getIsSystemUser() {
		return isSystemUser;
	}

	public int getOrgId() {
		return orgId;
	}

	public void setOrgId(int orgId) {
		this.orgId = orgId;
	}

	public boolean isSuperAdmin() {
		return isSuperAdmin;
	}

	public void setSuperAdmin(boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}
	
}
