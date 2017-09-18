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

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.ktrl.quality.profile.domain.ProfileTableResult;
import com.auphi.ktrl.quality.profile.service.ProfileTableResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-08 22:25
 */
@Service("ProfileTableResultService")
public class ProfileTableResultServiceImpl implements ProfileTableResultService {

    @Autowired
    SystemDao systemDao;

    @Override
    public PaginationSupport<ProfileTableResult> findPage(ProfileTableResult result) throws SQLException {

        List<ProfileTableResult> items = systemDao.queryForList("profileTableResult.findList", result);
        Integer total = (Integer)systemDao.queryForObject("profileTableResult.queryCount",result);
        PaginationSupport<ProfileTableResult> page = new PaginationSupport<ProfileTableResult>(items, total);
        return page;
    }

    @Override
    public void save(ProfileTableResult result) {
        systemDao.save("profileTableResult.insert",result);
    }

    @Override
    public List<ProfileTableResult> findList(ProfileTableResult profileResult) {
        return systemDao.queryForList("profileTableResult.findList", profileResult);
    }

    @Override
    public List<ProfileTableResult> findHistoryList(ProfileTableResult profileResult) {
        return systemDao.queryForList("profileTableResult.findHistoryList", profileResult);
    }
}
