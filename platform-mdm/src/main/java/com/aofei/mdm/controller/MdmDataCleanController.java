package com.aofei.mdm.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.mdm.model.request.DataCleanRequest;
import com.aofei.mdm.model.response.DataCleanResponse;
import com.aofei.mdm.service.IDataCleanService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 数据映射 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Api(tags = { "主数据管理-数据映射" })
@RestController
@RequestMapping("/mdm/clean")
public class MdmDataCleanController extends BaseController {

    @Autowired
    IDataCleanService dataCleanService;

    /**
     * 数据映射列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "数据映射列表(分页查询)", notes = "数据映射列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<DataCleanResponse>> page(@ApiIgnore DataCleanRequest request)  {
        Page<DataCleanResponse> page = dataCleanService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }


    /**
     * 新建数据映射
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<DataCleanResponse> add(
            @RequestBody DataCleanRequest request)  {

        return Response.ok(dataCleanService.save(request)) ;
    }

    /**
     * 编辑数据映射
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑数据映射", notes = "编辑数据映射", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<DataCleanResponse> edit(
            @RequestBody DataCleanRequest request)  {
        return Response.ok(dataCleanService.update(request)) ;
    }

    /**
     * 删除数据映射
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据映射", notes = "删除数据映射", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(dataCleanService.del(id)) ;
    }

    /**
     * 根据Id查询数据映射
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询数据映射", notes = "根据Id查询数据映射", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<DataCleanResponse> get(
            @PathVariable Long id)  {

        return Response.ok(dataCleanService.get(id)) ;
    }
}
