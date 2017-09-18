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
import com.auphi.ktrl.quality.profile.domain.ProfileTableGroup;
import com.auphi.ktrl.quality.profile.service.ProfileTableGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-08 22:30
 */
@Service("ProfileTableGroupService")
public class ProfileTableGroupServiceImpl implements ProfileTableGroupService {

    @Autowired
    SystemDao systemDao;

    @Override
    public List<ProfileTableGroup> findList(ProfileTableGroup group) {
        return systemDao.queryForList("profileTableGroup.findList",group);
    }

    @Override
    public void save(ProfileTableGroup group) {
        if(group.getProfielTableGroupId()!=null && group.getProfielTableGroupId()>0){
            systemDao.update("profileTableGroup.update", group);
        }else{
            systemDao.save("profileTableGroup.insert", group);

        }


    }

    @Override
    public void deleteMulti(ProfileTableGroup group) {
        systemDao.delete("profileTableGroup.deleteMulti", group);
    }
}
