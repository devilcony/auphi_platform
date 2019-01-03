package com.aofei.profile.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.profile.model.request.ProfileTableGroupRequest;
import com.aofei.profile.model.response.ProfileTableGroupResponse;
import com.aofei.profile.service.IProfileTableGroupService;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Log4j
@Api(tags = { "数据质量-数据质量分组" })
@RestController
@RequestMapping(value = "/quality/Group", produces = {"application/json;charset=UTF-8"})
public class ProfileTableGroupController extends BaseController {

    @Autowired
    IProfileTableGroupService profileTableGroupService;


    /**
     * 根据Id查询菜单
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询菜单", notes = "根据Id查询菜单", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<ProfileTableGroupResponse> get(
            @ApiParam(value = "菜单ID", required = true)  @PathVariable Long id)  {
        return Response.ok(profileTableGroupService.get(id)) ;
    }
    
    /**
     * 数据质量分组列表
     * @param request
     * @return
     */
    @ApiOperation(value = "数据质量分组列表", notes = "数据质量分组列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父数据质量分组ID", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "数据质量分组名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<ProfileTableGroupResponse>> list(@ApiIgnore ProfileTableGroupRequest request)  {

        List<ProfileTableGroupResponse> list = profileTableGroupService.getProfileTableGroups(request);
        return Response.ok(list) ;
    }
    /**
     * 新建数据质量分组
     * @param request
     * @return
     */
    @ApiOperation(value = "新建数据质量分组", notes = "新建数据质量分组", httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<ProfileTableGroupResponse> add(
            @RequestBody ProfileTableGroupRequest request)  {


        return Response.ok(profileTableGroupService.save(request)) ;
    }

    /**
     * 编辑数据质量分组
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑数据质量分组", notes = "编辑数据质量分组", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ProfileTableGroupResponse> edit(
            @RequestBody ProfileTableGroupRequest request)  {


        return Response.ok(profileTableGroupService.update(request)) ;
    }

    /**
     * 删除数据质量分组
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据质量分组", notes = "删除数据质量分组", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @ApiParam(value = "数据质量分组ID", required = true) @PathVariable Long id)  {

        return Response.ok(profileTableGroupService.del(id)) ;
    }

}
