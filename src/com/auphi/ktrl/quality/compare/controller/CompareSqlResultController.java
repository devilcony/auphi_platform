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
import com.auphi.ktrl.quality.compare.domain.CompareSqlResult;
import com.auphi.ktrl.quality.compare.service.CompareSqlColumnService;
import com.auphi.ktrl.quality.compare.service.CompareSqlResultService;
import com.auphi.ktrl.quality.compare.service.CompareSqlService;
import com.auphi.ktrl.schedule.template.TemplateUtil;
import com.auphi.ktrl.util.StringUtil;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @auther Tony
 * @create 2017-02-11 10:32
 */

@Controller
@RequestMapping(value = "compareSqlResult")
public class CompareSqlResultController extends BaseMultiActionController {

    @Autowired
    private CompareSqlResultService mCompareSqlResultService;

    @Autowired
    private CompareSqlColumnService mCompareSqlColumnService;

    @Autowired
    private CompareSqlService mCompareSqlService;

    private final static String INDEX = "admin/compare/compareSqlResult";


    @RequestMapping(value = {"index", ""})
    public ModelAndView index(HttpServletRequest req, HttpServletResponse resp){
        return new ModelAndView(INDEX);
    }


    /**
     * 执行数据统计
     * @param resp
     * @param renovate
     * @return
     * @throws IOException
     */
    @RequestMapping(value = {"listResult", ""})
    public String listResult(HttpServletResponse resp, CompareSqlResult sqlResult, boolean renovate) throws IOException {

        try{

            if(renovate){
                Date date = new Date();
                List<CompareSql> compareSqls =  mCompareSqlService.findListByCompareSqlResult(sqlResult);
                for(CompareSql compareSql:compareSqls){
                    execCompareSql(compareSql);
                }
            }
            PaginationSupport<CompareSqlResult> page = mCompareSqlResultService.findPage(sqlResult);
            String jsonString = JsonHelper.encodeObject2Json(page);
            write(jsonString, resp);

        }catch(Exception e){

            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    private void execCompareSql(CompareSql compareSql) throws SQLException {
        ResultSet resultSet = null;
        ResultSet referenceResultSet = null;

        Database testDatabase = null;

        Database referenceDatabase = null;

        try{

            testDatabase = createDatabase(compareSql.getDatabaseId());
            referenceDatabase =  createDatabase(compareSql.getReferenceDbId());

            String sql= compareSql.getSql();
            String referenceSql = compareSql.getReferenceSql();
            if(testDatabase!=null && referenceDatabase!= null   && compareSql!=null && sql != null && referenceSql !=null && !"".equals(sql) && !"".equals(referenceSql)){
                sql = TemplateUtil.replaceVariable(sql, new Date(), true);
                logger.info("sql:"+sql);

                referenceSql = TemplateUtil.replaceVariable(referenceSql, new Date(), true);
                logger.info("referenceSql:"+referenceSql);

                List<Map<String,String>> resultSetTmps = new ArrayList<>();
                List<Map<String,String>> referenceResultSetTmps = new ArrayList<>();
                if(compareSql.getCompareSqlColumns()!=null && !compareSql.getCompareSqlColumns().isEmpty()){
                    resultSet  = testDatabase.openQuery(sql);//sql执行结果
                    while(resultSet.next()){
                        for(CompareSqlColumn compareSqlColumn: compareSql.getCompareSqlColumns()){
                            if(compareSqlColumn.getColumnName()!=null && !"".equals(compareSqlColumn.getColumnName())){
                                Map<String,String> tmp = new HashMap<>();
                                tmp.put(compareSqlColumn.getColumnName(),resultSet.getString(compareSqlColumn.getColumnName()));
                                resultSetTmps.add(tmp);
                            }
                        }
                    }
                    referenceResultSet  = referenceDatabase.openQuery(referenceSql);//参考sql执行结果
                    while(referenceResultSet.next()){
                        for(CompareSqlColumn compareSqlColumn: compareSql.getCompareSqlColumns()){
                            if(compareSqlColumn.getReferenceColumnName()!=null && !"".equals(compareSqlColumn.getReferenceColumnName())){
                                Map<String,String> tmp = new HashMap<>();
                                tmp.put(compareSqlColumn.getReferenceColumnName(),referenceResultSet.getString(compareSqlColumn.getReferenceColumnName()));
                                referenceResultSetTmps.add(tmp);
                            }

                        }
                    }

                    //如果返回结果长度不一致着去多了一方循环
                    int count = resultSetTmps.size() > referenceResultSetTmps.size()?resultSetTmps.size():referenceResultSetTmps.size();

                    for(int i = 0 ; i < count;i++ ){
                        //如果已经取完了 这返回null
                        Map<String,String> resultSetTmp = i > resultSetTmps.size() ? null : resultSetTmps.get(i);
                        Map<String,String> referenceResultSetTmp = i > referenceResultSetTmps.size()? null:  referenceResultSetTmps.get(i);
                        CompareSqlColumn compareSqlColumn = compareSql.getCompareSqlColumns().get(i);
                        CompareSqlResult compareSqlResult = new CompareSqlResult(compareSqlColumn);
                        compareSqlResult.setCreateTime(new Date());
                        compareSqlResult.setColumnValue(resultSetTmp == null ? null : resultSetTmp.get(compareSqlColumn.getColumnName()));
                        compareSqlResult.setReferenceColumnValue(referenceResultSetTmp ==null ? null:referenceResultSetTmp.get(compareSqlColumn.getColumnName()));
                        //判断通过标准
                        if(compareSqlColumn.getCompareStyle() == 0){//等于参照值
                            if(compareSqlResult.getColumnValue()!=null && compareSqlResult.getReferenceColumnValue()!=null && compareSqlResult.getColumnValue().equals(compareSqlResult.getReferenceColumnValue())){
                                compareSqlResult.setCompareResult(1);
                            } else if (compareSqlResult.getColumnValue() ==null  && compareSqlResult.getReferenceColumnValue()==null){
                                compareSqlResult.setCompareResult(1);
                            }else{
                                compareSqlResult.setCompareResult(0);
                            }

                        }else {
                            //参照值之间
                            if(StringUtil.isDouble(compareSqlResult.getColumnValue()) &&  StringUtil.isDouble(compareSqlResult.getReferenceColumnValue())){

                                double a = Double.parseDouble(compareSqlResult.getColumnValue());
                                double b = Double.parseDouble(compareSqlResult.getReferenceColumnValue()) * compareSqlColumn.getMinRatio();
                                double c = Double.parseDouble(compareSqlResult.getReferenceColumnValue()) * compareSqlColumn.getMaxRatio();
                                if(a >=  b && a <= c){
                                    compareSqlResult.setCompareResult(1);
                                }else{
                                    compareSqlResult.setCompareResult(0);
                                }
                            }

                        }


                        mCompareSqlResultService.save(compareSqlResult);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            logger.error(e);
        }finally {

            if(testDatabase!=null){
                try {
                    testDatabase.closeConnectionOnly();
                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }
            }

            if(referenceDatabase !=null){
                try {
                    referenceDatabase.closeConnectionOnly();
                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }
            }

            if(referenceResultSet!=null){
                referenceResultSet.close();
            }
            if(resultSet !=null){
                resultSet.close();
            }
        }
    }



}
