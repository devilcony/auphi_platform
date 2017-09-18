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
package com.auphi.ktrl.quality.profile.service.impl;

import com.auphi.data.hub.dao.SystemDao;
import com.auphi.ktrl.quality.profile.domain.ProfileTable;
import com.auphi.ktrl.quality.profile.domain.ProfileTableColumn;
import com.auphi.ktrl.quality.profile.service.ProfileTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-09 08:47
 */
@Service("ProfileTableService")
public class ProfileTableServiceImpl implements ProfileTableService {

    @Autowired
    SystemDao systemDao;

    @Override
    public List<ProfileTable> findList(ProfileTable dto) {

        return this.systemDao.queryForList("profileTable.findList", dto);
    }

    @Override
    public void save(ProfileTable profileTable) {

        if(profileTable.getProfileTableId()!=null && profileTable.getProfileTableId() > 0){
            systemDao.update("profileTable.update", profileTable);
            systemDao.delete("profileTableColumn.deleteByProfileTable", new ProfileTableColumn(profileTable));

        }else{
            Integer id = (Integer) this.systemDao.queryForObject("profileTable.getMaxId",profileTable);
            if(id == null) id = 1;
            else id = id+1;
            profileTable.setProfileTableId(id);
            profileTable.setCreateTime(new Date());
            systemDao.save("profileTable.insert", profileTable);
        }


        if(profileTable.getProfileTableColumns()!=null && !profileTable.getProfileTableColumns().isEmpty()){
            for(int i = 0 ; i < profileTable.getProfileTableColumns().size();i++){
                ProfileTableColumn profileTableColumn = profileTable.getProfileTableColumns().get(i);
                profileTableColumn.setProfileTable(profileTable);
                profileTableColumn.setProfileTableColumnOrder(i);
                systemDao.save("profileTableColumn.insert", profileTableColumn);
            }
        }
    }

    @Override
    public ProfileTable getProfileTable(ProfileTable profileTable) {
        ProfileTable obj =  (ProfileTable)this.systemDao.queryForObject("profileTable.get",profileTable);
        if(obj!=null){
            List<ProfileTableColumn> list = systemDao.queryForList("profileTableColumn.findList",new ProfileTableColumn(obj));
            obj.setProfileTableColumns(list);
        }

        return obj;
    }

    @Override
    public void delete(ProfileTable table) {
        systemDao.delete("profileTable.delete", table);
        systemDao.delete("profileTableColumn.deleteByProfileTable", new ProfileTableColumn(table));
    }
}
