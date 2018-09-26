package com.aofei.sys.controller;

import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.log.annotation.Log;
import com.aofei.sys.model.request.MenuRequest;
import com.aofei.sys.model.response.MenuResponse;
import com.aofei.sys.model.response.UserResponse;
import com.aofei.sys.service.IMenuService;
import com.aofei.sys.service.IUserService;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-09-12 20:07
 */
@Log4j
@Api(tags = { "系统管理-菜单管理模块接口" })
@RestController
@RequestMapping(value = "/sys/menu", produces = {"application/json;charset=UTF-8"})
public class MenuController extends BaseController {



    @Autowired
    IMenuService menuService;

    @Autowired
    IUserService userService;
    /**
     * 菜单列表
     * @param request
     * @return
     */
    @ApiOperation(value = "菜单列表", notes = "菜单列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父菜单ID", paramType = "query", dataType = "Long"),
            @ApiImplicitParam(name = "name", value = "菜单名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<MenuResponse>> list(@ApiIgnore MenuRequest request)  {

        List<MenuResponse> list = menuService.getMenus(request);
        return Response.ok(list) ;
    }
    /**
     * 新建菜单
     * @param request
     * @return
     */
    @Log(module = "菜单管理",description = "新建菜单")
    @ApiOperation(value = "新建菜单", notes = "新建菜单", httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<MenuResponse> add(
            @RequestBody MenuRequest request)  {


        return Response.ok(menuService.save(request)) ;
    }

    /**
     * 编辑菜单
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑菜单", notes = "编辑菜单", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<MenuResponse> edit(
            @RequestBody MenuRequest request)  {


        return Response.ok(menuService.update(request)) ;
    }

    /**
     * 删除菜单
     * @param id
     * @return
     */
    @ApiOperation(value = "删除菜单", notes = "删除菜单", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @ApiParam(value = "菜单ID", required = true) @PathVariable Long id)  {

        return Response.ok(menuService.del(id)) ;
    }

    /**
     * 根据Id查询菜单
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询菜单", notes = "根据Id查询菜单", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<MenuResponse> get(
            @ApiParam(value = "菜单ID", required = true)  @PathVariable Long id)  {
        return Response.ok(menuService.get(id)) ;
    }

    /**
     * 查询菜单列表
     */
    @ApiOperation(value = "当前登录用户的菜单列表", notes = "当前登录用户的菜单列表", httpMethod = "GET")
    @RequestMapping(value = "/menus/my", method = RequestMethod.GET)
    public Response<List<MenuResponse>> getMenus(
            @ApiIgnore @CurrentUser CurrentUserResponse user) {

        //查询我的菜单
        UserResponse userResponse = userService.get(user.getUserId());
        List<MenuResponse> list = null;
        if(userResponse.getUserId() ==1){
            list = menuService.getMenus(new MenuRequest());
        }else{
            list =  menuService.getMenusByUser(userResponse.getUserId());
        }
        return Response.ok(list);
    }

    /**
     * 查询菜单列表
     */
    @ApiOperation(value = "指定用户ID的菜单", notes = "指定用户ID的菜单", httpMethod = "GET")
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET)
    public Response<List<MenuResponse>> getMenus(
            @ApiParam(value = "用户ID", required = true)  @PathVariable("userId") String userId) {

        //查询我的菜单
        UserResponse userResponse = userService.get(userId);
        List<MenuResponse> list = null;
        if(userResponse.getUserId() ==1){
            list = menuService.getMenus(new MenuRequest());
        }else{
            list =  menuService.getMenusByUser(userResponse.getUserId());
        }
        return Response.ok(list);
    }
}
