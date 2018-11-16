package com.aofei.dataservice.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.dataservice.model.request.ServiceInterfaceRequest;
import com.aofei.dataservice.model.response.ServiceInterfaceResponse;
import com.aofei.dataservice.service.IServiceInterfaceService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * <p>
 *  对外数据接出接口管理控制器
 * </p>
 *
 * @author Tony
 * @since 2018-11-10
 */
@Log4j
@Api(tags = { "对外数据接出接口-对外数据接出接口管理" })
@RestController
@RequestMapping("/dataservice/service")
public class DataServiceController extends BaseController {

    @Autowired
    IServiceInterfaceService interfaceService;

    /**
     * 对外数据接出接口列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "对外数据接出接口列表(分页查询)", notes = "对外数据接出接口列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<ServiceInterfaceResponse>> page(@ApiIgnore ServiceInterfaceRequest request)  {
        Page<ServiceInterfaceResponse> page = interfaceService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 对外数据接出接口列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有对外数据接出接口列表", notes = "所有对外数据接出接口列表", httpMethod = "GET")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<ServiceInterfaceResponse>> list(@ApiIgnore ServiceInterfaceRequest request)  {
        List<ServiceInterfaceResponse> list = interfaceService.getServiceInterfaces(request);
        return Response.ok(list) ;
    }


    /**
     * 新建对外数据接出接口
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<ServiceInterfaceResponse> add(
            @RequestBody ServiceInterfaceRequest request)  {

        return Response.ok(interfaceService.save(request)) ;
    }

    /**
     * 编辑对外数据接出接口
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑对外数据接出接口", notes = "编辑对外数据接出接口", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ServiceInterfaceResponse> edit(
            @RequestBody ServiceInterfaceRequest request)  {
        return Response.ok(interfaceService.update(request)) ;
    }

    /**
     * 删除对外数据接出接口
     * @param id
     * @return
     */
    @ApiOperation(value = "删除对外数据接出接口", notes = "删除对外数据接出接口", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(interfaceService.del(id)) ;
    }

    /**
     * 根据Id查询对外数据接出接口
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询对外数据接出接口", notes = "根据Id查询对外数据接出接口", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<ServiceInterfaceResponse> get(
            @PathVariable Long id)  {

        return Response.ok(interfaceService.get(id)) ;
    }
}
