package com.aofei.datasource.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.datasource.model.request.DatabaseRequest;
import com.aofei.datasource.model.response.DatabaseNameResponse;
import com.aofei.datasource.model.response.DatabaseResponse;
import com.aofei.datasource.service.IDatabaseService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.sql.SQLException;
import java.util.List;

/**
 * <p>
 *  数据库管理 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Log4j
@Api(tags = { "数据源管理-数据库管理模块接口" })
@Authorization
@RestController
@RequestMapping(value = "/datasource/database", produces = {"application/json;charset=UTF-8"})
public class DatabaseController extends BaseController {

    @Autowired
    private IDatabaseService databaseService;

    /**
     * 列表(分页查询)
     * @return
     */
    @ApiOperation(value = "数据库列表(分页查询)", notes = "数据库列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "name", value = "数据源名称(like查询)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "repository", value = "资源库名称", paramType = "query", dataType = "String", required=true)
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<DatabaseResponse>> page(
            @ApiIgnore DatabaseRequest request, @ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, SQLException {
        request.setOrganizerId(user.getOrganizerId());
        Page<DatabaseResponse> page = databaseService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }
    /**
     * 列表(分页查询)
     * @return
     */
    @ApiOperation(value = "数据库名称列表(只返回name)", notes = "数据库名称列表", httpMethod = "GET")
    @RequestMapping(value = "/listNames", method = RequestMethod.GET)
    public Response<List<DatabaseNameResponse>> listNames(
            @ApiIgnore DatabaseRequest request, @ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException, SQLException {
        request.setOrganizerId(user.getOrganizerId());
        List<DatabaseResponse> list = databaseService.getDatabases(request);
        return Response.ok(BeanCopier.copy(list,DatabaseNameResponse.class)) ;
    }
}
