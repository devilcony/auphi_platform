package com.aofei.schedule.controller;


import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.schedule.model.request.GroupRequest;
import com.aofei.schedule.model.response.GroupResponse;
import com.aofei.schedule.service.IGroupService;
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
 * 调度分组 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Api(tags = { "调度管理-调度分组" })
@Authorization
@RestController
@RequestMapping("/schedule/group")
public class GroupController extends BaseController {


    @Autowired
    IGroupService groupService;

    /**
     * 调度分组列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "调度分组列表(分页查询)", notes = "调度分组列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "groupName", value = "名称(模糊查询)", paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<GroupResponse>> page(
            @ApiIgnore GroupRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        Page<GroupResponse> page = groupService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 调度分组列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有调度分组列表", notes = "所有调度分组列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "名称(模糊查询)", paramType = "query", dataType = "Long")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<GroupResponse>> list(
            @ApiIgnore GroupRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        List<GroupResponse> list = groupService.getGroups(request);
        return Response.ok(list) ;
    }


    /**
     * 新建调度分组
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<GroupResponse> add(
            @RequestBody GroupRequest request,@ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        return Response.ok(groupService.save(request)) ;
    }

    /**
     * 编辑调度分组
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑调度分组", notes = "编辑调度分组", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<GroupResponse> edit(
            @RequestBody GroupRequest request)  {
        return Response.ok(groupService.update(request)) ;
    }

    /**
     * 删除调度分组
     * @param id
     * @return
     */
    @ApiOperation(value = "删除调度分组", notes = "删除调度分组", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(groupService.del(id)) ;
    }

    /**
     * 根据Id查询调度分组
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询调度分组", notes = "根据Id查询调度分组", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<GroupResponse> get(
            @PathVariable Long id)  {

        return Response.ok(groupService.get(id)) ;
    }

}

