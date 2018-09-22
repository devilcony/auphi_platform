package com.aofei.sys.controller;


import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.log.annotation.Log;
import com.aofei.sys.model.request.RepositoryRequest;
import com.aofei.sys.model.response.RepositoryResponse;
import com.aofei.sys.service.IRepositoryService;
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
 * 资源库 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-18
 */
@Log4j
@Api(tags = { "系统管理-资源库管理模块接口" })
@RestController
@RequestMapping(value = "/sys/repository", produces = {"application/json;charset=UTF-8"})
public class RepositoryController extends BaseController {

    @Autowired
    IRepositoryService repositoryService;

    /**
     * 资源库列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "资源库列表(分页查询)", notes = "资源库列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "repositoryName", value = "资源库名称(模糊查询)", paramType = "query", dataType = "String")

    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<RepositoryResponse>> page(@ApiIgnore RepositoryRequest request)  {
        Page<RepositoryResponse> page = repositoryService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 资源库列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "资源库列表", notes = "资源库列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "repositoryName", value = "资源库名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<RepositoryResponse>> list(@ApiIgnore RepositoryRequest request)  {
        List<RepositoryResponse> list = repositoryService.getRepositorys(request);
        return Response.ok(list) ;
    }

    /**
     * 新建资源库
     * @param request
     * @return
     */
    @Log(module = "资源库管理",description = "新建资源库")
    @ApiOperation(value = "新建资源库", notes = "新建资源库", httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<RepositoryResponse> add(
            @RequestBody RepositoryRequest request)  {

        return Response.ok(repositoryService.save(request)) ;
    }

    /**
     * 编辑资源库
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑资源库", notes = "编辑资源库", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<RepositoryResponse> edit(
            @RequestBody RepositoryRequest request)  {

        return Response.ok(repositoryService.update(request)) ;
    }

    /**
     * 删除资源库
     * @param id
     * @return
     */
    @ApiOperation(value = "删除资源库", notes = "删除资源库", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(repositoryService.del(id)) ;
    }

    /**
     * 根据Id查询资源库
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询资源库", notes = "根据Id查询资源库", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<RepositoryResponse> get(
            @PathVariable Long id)  {
        return Response.ok(repositoryService.get(id)) ;
    }
}

