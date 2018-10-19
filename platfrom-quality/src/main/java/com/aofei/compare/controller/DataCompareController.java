package com.aofei.compare.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.compare.entity.CompareSqlColumn;
import com.aofei.compare.exception.CompareError;
import com.aofei.compare.model.request.CompareSqlRequest;
import com.aofei.compare.model.request.CompareSqlResultRequest;
import com.aofei.compare.model.response.CompareSqlColumnResponse;
import com.aofei.compare.model.response.CompareSqlResponse;
import com.aofei.compare.service.ICompareSqlResultService;
import com.aofei.compare.service.ICompareSqlService;
import com.aofei.compare.util.TemplateUtil;
import com.aofei.kettle.utils.DatabaseUtils;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;


/**
 * <p>
 * 数据质量-统计数据稽核管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Log4j
@Api(tags = { "数据质量-统计数据稽核管理模块接口" })
@RestController
@RequestMapping(value = "/quality/compare", produces = {"application/json;charset=UTF-8"})
public class DataCompareController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(DataCompareController.class);

    @Autowired
    private ICompareSqlService compareSqlService;

    @Autowired
    private ICompareSqlResultService compareSqlResultService;


    /**
     * 数据稽核列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "数据稽核列表(分页查询)", notes = "数据稽核列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startCreateTime", value = "开始时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<CompareSqlResponse>> page(@ApiIgnore CompareSqlRequest request)  {
        Page<CompareSqlResponse> page = compareSqlService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }


    /**
     * 执行数据稽核
     * @param ids
     * @return
     */
    @ApiOperation(value = "执行数据稽核", notes = "执行数据稽核", httpMethod = "POST")
    @RequestMapping(value = "/exec", method = RequestMethod.POST)
    public Response<Integer> exec(@RequestBody Long[] ids)  {
        int count = 0;
        for(Long id : ids){
            if(id!=null){
                CompareSqlResponse response = compareSqlService.get(id);
            }

        }

        return Response.ok(count);

    }

    private void execCompareSql(CompareSqlResponse compareSql) throws Exception {
        ResultSet resultSet = null;
        ResultSet referenceResultSet = null;

        Database testDatabase = null;

        Database referenceDatabase = null;

        try{

            testDatabase = DatabaseUtils.getDatabase(compareSql.getRepositoryName(),compareSql.getDatabaseId());
            referenceDatabase =  DatabaseUtils.getDatabase(compareSql.getRepositoryName(),compareSql.getReferenceDbId());

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
                        CompareSqlResultRequest compareSqlResult = new CompareSqlResultRequest(compareSqlColumn.getCompareSqlColumnId());
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
                            if(StringUtils.isNumeric(compareSqlResult.getColumnValue()) &&  StringUtils.isNumeric(compareSqlResult.getReferenceColumnValue())){

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
                        compareSqlResultService.save(compareSqlResult);
                    }
                }
            }
        } catch(Exception e){
            logger.error(e.getMessage());
            throw e;
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

    /**
     * 数据稽核-获取字段
     * @param request
     * @return
     */
    @ApiOperation(value = "数据稽核-获取字段", notes = "数据稽核-获取字段", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 210001, message = "字段类型不一致"),
            @ApiResponse(code = 210002, message = "字段数量不一致"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/column/get", method = RequestMethod.POST)
    public Response<List<CompareSqlColumnResponse>> getColumn(@RequestBody CompareSqlRequest request) throws Exception {

        Database database = null;
        Database reference = null;
        try{
            if(request!=null && request.getDatabaseId()!=null && request.getReferenceDbId()!=null){

                database = DatabaseUtils.getDatabase(request.getRepositoryName(),request.getDatabaseId());
                database.connect();

                reference = DatabaseUtils.getDatabase(request.getRepositoryName(),request.getReferenceDbId());
                reference.connect();

                List<CompareSqlColumn> list = new ArrayList<>();


                ResultSet sqlSet  = database.openQuery(TemplateUtil.replaceVariable(request.getSql(),new Date(),true));



                ResultSet referenceSqlSet  = reference.openQuery(TemplateUtil.replaceVariable(request.getReferenceSql(),new Date(),true));

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
                            throw  new ApplicationException(CompareError.TYPE_INCONSISTENCY.getCode(),"字段返回类型不一致 "+columnName+"->"+columnType+";"+referenceColumnName+"->"+referenceColumnType);
                        }

                        list.add(compareSqlColumn);
                    }

                    return Response.ok(list);

                }else{
                    throw  new ApplicationException(CompareError.QUANTITY_DISCREPANCY.getCode(),"两条sql返回的字段数量不一致");
                }

            }

        }catch(Exception e){
            throw  e;
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


    /**
     * 新建数据稽核
     *
     * @return
     */
    @ApiOperation(value = "新建数据稽核", notes = "新建数据稽核", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public Response<CompareSqlResponse> add(@RequestBody CompareSqlRequest request) throws Exception {

        CompareSqlResponse response =  compareSqlService.save(request);
        return Response.ok(response);
    }

    /**
     * 根据Id查询数据稽核信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询数据稽核信息", notes = "根据Id查询数据稽核信息", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<CompareSqlResponse> get(
            @ApiParam(value = "数据稽核信息ID", required = true)  @PathVariable Long id)  {
        return Response.ok(compareSqlService.get(id)) ;
    }

    /**
     * 编辑数据稽核信息
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑数据稽核信息", notes = "编辑数据稽核信息", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<CompareSqlResponse> edit(
            @RequestBody CompareSqlRequest request)  {


        return Response.ok(compareSqlService.update(request)) ;
    }

    /**
     * 删除数据稽核信息
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据稽核信息", notes = "删除数据稽核信息", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @ApiParam(value = "数据稽核信息ID", required = true) @PathVariable Long id)  {

        return Response.ok(compareSqlService.del(id)) ;
    }
}
