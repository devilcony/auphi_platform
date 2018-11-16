package com.aofei.dataservice.controller;


import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.dataservice.model.request.ServiceAuthRequest;
import com.aofei.dataservice.model.response.ServiceAuthResponse;
import com.aofei.dataservice.service.IServiceAuthService;
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
 *  服务用户权限管理控制器
 * </p>
 *
 * @author Tony
 * @since 2018-11-10
 */
@Log4j
@Api(tags = { "对外数据接出接口-服务用户权限管理" })
@RestController
@RequestMapping("/dataservice/auth")
public class DataServiceAuthController extends BaseController {


    @Autowired
   private IServiceAuthService serviceAuthService;

    /**
     * 对外数据接出接口服务服务用户权限(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "对外数据接出接口服务服务用户权限(分页查询)", notes = "对外数据接出接口服务服务用户权限(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<ServiceAuthResponse>> page(@ApiIgnore ServiceAuthRequest request)  {
        Page<ServiceAuthResponse> page = serviceAuthService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 对外数据接出接口服务服务用户权限
     * @param request
     * @return
     */
    @ApiOperation(value = "所有对外数据接出接口服务服务用户权限", notes = "所有对外数据接出接口服务服务用户权限", httpMethod = "GET")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<ServiceAuthResponse>> list(@ApiIgnore ServiceAuthRequest request)  {
        List<ServiceAuthResponse> list = serviceAuthService.getServiceAuths(request);
        return Response.ok(list) ;
    }


    /**
     * 新建对外数据接出接口服务用户
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<ServiceAuthResponse> add(
            @RequestBody ServiceAuthRequest request)  {

        return Response.ok(serviceAuthService.save(request)) ;
    }

    /**
     * 编辑对外数据接出接口服务用户
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑对外数据接出接口服务用户", notes = "编辑对外数据接出接口服务用户", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ServiceAuthResponse> edit(
            @RequestBody ServiceAuthRequest request)  {
        return Response.ok(serviceAuthService.update(request)) ;
    }

    /**
     * 删除对外数据接出接口服务用户
     * @param id
     * @return
     */
    @ApiOperation(value = "删除对外数据接出接口服务用户", notes = "删除对外数据接出接口服务用户", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(serviceAuthService.del(id)) ;
    }

    /**
     * 根据Id查询对外数据接出接口服务用户
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询对外数据接出接口服务用户", notes = "根据Id查询对外数据接出接口服务用户", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<ServiceAuthResponse> get(
            @PathVariable Long id)  {

        return Response.ok(serviceAuthService.get(id)) ;
    }

}

