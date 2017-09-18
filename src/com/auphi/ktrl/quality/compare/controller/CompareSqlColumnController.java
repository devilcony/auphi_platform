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
package com.auphi.ktrl.quality.compare.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.ktrl.quality.compare.domain.CompareSql;
import com.auphi.ktrl.quality.compare.domain.CompareSqlColumn;
import com.auphi.ktrl.quality.compare.service.CompareSqlColumnService;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import com.auphi.ktrl.schedule.template.TemplateUtil;
import com.auphi.ktrl.schedule.util.MarketUtil;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther Tony
 * @create 2017-02-11 10:31
 */

@Controller
@RequestMapping(value = "compareSqlColumn")
public class CompareSqlColumnController extends BaseMultiActionController {

    @Autowired
    private CompareSqlResultService mCompareSqlResultService;

    @Autowired
    private CompareSqlColumnService mCompareSqlColumnService;

    @Autowired
    private CompareSqlService mCompareSqlService;


    /**
     * 获取sql字段信息
     * @param resp
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"getSqlColumn", ""})
    public String getSqlColumn(HttpServletResponse resp, CompareSql compareSql) throws IOException {
        Database database = null;
        Database reference = null;
        try{
            if(compareSql!=null && compareSql.getDatabaseId()!=null && compareSql.getReferenceDbId()!=null){

                database = MarketUtil.getDatabase(compareSql.getDatabaseId());
                database.connect();

                reference = MarketUtil.getDatabase(compareSql.getReferenceDbId());
                reference.connect();

                List<CompareSqlColumn> list = new ArrayList<>();


                ResultSet sqlSet  = database.openQuery(TemplateUtil.replaceVariable(compareSql.getSql(),new Date(),true));



                ResultSet referenceSqlSet  = reference.openQuery(TemplateUtil.replaceVariable(compareSql.getReferenceSql(),new Date(),true));

                ResultSetMetaData sqlSetData = sqlSet.getMetaData();
                ResultSetMetaData referenceSqlSetData = referenceSqlSet.getMetaData();

                int cols = sqlSetData.getColumnCount();
                int cols2 = referenceSqlSetData.getColumnCount();

                if(cols == cols2){
                    for (int i = 1; i <= cols; i++) {

                        CompareSqlColumn compareSqlColumn = new CompareSqlColumn();

                        String columnName = sqlSetData.getColumnName(i);
                        String  referenceColumnName = referenceSqlSetData.getColumnName(i);

                        compareSqlColumn.setColumnName(columnName);
                        compareSqlColumn.setReferenceColumnName(referenceColumnName);

                        String columnType = sqlSetData.getColumnTypeName(i);
                        String  referenceColumnType = referenceSqlSetData.getColumnTypeName(i);

                        if(columnType.equals(referenceColumnType)){
                            compareSqlColumn.setColumnType(columnType);
                        }else{
                            this.setFailTipMsg("字段返回类型不一致 "+columnName+"->"+columnType+";"+referenceColumnName+"->"+referenceColumnType, resp);
                            return null;
                        }

                        list.add(compareSqlColumn);
                    }


                    PaginationSupport<CompareSqlColumn> page = new PaginationSupport<CompareSqlColumn>(list, list.size());
                    String jsonString = JsonHelper.encodeObject2Json(page);
                    write(jsonString,resp);

                }else{
                    this.setFailTipMsg("两条sql返回的字段数量不一致", resp);
                }

            }

        }catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }finally{
            if(database!=null){
                try {
                    database.closeConnectionOnly();
                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }
            }
            if(reference!=null){
                try {
                    reference.closeConnectionOnly();
                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;


    }
}
