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
import com.auphi.ktrl.quality.profile.domain.ProfileTableColumn;
import com.auphi.ktrl.quality.profile.domain.ProfileTableResult;
import com.auphi.ktrl.quality.profile.service.ProfileTableColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-09 08:49
 */
@Service("ProfileTableColumnService")
public class ProfileTableColumnServiceImpl implements ProfileTableColumnService {


    @Autowired
    SystemDao systemDao;

    @Override
    public List<ProfileTableColumn> findListByProfileTableResult(ProfileTableResult result) {
        return systemDao.queryForList("profileTableColumn.findListByProfileTableResult",result);
    }

    @Override
    public List<ProfileTableColumn> findList(ProfileTableColumn profileTableColumn) {

        return systemDao.queryForList("profileTableColumn.findList",profileTableColumn);
    }

    @Override
    public void delete(ProfileTableColumn profileTableColumn) {
        systemDao.delete("profileTableColumn.delete",profileTableColumn);
    }

    @Override
    public ProfileTableColumn get(ProfileTableColumn profileTableColumn) {
        return (ProfileTableColumn) systemDao.queryForObject("profileTableColumn.get",profileTableColumn);
    }
}
