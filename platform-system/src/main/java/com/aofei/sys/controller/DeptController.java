package com.aofei.sys.controller;


import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.sys.model.request.DeptRequest;
import com.aofei.sys.model.response.DeptResponse;
import com.aofei.sys.service.IDeptService;
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
 * 部门管理 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Log4j
@Api(tags = { "系统管理-部门管理模块接口" })
@RestController
@RequestMapping(value = "/sys/dept", produces = {"application/json;charset=UTF-8"})
public class DeptController extends BaseController {


    @Autowired
    IDeptService deptService;

    /**
     * 部门列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "部门列表(分页查询)", notes = "部门列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "parentId", value = "上级部门ID", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "名称(模糊查询)", paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<DeptResponse>> page(@ApiIgnore DeptRequest request)  {
        Page<DeptResponse> page = deptService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 部门列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有部门列表", notes = "所有部门列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "上级部门ID", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "名称(模糊查询)", paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<DeptResponse>> list(@ApiIgnore DeptRequest request)  {
        List<DeptResponse> list = deptService.getDepts(request);
        return Response.ok(list) ;
    }


    /**
     * 新建部门
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<DeptResponse> add(
            @RequestBody DeptRequest request)  {

        return Response.ok(deptService.save(request)) ;
    }

    /**
     * 编辑部门
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑部门", notes = "编辑部门", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<DeptResponse> edit(
            @RequestBody DeptRequest request)  {
        return Response.ok(deptService.update(request)) ;
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    @ApiOperation(value = "删除部门", notes = "删除部门", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(deptService.del(id)) ;
    }

    /**
     * 根据Id查询部门
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询部门", notes = "根据Id查询部门", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<DeptResponse> get(
            @PathVariable Long id)  {

        return Response.ok(deptService.get(id)) ;
    }


}

