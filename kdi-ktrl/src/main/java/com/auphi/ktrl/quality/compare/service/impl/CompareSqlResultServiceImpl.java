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
package com.auphi.ktrl.quality.compare.service.impl;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:31
 */

@Service("CompareSqlResultService")
public class CompareSqlResultServiceImpl implements CompareSqlResultService {


    @Autowired
    SystemDao systemDao;

    @Override
    public PaginationSupport<CompareSqlResult> findPage(CompareSqlResult sqlResult) {
        List<CompareSqlResult> items = systemDao.queryForList("compareSqlResult.findList", sqlResult);
        Integer total = (Integer)systemDao.queryForObject("compareSqlResult.queryCount",sqlResult);
        PaginationSupport<CompareSqlResult> page = new PaginationSupport<CompareSqlResult>(items, total);
        return page;
    }

    @Override
    public void save(CompareSqlResult compareSqlResult) {
        systemDao.save("compareSqlResult.insert",compareSqlResult);
    }
}
