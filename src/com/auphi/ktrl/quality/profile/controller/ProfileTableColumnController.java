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
import com.auphi.ktrl.quality.profile.service.ProfileTableColumnService;
import com.auphi.ktrl.schedule.util.MarketUtil;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Tony
 * @create 2017-02-09 08:49
 */
@Controller("profileTableColumn")
public class ProfileTableColumnController extends BaseMultiActionController {

    @Autowired
    private ProfileTableColumnService mProfileTableColumnService;


    @RequestMapping(value = {"getProfileTableColumnList", ""})
    public String getProfileTableColumnList(HttpServletRequest req, HttpServletResponse resp, ProfileTableColumn profileTableColumn) throws IOException {
        try{
            List<ProfileTableColumn> list =  mProfileTableColumnService.findList(profileTableColumn);
            String jsonString =list==null?"[]": JsonHelper.encodeObject2Json(list);
            write(jsonString, resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"listDistinct", ""})
    public String listDistinct(HttpServletRequest req, HttpServletResponse resp, ProfileTableColumn profileTableColumn) throws IOException {

        Database database = null;
        List<Map<String,Object>> items = new ArrayList<>();
        int limit = ServletRequestUtils.getIntParameter(req,"limit",50);
        String dir = ServletRequestUtils.getStringParameter(req,"dir","DESC");
        String value = ServletRequestUtils.getStringParameter(req,"value",null);
        try{
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
                        if(value !=null && !value.isEmpty()){
                            sql.append(" AND ").append(profileTableColumn.getProfileTableColumnName()).append(" = '").append(value).append("'");
                        }
                    }else{
                        if(value !=null && !value.isEmpty()){
                            sql.append(" WHERE ").append(profileTableColumn.getProfileTableColumnName()).append(" = '").append(value).append("'");
                        }
                    }
                    sql.append(" GROUP BY ").append(profileTableColumn.getProfileTableColumnName());
                    sql.append(" ORDER BY columnName ").append(dir);

                    database = MarketUtil.getDatabase(table.getDatabaseId());
                    database.connect();
                    database.setQueryLimit(limit);
                    ResultSet colRet = database.openQuery(sql.toString());

                    while(colRet.next()){
                        Map<String,Object> map = new HashMap<>();
                        map.put("columnName",colRet.getString("columnName"));
                        map.put("columnCount",colRet.getString("columnCount"));
                        items.add(map);
                    }

                    PaginationSupport<Map<String,Object>> page = new PaginationSupport<Map<String,Object>>(items, items.size());
                    String jsonString = JsonHelper.encodeObject2Json(page);
                    write(jsonString, resp);
                }

            }

        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }

    @RequestMapping(value = {"getTableColumnList", ""})
    public String getTableColumnList(HttpServletRequest req, HttpServletResponse resp, ProfileTable profileTable) throws IOException {
        Database database = null;
        List<Map<String,Object>> items = new ArrayList<>();
        try{

            if(profileTable!=null &&profileTable.getDatabaseId()!=null){
                database = MarketUtil.getDatabase(profileTable.getDatabaseId());
            }

            if(database!=null && profileTable.getTableName()!=null){
                database.connect();
                DatabaseMetaData m_DBMetaData = database.getConnection().getMetaData();
                if(profileTable.getTableNameTag() == 1){ //表


                    String schemaName = profileTable.getSchemaName() ==null ? null: profileTable.getSchemaName().trim();
                    String tableName = profileTable.getTableName()==null?null:profileTable.getTableName().trim();


                    ResultSet colRet = m_DBMetaData.getColumns(null,schemaName , tableName, "%");

                    while(colRet.next()){
                        Map<String,Object> map = new HashMap<>();
                        map.put("columnName",colRet.getString("COLUMN_NAME"));
                        map.put("typeName",colRet.getString("TYPE_NAME"));
                        map.put("remarks",colRet.getString("REMARKS"));
                        map.put("columnSize",colRet.getString("COLUMN_SIZE"));
                        map.put("decimalDigits",colRet.getString("DECIMAL_DIGITS"));
                        items.add(map);
                    }
                }else if(profileTable.getTableNameTag() == 2){
                    ResultSet resultSet  = database.openQuery(profileTable.getTableName());


                    ResultSetMetaData resultMetaData = resultSet.getMetaData();
                    int cols = resultMetaData.getColumnCount();
                    for (int i = 1; i < cols; i++) {


                        String columnName = resultMetaData.getColumnName(i);

                        Map<String,Object> map = new HashMap<>();
                        map.put("columnName",columnName);
                        map.put("typeName",resultMetaData.getColumnType(i));
                        map.put("remarks",resultMetaData.getColumnLabel(i));
                        map.put("columnSize",resultMetaData.getPrecision(i));
                        map.put("decimalDigits",resultMetaData.getScale(i));


                        items.add(map);
                    }

                }


                PaginationSupport<Map<String,Object>> page = new PaginationSupport<Map<String,Object>>(items, items.size());
                String jsonString = JsonHelper.encodeObject2Json(page);
                write(jsonString, resp);
            }

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



}
