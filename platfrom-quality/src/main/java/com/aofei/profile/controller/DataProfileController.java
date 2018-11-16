package com.aofei.profile.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.compare.util.TemplateUtil;
import com.aofei.profile.entity.ProfileTableColumn;
import com.aofei.profile.entity.ProfileTableResult;
import com.aofei.profile.model.request.ProfileTableRequest;
import com.aofei.profile.model.response.ProfileTableColumnResponse;
import com.aofei.profile.model.response.ProfileTableResponse;
import com.aofei.profile.service.IProfileTableService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import com.aofei.kettle.utils.DatabaseUtils;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 数据质量-数据剖析管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Log4j
@Api(tags = { "数据质量-数据剖析管理模块接口" })
@RestController
@RequestMapping(value = "/quality/profile", produces = {"application/json;charset=UTF-8"})
public class DataProfileController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DataProfileController.class);

    @Autowired
    private IProfileTableService profileTableService;

    String[] needAvgTypes = {"NUMBER","INT","FLOAT","INTEGER","DOUBLE"};

    /**
     * 数据剖析列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "数据剖析列表(分页查询)", notes = "数据剖析列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startCreateTime", value = "开始时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<ProfileTableResponse>> page(@ApiIgnore ProfileTableRequest request)  {
        Page<ProfileTableResponse> page = profileTableService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 执行数据剖析
     * @param ids
     * @return
     */
    @ApiOperation(value = "执行数据剖析", notes = "执行数据剖析", httpMethod = "POST")
    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public Response<Integer> exec(
            @ApiParam(value = "id数组", required = true) @RequestBody Long[] ids) throws Exception {
        int count = 0;
        for(Long id : ids){
            if(id!=null){
                ProfileTableResponse response = profileTableService.get(id);
                execProfileTable(response);
            }
        }
        return Response.ok(count);
    }

    /**
     * 数据剖析-获取字段
     * @param request
     * @return
     */
    @ApiOperation(value = "数据剖析-获取字段", notes = "数据剖析-获取字段", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 210001, message = "字段类型不一致"),
            @ApiResponse(code = 210002, message = "字段数量不一致"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/column/get", method = RequestMethod.POST)
    public Response<List<ProfileTableColumn>> getColumn(@RequestBody ProfileTableRequest request) throws Exception {


        Database database = null;
        List<ProfileTableColumn> items = new ArrayList<>();
        try{

            if(request!=null &&request.getDatabaseId()!=null){
                database = DatabaseUtils.getDatabase(request.getRepositoryName(),request.getDatabaseId());
            }

            if(database!=null && request.getTableName()!=null){
                database.connect();
                DatabaseMetaData m_DBMetaData = database.getConnection().getMetaData();
                if(request.getTableNameTag() == 1){ //表


                    String schemaName = request.getSchemaName() ==null ? null: request.getSchemaName().trim();
                    String tableName = request.getTableName()==null?null:request.getTableName().trim();


                    ResultSet colRet = m_DBMetaData.getColumns(null,schemaName , tableName, "%");

                    while(colRet.next()){
                        ProfileTableColumn map = new ProfileTableColumn();
                        map.setProfileTableColumnName(colRet.getString("COLUMN_NAME"));
                        map.setProfileTableColumnType(colRet.getString("TYPE_NAME"));
                        map.setProfileTableColumnDesc(colRet.getString("REMARKS"));
                        map.setProfileTableColumnSize(colRet.getString("COLUMN_SIZE"));
                        map.setProfileTableColumnPrecision(colRet.getString("DECIMAL_DIGITS"));
                        items.add(map);
                    }
                }else if(request.getTableNameTag() == 2){
                    ResultSet resultSet  = database.openQuery(request.getTableName());


                    ResultSetMetaData resultMetaData = resultSet.getMetaData();
                    int cols = resultMetaData.getColumnCount();
                    for (int i = 1; i < cols; i++) {

                        ProfileTableColumn map = new ProfileTableColumn();
                        map.setProfileTableColumnName(resultMetaData.getColumnName(i));
                        map.setProfileTableColumnType(resultMetaData.getColumnTypeName(i));
                        map.setProfileTableColumnDesc(resultMetaData.getColumnLabel(i));
                        map.setProfileTableColumnSize(String.valueOf(resultMetaData.getPrecision(i)));
                        map.setProfileTableColumnPrecision(String.valueOf(resultMetaData.getScale(i)));
                        items.add(map);

                        items.add(map);
                    }

                }

               return Response.ok(items);


            }

        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(database!=null){
                try {
                    database.closeConnectionOnly();
                } catch (KettleDatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new ApplicationException(null);
    }


    /**
     * 新建数据剖析
     *
     * @return
     */
    @ApiOperation(value = "新建数据剖析", notes = "新建数据剖析", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response<ProfileTableResponse> add(@RequestBody ProfileTableRequest request) throws Exception {

        ProfileTableResponse response =  profileTableService.save(request);
        return Response.ok(response);
    }

    /**
     * 根据Id查询数据剖析信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询数据剖析信息", notes = "根据Id查询数据剖析信息", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<ProfileTableResponse> get(
            @ApiParam(value = "数据剖析信息ID", required = true)  @PathVariable Long id)  {
        return Response.ok(profileTableService.get(id)) ;
    }

    /**
     * 编辑数据剖析信息
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑数据剖析信息", notes = "编辑数据剖析信息", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ProfileTableResponse> edit(
            @RequestBody ProfileTableRequest request)  {


        return Response.ok(profileTableService.update(request)) ;
    }

    /**
     * 删除数据剖析信息
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据剖析信息", notes = "删除数据剖析信息", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @ApiParam(value = "数据剖析信息ID", required = true) @PathVariable Long id)  {

        return Response.ok(profileTableService.del(id)) ;
    }

    /**
     * 执行sql
     * @param profileTable
     * @return
     */
    private void   execProfileTable(ProfileTableResponse profileTable) throws Exception {
        ResultSet colRet = null;
        ResultSet resultSet = null;
        Database database = DatabaseUtils.getDatabase(profileTable.getRepositoryName(),profileTable.getDatabaseId());

        try {
            if(database!=null){
                for(ProfileTableColumnResponse column : profileTable.getProfileTableColumns()){
                    ProfileTableResult result =  execProfileTableColumn(database,profileTable,column);
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
            throw e;
        }finally {
            if(database!=null ){
                database.disconnect();
            }
        }

    }

    private ProfileTableResult execProfileTableColumn(Database database, ProfileTableResponse profileTable, ProfileTableColumnResponse column) throws Exception {

        ResultSet resultSet = null;

        ProfileTableResult profileTableResult = new ProfileTableResult();
        profileTableResult.setProfileTableColumnId(column.getProfileTableColumnId());

        DatabaseMetaData m_DBMetaData = database.getConnection().getMetaData();

        String schemaName = profileTable.getSchemaName();
        String tableName = profileTable.getTableName();
        String columnName = column.getProfileTableColumnName();

        ResultSet colRet = m_DBMetaData.getColumns(null,schemaName ==null ? null: schemaName.trim(), tableName.trim(), columnName==null?null:columnName.trim() );
        while(colRet.next()){
            String colName = colRet.getString("COLUMN_NAME");//列名
            if(colName!=null && colName.equals(column.getProfileTableColumnName())){
                profileTableResult.setIndicatorDataLength(colRet.getInt("COLUMN_SIZE"));
                profileTableResult.setIndicatorDataType(colRet.getString("TYPE_NAME"));//类型名称
                profileTableResult.setIndicatorDataPrecision(colRet.getInt("COLUMN_SIZE"));//精度
                profileTableResult.setIndicatorDataScale(colRet.getInt("DECIMAL_DIGITS"));// 小数的位数
            }
        }

        String sql = spliceSQl(profileTable,column,profileTableResult);
        logger.info("sql==>"+sql);
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

                logger.error(e.getMessage());
                throw  e;
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
    }

    /**
     * 生成sql
     * @param column
     * @param profileTableResult
     * @return
     * @throws Exception
     */
    private String spliceSQl(ProfileTableResponse profileTable, ProfileTableColumnResponse column, ProfileTableResult profileTableResult) throws Exception {


        String tableName = profileTable.getTableName();
        String columnName = column.getProfileTableColumnName();

        if(tableName!=null && columnName!=null && !"".equals(tableName) && !"".equals(columnName)){
            StringBuffer sql = new StringBuffer("SELECT count(1) AS indicatorAllCount, ");
            sql.append(" (SELECT count(1) FROM ").append(tableName).append(" A WHERE A.").append(columnName).append(" is null "+ getConditionSql("AND",profileTable,column) +") AS indicatorNullCount , ");
            if(needAvg(profileTableResult.getIndicatorDataType())){
                sql.append(" (SELECT count(1) FROM ").append(tableName).append(" B WHERE B.").append(columnName).append(" = 0 "+ getConditionSql("AND",profileTable,column) +") AS indicatorZeroCount, ");
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
            sql.append(" FROM ").append(tableName).append(" T ").append(getConditionSql(" WHERE ",profileTable,column));

            return TemplateUtil.replaceVariable(sql.toString(), new Date(), true);
        }
        return null;

    }

    private String getConditionSql(String s, ProfileTableResponse profileTable, ProfileTableColumnResponse column) {

        if(profileTable.getCondition()!=null && !"".equals(profileTable.getCondition())){
            return " "+s+" "+profileTable.getCondition();
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
