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
package com.auphi.ktrl.quality.profile.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.ktrl.quality.profile.domain.ProfileTable;
import com.auphi.ktrl.quality.profile.domain.ProfileTableColumn;
import com.auphi.ktrl.quality.profile.service.ProfileTableColumnService;
import com.auphi.ktrl.quality.profile.service.ProfileTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @auther Tony
 * @create 2017-02-09 08:47
 */
@Controller
@RequestMapping(value = "profileTable")
public class ProfileTableController extends BaseMultiActionController {


    @Autowired
    private ProfileTableService mProfileTableService;

    @Autowired
    private ProfileTableColumnService mProfileTableColumnService;


    @RequestMapping(value = {"getProfileTableList", ""})
    public String getProfileTableList(HttpServletRequest req, HttpServletResponse resp,ProfileTable profileTable) throws IOException {
        try{
           List<ProfileTable> list =  mProfileTableService.findList(profileTable);
            String jsonString =list==null?"[]": JsonHelper.encodeObject2Json(list);
            write(jsonString, resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"save", ""})
    public String save(HttpServletRequest req, HttpServletResponse resp,ProfileTable profileTable) throws IOException {
        try{
            if(profileTable.getTableNameTag() == 2  ){
                profileTable.setTableName(profileTable.getSql());
            }

            mProfileTableService.save(profileTable);


            this.setOkTipMsg("操作成功", resp);

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }
    @ResponseBody
    @RequestMapping(value = {"getProfileTable", ""})
    public ProfileTable  getProfileTable(HttpServletRequest req, HttpServletResponse resp,ProfileTable profileTable) throws IOException {
        try{
            profileTable = mProfileTableService.getProfileTable(profileTable);
            if(profileTable.getTableNameTag() == 2){
                profileTable.setSql(profileTable.getTableName());
                profileTable.setTableName(null);
            }
           // String jsonString = profileTable==null?"{}": JsonHelper.encodeObject2Json(profileTable);
            //write(jsonString, resp);
            return profileTable;

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"delete", ""})
    public String delete(ProfileTableColumn column, String dl, HttpServletResponse resp) throws IOException {
        try{

            if(column!=null && column.getIds()!=null && !"".equals(column.getIds())  ){
                List<ProfileTableColumn> columns = mProfileTableColumnService.findList(column);

                if("t".equals(dl)){// 删除
                    Set<ProfileTable> tables = new HashSet<>();
                    for(ProfileTableColumn ptc:columns ){
                        tables.add(ptc.getProfileTable());
                    }
                    for (ProfileTable table : tables) {
                        mProfileTableService.delete(table);
                    }

                }else if("c".equals(dl)){
                    for(ProfileTableColumn ptc:columns ){
                        mProfileTableColumnService.delete(ptc);
                    }
                }
            }

            this.setOkTipMsg("操作成功", resp);

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }


}
