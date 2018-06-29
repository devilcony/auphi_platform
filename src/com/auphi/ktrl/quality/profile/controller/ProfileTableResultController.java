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
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.ktrl.quality.profile.domain.ProfileTable;
import com.auphi.ktrl.quality.profile.domain.ProfileTableColumn;
import com.auphi.ktrl.quality.profile.domain.ProfileTableResult;
import com.auphi.ktrl.quality.profile.service.ProfileTableColumnService;
import com.auphi.ktrl.quality.profile.service.ProfileTableResultService;
import com.auphi.ktrl.schedule.template.TemplateUtil;
import com.auphi.ktrl.schedule.util.MarketUtil;
import com.auphi.ktrl.util.StringUtil;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @auther Tony
 * @create 2017-02-08 22:26
 */
@ApiIgnore
@Controller
@RequestMapping(value = "profileTableResult")
public class ProfileTableResultController extends BaseMultiActionController {

    private final static String INDEX = "admin/profile/profileResult";

    @Autowired
    private ProfileTableResultService mProfileTableResultService;

    @Autowired
    private ProfileTableColumnService mProfileTableColumnService;

    @RequestMapping(value = {"index", ""})
    public ModelAndView index(HttpServletRequest req, HttpServletResponse resp){
        return new ModelAndView(INDEX);
    }

    String[] needAvgTypes = {"NUMBER","INT","FLOAT","INTEGER","DOUBLE"};

    /**
     * 统计页面
     * @param req
     * @param resp
     * @param model
     * @return
     */
    @RequestMapping("/dashboard")
    public String dashboard(HttpServletRequest req, HttpServletResponse resp, Model model){

        String type = ServletRequestUtils.getStringParameter(req,"type","nullValueDashboard");
        String profileTableId = ServletRequestUtils.getStringParameter(req,"profileTableId","");
        String profileTableColumnId = ServletRequestUtils.getStringParameter(req,"profileTableColumnId","");
        model.addAttribute("profileTableId",profileTableId);
        model.addAttribute("profileTableColumnId",profileTableColumnId);


        return "admin/profile/"+type;
    }


    /**
     * 获取 nullValueDashboard 数据
     * @param resp
     * @param profileResult
     * @return
     */
    @RequestMapping("/getNullValueDashboardData")
    public String getNullValueDashboardData(HttpServletResponse resp, ProfileTableResult profileResult){

        try {
            profileResult.setLimit(null);
            PaginationSupport<ProfileTableResult> page = mProfileTableResultService.findPage(profileResult);

            List<String> categories = new ArrayList<>();
            List<Integer> datas = new ArrayList<Integer>();
            Map<String,Object> map = new HashMap<>();

            if(page!=null && page.getRows()!=null){

                for(ProfileTableResult result:page.getRows()){
                    categories.add(result.getProfileTableColumn().getProfileTableColumnName());

                    int zeroCount = result.getIndicatorZeroCount() == null ? 0 : result.getIndicatorZeroCount();
                    int nullCount = result.getIndicatorNullCount() == null ? 0 : result.getIndicatorNullCount();
                    datas.add(zeroCount+nullCount);
                }
            }

            map.put("categories",categories);
            map.put("datas",datas);

            String jsonString = JsonHelper.encodeObject2Json(map);
            write(jsonString, resp);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取柱状图
     * @param resp
     * @param profileResult
     * @return
     */
    @RequestMapping("/getHistoryData")
    public String getHistoryData(HttpServletResponse resp, ProfileTableResult profileResult){
        try {
            List<ProfileTableResult> list = mProfileTableResultService.findHistoryList(profileResult);
            List<String> times = new ArrayList<>();
            List<Integer> allCounts = new ArrayList<>();
            List<Integer> nullCounts = new ArrayList<>();
            List<Double> percentages = new ArrayList<>();

            if(list!=null && !list.isEmpty()){
                for(ProfileTableResult result:list){
                    times.add(StringUtil.DateToString(result.getCreateTime(),"yy/MM/dd") );
                    allCounts.add(result.getIndicatorAllCount());
                    nullCounts.add(result.getIndicatorNullCount());

                    int indicatorAllCount = result.getIndicatorAllCount();
                    int indicatorNullCount = result.getIndicatorNullCount();
                    double percentage = indicatorAllCount == 0 ? 0 : (double)indicatorNullCount/(double)indicatorAllCount *100;
                    BigDecimal bg = new BigDecimal(percentage).setScale(2, RoundingMode.UP);
                    percentages.add(bg.doubleValue());
                }
            }

            Map<String,Object> map = new HashMap<>();
            map.put("times",times);
            map.put("allCounts",allCounts);
            map.put("nullCounts",nullCounts);
            map.put("percentages",percentages);
            String jsonString = JsonHelper.encodeObject2Json(map);
            write(jsonString, resp);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 数据库表中该值的数据行
     * @param resp
     * @param profileTableColumn
     * @return
     */
    @RequestMapping("/getTableData")
    public String getTableData(HttpServletRequest req,HttpServletResponse resp, ProfileTableColumn profileTableColumn) throws IOException {
        Database database = null;
        Map<String,Object> items = new HashMap<>();
        String value = ServletRequestUtils.getStringParameter(req,"value",null);
        List<Map<String,Object>> columns = new ArrayList<>();
        List<Map<String,Object>> datas = new ArrayList<>();
        try{
            profileTableColumn = mProfileTableColumnService.get(profileTableColumn);

            if(profileTableColumn!=null){
                ProfileTable table = profileTableColumn.getProfileTable();
                if(table!=null && table.getDatabaseId()!=null){
                    String tableName = table.getSqlTableName();

                    StringBuffer sql = new StringBuffer("SELECT * FROM ");
                    sql.append(tableName);
                    if(value !=null && !value.isEmpty()){
                        sql.append(" WHERE ").append(profileTableColumn.getProfileTableColumnName()).append(" = '").append(value).append("'");
                    }
                    database = MarketUtil.getDatabase(table.getDatabaseId());
                    database.connect();
                    database.setQueryLimit(100);
                    ResultSet colRet = database.openQuery(sql.toString());
                    int cols = colRet.getMetaData().getColumnCount();

                    //表头
                    for(int i = 1;i<cols;i++){
                        Map<String,Object> column = new HashMap<>();
                        String columnName = colRet.getMetaData().getColumnName(i);

                        column.put("field",columnName);
                        column.put("title",columnName);

                        columns.add(column);
                    }

                    //表数据
                    while(colRet.next()){
                        Map<String,Object> data = new HashMap<>();
                        for(int i = 1;i<cols;i++){
                            String columnName = colRet.getMetaData().getColumnName(i);
                            data.put(columnName,colRet.getString(i));
                        }
                        datas.add(data);
                    }

                    items.put("columns",columns);
                    items.put("datas",datas);

                    String jsonString = JsonHelper.encodeObject2Json(items);
                    write(jsonString, resp);
                }

            }

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;


    }


    @RequestMapping("/getDifferentValueData")
    public String getDifferentValueData(HttpServletResponse resp, ProfileTableColumn profileTableColumn){
        Database database = null;
        List<Map<String,Object>> items = new ArrayList<>();
        List<String> names = new ArrayList<>();
        try {
            profileTableColumn = mProfileTableColumnService.get(profileTableColumn);

            if(profileTableColumn!=null){
                ProfileTable table = profileTableColumn.getProfileTable();
                if(table!=null && table.getDatabaseId()!=null){
                    String tableName = table.getSqlTableName();

                    StringBuffer sql = new StringBuffer("SELECT ");
                    sql.append(profileTableColumn.getProfileTableColumnName()).append(" AS columnName, COUNT(1) AS columnCount FROM ");
                    sql.append(tableName);
                    if(table.getCondition()!=null && !"".equals(table.getCondition()) ){
                        sql.append(" WHERE ").append(table.getCondition());
                    }
                    sql.append(" GROUP BY ").append(profileTableColumn.getProfileTableColumnName());
                    sql.append(" ORDER BY COUNT(1) ");

                    database = MarketUtil.getDatabase(table.getDatabaseId());
                    database.connect();
                    ResultSet colRet = database.openQuery(sql.toString());

                    int i = 0;
                    int v = 0;
                    while(colRet.next()){
                        if(i<10){
                            Map<String,Object> map = new HashMap<>();
                            String columnName = colRet.getString("columnName");
                            map.put("name",columnName);
                            map.put("value",colRet.getString("columnCount"));
                            items.add(map);
                            names.add(columnName);
                        }else{

                            Integer columnCount = colRet.getInt("columnCount");
                            v = v + (columnCount == null ? 0 : columnCount);
                        }
                        i++;
                    }
                    if(v>0){
                        Map<String,Object> map = new HashMap<>();
                        map.put("name","其他");
                        map.put("value",v);
                        items.add(map);
                        names.add("其他");
                    }

                    Map<String,Object> map = new HashMap<>();
                    map.put("names",names);
                    map.put("datas",items);

                    String jsonString = JsonHelper.encodeObject2Json(map);
                    write(jsonString, resp);
            }


                }
        }catch (Exception e) {
                e.printStackTrace();
        }
            return null;
    }






    /**
     *
     * 详情
     * @param resp
     * @param profileResult
     * @return
     * @throws IOException
     */

    @RequestMapping("/details")
    public String details( HttpServletResponse resp, ProfileTableResult profileResult) throws IOException {

        try {
            List<ProfileTableResult> list = mProfileTableResultService.findList(profileResult);
            Map<String,Object> map = new HashMap<>();
            List<Map<String,Object>>  datas = new ArrayList<>();
            List<Map<String,Object>>  columnModle = new ArrayList<>();
            List<Map<String,Object>>  fieldsNames = new ArrayList<>();

            Map<String,Object> xh = new HashMap();
            xh.put("header","");
            xh.put("dataIndex","number");
            xh.put("width",80);
            xh.put("locked",true);
            columnModle.add(xh);

            Map<String,Object> number = new HashMap();
            number.put("name","number");
            fieldsNames.add(number);


            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                number = new HashMap();
                number.put("name",fieldName);
                fieldsNames.add(number);

                xh = new HashMap();
                xh.put("header",result.getProfileTableColumn().getProfileTableColumnName());
                xh.put("dataIndex",fieldName);
                columnModle.add(xh);
            }


            Map<String,Object> data = new HashMap();
            data.put("number","类型");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorDataType());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","长度");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorDataLength());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","精度");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorDataPrecision());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","小数长度");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorDataScale());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","总数");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorAllCount());
            }
            datas.add(data);


            data = new HashMap();
            data.put("number","不同值数");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorDistinctCount());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","空值数");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorNullCount());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","零个数");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorZeroCount());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","平均值");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorAggAvg());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","最大值");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorAggMax());
            }
            datas.add(data);

            data = new HashMap();
            data.put("number","最小值");
            for(int i = 0 ; i < list.size(); i++){
                ProfileTableResult result =  list.get(i);
                String  fieldName = "field"+i;
                data.put(fieldName,result.getIndicatorAggMin());
            }
            datas.add(data);


            map.put("data",datas);
            map.put("columnModle",columnModle);
            map.put("fieldsNames",fieldsNames);

            String jsonString = JsonHelper.encodeObject2Json(map);

            write(jsonString, resp);
        }catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }

        return null;
    }


    /**
     * 执行数据统计
     * @param resp
     * @param renovate
     * @return
     * @throws IOException
     */
    @RequestMapping("/listResult")
    public String listResult(HttpServletResponse resp,ProfileTableResult profileResult,boolean renovate) throws IOException {

        Database database = null;
        Map<Integer,List<ProfileTableColumn>> maps = new HashMap<>();
        try{

            //renovate = true时重新执行
            if(renovate){
                Date date = new Date();
               List<ProfileTableColumn> columns =  mProfileTableColumnService.findListByProfileTableResult(profileResult);
                //先将所有的TableColumn g根据 database_Id  分组
                for(ProfileTableColumn column:columns){
                    if(maps.get(column.getProfileTable().getDatabaseId()) ==null){
                        List<ProfileTableColumn> list = new ArrayList<>();
                        list.add(column);
                        maps.put(column.getProfileTable().getDatabaseId(),list);
                    }else{
                        maps.get(column.getProfileTable().getDatabaseId()).add(column);
                    }
                }
                //开始执行
                for (Integer databaseId : maps.keySet()) {
                    database  = createDatabase(databaseId);
                    List<ProfileTableColumn> runs = maps.get(databaseId);
                    for(ProfileTableColumn column:runs){
                        ProfileTableResult result = null;
                        if(database!=null){
                            result = execProfileSQl(column,database);
                        }
                        if(result!=null ){
                            result.setCreateTime(date);
                            mProfileTableResultService.save(result);
                        }
                    }

                    database.closeConnectionOnly();
                }


            }
            PaginationSupport<ProfileTableResult> page = mProfileTableResultService.findPage(profileResult);
            String jsonString = JsonHelper.encodeObject2Json(page);
            write(jsonString, resp);

        } catch(Exception e){
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
        }
        return null;
    }

    /**
     * 执行sql
     * @param column
     * @param database
     * @return
     */
    private ProfileTableResult execProfileSQl(ProfileTableColumn column, Database database)  {
        ResultSet colRet = null;
        ResultSet resultSet = null;
        try{
            ProfileTableResult profileTableResult = new ProfileTableResult();
            profileTableResult.setProfileTableColumn(column);



            DatabaseMetaData m_DBMetaData = database.getConnection().getMetaData();

            String schemaName = column.getProfileTable().getSchemaName();
            String tableName = column.getProfileTable().getTableName();
            String columnName = column.getProfileTableColumnName();

            colRet = m_DBMetaData.getColumns(null,schemaName ==null ? null: schemaName.trim(), tableName.trim(), columnName==null?null:columnName.trim() );
            while(colRet.next()){
                String colName = colRet.getString("COLUMN_NAME");//列名
                if(colName!=null && colName.equals(column.getProfileTableColumnName())){
                    profileTableResult.setIndicatorDataLength(colRet.getInt("COLUMN_SIZE"));
                    profileTableResult.setIndicatorDataType(colRet.getString("TYPE_NAME"));//类型名称
                    profileTableResult.setIndicatorDataPrecision(colRet.getInt("COLUMN_SIZE"));//精度
                    profileTableResult.setIndicatorDataScale(colRet.getInt("DECIMAL_DIGITS"));// 小数的位数
                }
            }

            String sql = spliceSQl(column,profileTableResult);
            System.out.print(sql);

            profileTableResult.setExecuteSql(sql);
            if(database!=null && sql!=null && !"".equals(sql)){
                try {
                    resultSet  = database.openQuery(sql);
                    while(resultSet.next()){
                        profileTableResult.setIndicatorAllCount(resultSet.getInt("indicatorAllCount"));
                        profileTableResult.setIndicatorNullCount(resultSet.getInt("indicatorNullCount"));
                        profileTableResult.setIndicatorZeroCount(resultSet.getInt("indicatorZeroCount"));
                        profileTableResult.setIndicatorAggAvg(resultSet.getString("indicatorAggAvg"));
                        profileTableResult.setIndicatorAggMax(resultSet.getString("indicatorAggMax"));
                        profileTableResult.setIndicatorAggMin(resultSet.getString("indicatorAggMin"));
                        profileTableResult.setIndicatorDistinctCount(resultSet.getInt("indicatorDistinctCount"));

                        return profileTableResult;
                    }



                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }finally {
                    if(colRet!=null){
                        colRet.close();
                    }
                    if(resultSet!=null){
                        resultSet.close();
                    }
                }
            }

            return profileTableResult;
        } catch(Exception e){
            e.printStackTrace();
            logger.error(e);
            return null;
        }

    }


    /**
     * 生成sql
     * @param column
     * @param profileTableResult
     * @return
     * @throws Exception
     */
    private String spliceSQl(ProfileTableColumn column, ProfileTableResult profileTableResult) throws Exception {



        String tableName = column.getProfileTable().getSqlTableName();
        String columnName = column.getProfileTableColumnName();

        if(tableName!=null && columnName!=null && !"".equals(tableName) && !"".equals(columnName)){
            StringBuffer sql = new StringBuffer("SELECT count(1) AS indicatorAllCount, ");
            sql.append(" (SELECT count(1) FROM ").append(tableName).append(" A WHERE A.").append(columnName).append(" is null "+ getConditionSql("AND",column) +") AS indicatorNullCount , ");
            if(needAvg(profileTableResult.getIndicatorDataType())){
                sql.append(" (SELECT count(1) FROM ").append(tableName).append(" B WHERE B.").append(columnName).append(" = 0 "+ getConditionSql("AND",column) +") AS indicatorZeroCount, ");
            }else {
                sql.append(" 0 AS indicatorZeroCount, ");
            }


            sql.append(" COUNT(DISTINCT(").append(column.getProfileTableColumnName()).append(")) AS indicatorDistinctCount, ");
            sql.append(" MAX(").append(columnName).append(") AS indicatorAggMax, ");
            sql.append(" MIN(").append(columnName).append(") AS indicatorAggMin, ");
            if(needAvg(profileTableResult.getIndicatorDataType())){
                sql.append(" AVG(").append(columnName).append(") AS indicatorAggAvg ");
            }else{
                sql.append(" 0 AS indicatorAggAvg ");
            }
            sql.append(" FROM ").append(tableName).append(" T ").append(getConditionSql(" WHERE ",column));

            return TemplateUtil.replaceVariable(sql.toString(), new Date(), true);
        }
        return null;

    }

    private String getConditionSql(String s, ProfileTableColumn column) {

        if(column.getProfileTable().getCondition()!=null && !"".equals(column.getProfileTable().getCondition())){
            return " "+s+" "+column.getProfileTable().getCondition();
        }

        return "";
    }


    private boolean needAvg(String indicatorDataType) {

        for(String type:needAvgTypes){
            if(type.equalsIgnoreCase(indicatorDataType)){
                return true;
            }

        }
        return false;
    }




}
