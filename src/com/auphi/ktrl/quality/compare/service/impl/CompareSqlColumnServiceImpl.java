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

import com.auphi.data.hub.dao.SystemDao;
import com.auphi.ktrl.quality.compare.domain.CompareSqlColumn;
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.service.CompareSqlColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:31
 */
@Service("CompareSqlColumnService")
public class CompareSqlColumnServiceImpl implements CompareSqlColumnService {

    @Autowired
    SystemDao systemDao;

    @Override
    public List<CompareSqlColumn> findListByCompareSqlResult(CompareSqlResult sqlResult) {
        return systemDao.queryForList("compareSqlColumn.findListByCompareSqlResult",sqlResult);
    }

    @Override
    public void delete(CompareSqlColumn sqlColumn) {

        systemDao.delete("compareSqlColumn.delete", sqlColumn);


    }

    @Override
    public List<CompareSqlColumn> findList(CompareSqlColumn column) {

        return systemDao.queryForList("compareSqlColumn.findList",column);
    }
}
