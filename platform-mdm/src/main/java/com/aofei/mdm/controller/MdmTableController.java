package com.aofei.mdm.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.mdm.model.request.TableRequest;
import com.aofei.mdm.model.response.TableResponse;
import com.aofei.mdm.service.ITableService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * 主数据表 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Api(tags = { "主数据管理-主数据表" })
@RestController
@RequestMapping("/mdm/table")
public class MdmTableController extends BaseController {

    @Autowired
    ITableService tableService;

    /**
     * 主数据表列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "主数据表列表(分页查询)", notes = "主数据表列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tableName", value = "名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<TableResponse>> page(@ApiIgnore TableRequest request)  {
        Page<TableResponse> page = tableService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 主数据表列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有主数据表列表", notes = "所有主数据表列表", httpMethod = "GET")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<TableResponse>> list(@ApiIgnore TableRequest request)  {
        List<TableResponse> list = tableService.getTables(request);
        return Response.ok(list) ;
    }


    /**
     * 新建主数据表
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<TableResponse> add(
            @RequestBody TableRequest request)  {

        return Response.ok(tableService.save(request)) ;
    }

    /**
     * 编辑主数据表
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑主数据表", notes = "编辑主数据表", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<TableResponse> edit(
            @RequestBody TableRequest request)  {
        return Response.ok(tableService.update(request)) ;
    }

    /**
     * 删除主数据表
     * @param id
     * @return
     */
    @ApiOperation(value = "删除主数据表", notes = "删除主数据表", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(tableService.del(id)) ;
    }

    /**
     * 根据Id查询主数据表
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询主数据表", notes = "根据Id查询主数据表", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<TableResponse> get(
            @PathVariable Long id)  {

        return Response.ok(tableService.get(id)) ;
    }


}
