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
import com.auphi.ktrl.quality.compare.domain.CompareSql;
import com.auphi.ktrl.quality.compare.domain.CompareSqlColumn;
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:30
 */
@Service("CompareSqlService")
public class CompareSqlServiceImpl implements CompareSqlService {

    @Autowired
    SystemDao systemDao;

    @Override
    public List<CompareSql> findListByCompareSqlResult(CompareSqlResult sqlResult) {

        List<CompareSql> list = systemDao.queryForList("compareSql.findListByCompareSqlResult",sqlResult);

        for(CompareSql sql:list){

            CompareSqlColumn compareSqlColumn = new CompareSqlColumn(sql);


            sql.setCompareSqlColumns( systemDao.queryForList("compareSqlColumn.findList",new CompareSqlColumn(sql)));
        }

        return list;
    }

    @Override
    public void save(CompareSql compareSql) {

        if(compareSql.getCompareSqlId()!=null && compareSql.getCompareSqlId() > 0){
            systemDao.save("compareSql.update", compareSql);
            systemDao.delete("compareSqlColumn.deleteByCompareSql", new CompareSqlColumn(compareSql) );

        }else{
            Integer nextId = (Integer) this.systemDao.queryForObject("compareSql.getMaxId",compareSql);
            if(nextId == null) nextId = 1;
            else nextId = nextId+1;
            compareSql.setCompareSqlId(nextId);
            compareSql.setCreateTime(new Date());
            systemDao.save("compareSql.insert", compareSql);
        }

        if(compareSql.getCompareSqlColumns()!=null && !compareSql.getCompareSqlColumns().isEmpty()){
            for(int i = 0 ; i < compareSql.getCompareSqlColumns().size();i++){
                CompareSqlColumn compareSqlColumn = compareSql.getCompareSqlColumns().get(i);
                compareSqlColumn.setCompareSql(compareSql);
                if(compareSqlColumn.getCompareStyle() ==null){
                    compareSqlColumn.setCompareStyle(0);
                }
                systemDao.save("compareSqlColumn.insert", compareSqlColumn);
            }
        }

    }

    @Override
    public List<CompareSql> findAllList(CompareSql compareSql) {
        List<CompareSql> list = systemDao.queryForList("compareSql.findAllList",compareSql);

        return list;
    }

    @Override
    public List<CompareSql> findComboList(CompareSql compareSql) {
        List<CompareSql> list = systemDao.queryForList("compareSql.findComboList",compareSql);

        return list;
    }

    @Override
    public CompareSql getCompareSql(CompareSql compareSql) {

        CompareSql compare = (CompareSql) systemDao.queryForObject("compareSql.get",compareSql);
        if(compare != null){
            List<CompareSqlColumn> compareSqlColumns = systemDao.queryForList("compareSqlColumn.findList",new CompareSqlColumn(compare));
            compare.setCompareSqlColumns(compareSqlColumns);
        }

        return compare;
    }

    @Override
    public void delete(CompareSql compareSql) {
        systemDao.queryForList("compareSql.delete",compareSql);
        systemDao.queryForList("compareSqlColumn.deleteByCompareSql",new CompareSqlColumn(compareSql));
    }
}
