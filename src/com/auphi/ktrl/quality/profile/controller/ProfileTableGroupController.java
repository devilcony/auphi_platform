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
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.ktrl.quality.profile.domain.ProfileTableGroup;
import com.auphi.ktrl.quality.profile.service.ProfileTableGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-08 22:29
 */
@ApiIgnore
@Controller
@RequestMapping(value = "profileTableGroup")
public class ProfileTableGroupController extends BaseMultiActionController {

    @Autowired
    private ProfileTableGroupService mProfileTableGroupService;


    @RequestMapping(value = {"getTableGroupList", ""})
    public String getTableGroupList(HttpServletRequest req, HttpServletResponse resp, ProfileTableGroup group) throws IOException {
        List<Dto> dataSourceList= new ArrayList<Dto>();
        try{
            List<ProfileTableGroup> list =  mProfileTableGroupService.findList(group);
            String jsonString =list==null?"[]": JsonHelper.encodeObject2Json(list);
            write(jsonString, resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"save", ""})
    public String save(HttpServletRequest req, HttpServletResponse resp, ProfileTableGroup group) throws IOException {
        try{
            mProfileTableGroupService.save(group);
            this.setOkTipMsg("操作成功", resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"delete", ""})
    public String delete(HttpServletRequest req, HttpServletResponse resp, ProfileTableGroup group) throws IOException {
        try{
            mProfileTableGroupService.deleteMulti(group);
            this.setOkTipMsg("操作成功", resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }


}
