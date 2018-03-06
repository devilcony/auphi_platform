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

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.user.util.UMStatus;


public class LoginResponse
{
    private UMStatus status ;
    private String user_name ;
    private String user_id ;
    private long priviledges ;
    private List<RepositoryBean> rep_list ;
    
    
    public static String encode(LoginResponse lRps)
    {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("status", lRps.status.getStatusCode()) ;
        jsonObj.put("user_name", lRps.user_name) ;
        jsonObj.put("user_id", lRps.user_id) ;
        jsonObj.put("priviledges", Long.valueOf(lRps.priviledges)) ;
        jsonObj.put("rep_list", lRps.rep_list) ;
        
        if (lRps.rep_list != null)
        {
            //JSONObject repListObj = new JSONObject() ;
            JSONArray repListObj = new JSONArray() ;
            
            for (int i= 0 ; i < lRps.rep_list.size() ; i++)
            {
                JSONObject repObj = new JSONObject() ;
                RepositoryBean rep = lRps.rep_list.get(i) ;
                repObj.put("DBAccess", rep.getDbAccess()) ;
                repObj.put("DBHost", rep.getDbHost()) ;
                repObj.put("DBPort", rep.getDbPort()) ;
                repObj.put("DBName", rep.getDbName()) ;
                repObj.put("DBType",rep.getDbType()) ;
                repObj.put("user_name", rep.getUserName()) ;
                repObj.put("password", rep.getPassword()) ;
                repObj.put("version", rep.getVersion() ) ;
                repObj.put("rep_name", rep.getRepositoryName()) ;
                repObj.put("rep_ID", rep.getRepositoryID()) ;

                repListObj.add(repObj) ;
            }
            
            jsonObj.put("rep_list", repListObj) ;
        }
        else
            jsonObj.put("rep_list", null) ;
        
        return jsonObj.toString() ;
    }
    
    public static LoginResponse decode(String loginResponseJson)
    {
        LoginResponse lRps = new LoginResponse() ;

        JSONObject obj = (JSONObject)JSONValue.parse(loginResponseJson) ;
       
        lRps.setStatus(UMStatus.getStatus(Integer.parseInt(((Long)obj.get("status")).toString()))) ;
        lRps.setPriviledges((Long)(obj.get("priviledges"))) ;
        lRps.setUser_id((String)obj.get("user_id")) ;
        lRps.setUser_name((String)obj.get("user_name")) ;
        JSONArray repListObj = (JSONArray)obj.get("rep_list") ;
        
        if (repListObj != null)
        {
            ArrayList<RepositoryBean> repList = new ArrayList<RepositoryBean>() ;
            for(int i = 0 ; i < repListObj.size() ; i ++)
            {
                JSONObject repObj = (JSONObject)repListObj.get(i) ;
                RepositoryBean repBean = new RepositoryBean() ;
                
                repBean.setDbAccess((String)repObj.get("DBAccess")) ;
                repBean.setDbHost((String)repObj.get("DBHost")) ;
                repBean.setDbPort((String)repObj.get("DBPort")) ;
                repBean.setDbName((String)repObj.get("DBName")) ;
                repBean.setDbType((String)repObj.get("DBType")) ;
                repBean.setUserName((String)repObj.get("user_name")) ;
                repBean.setPassword((String)repObj.get("password")) ;
                repBean.setVersion((String)repObj.get("version")) ;
                repBean.setRepositoryName((String)repObj.get("rep_name")) ;
                repBean.setRepositoryID(Integer.parseInt(((Long)repObj.get("rep_ID")).toString())) ;
                
                repList.add(repBean) ;
            }
            lRps.setRep_list(repList) ;
        }
        
        
        return lRps ;
    }

    public String getUser_name()
    {
        return user_name;
    }

    public void setUser_name(String user_name)
    {
        this.user_name = user_name;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public long getPriviledges()
    {
        return priviledges;
    }

    public void setPriviledges(long priviledges)
    {
        this.priviledges = priviledges;
    }

    public List<RepositoryBean> getRep_list()
    {
        return rep_list;
    }

    public void setRep_list(List<RepositoryBean> rep_list)
    {
        this.rep_list = rep_list;
    }
    
    public String toJSONString()
    {
        return encode(this) ;
    }

    public UMStatus getStatus()
    {
        return status;
    }

    public void setStatus(UMStatus status)
    {
        this.status = status;
    }
}
