package com.aofei.mdm.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.mdm.model.request.ModelRequest;
import com.aofei.mdm.model.response.ModelResponse;
import com.aofei.mdm.service.IModelService;
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
 * 主数据模型 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Api(tags = { "主数据管理-数据模型" })
@Authorization
@RestController
@RequestMapping("/mdm/model")
public class MdmModelController extends BaseController {

    @Autowired
    IModelService modelService;

    /**
     * 数据模型列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "数据模型列表(分页查询)", notes = "数据模型列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tableName", value = "名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<ModelResponse>> page(@ApiIgnore ModelRequest request,@ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        Page<ModelResponse> page = modelService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 数据模型列表
     * @param request
     * @return
     */
    @ApiOperation(value = "所有数据模型列表", notes = "所有数据模型列表", httpMethod = "GET")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<ModelResponse>> list(@ApiIgnore ModelRequest request,@ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        List<ModelResponse> list = modelService.getModels(request);
        return Response.ok(list) ;
    }


    /**
     * 新建数据模型
     * @param request
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<ModelResponse> add(
            @RequestBody ModelRequest request,@ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        return Response.ok(modelService.save(request)) ;
    }

    /**
     * 编辑数据模型
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑数据模型", notes = "编辑数据模型", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<ModelResponse> edit(
            @RequestBody ModelRequest request)  {
        return Response.ok(modelService.update(request)) ;
    }

    /**
     * 删除数据模型
     * @param id
     * @return
     */
    @ApiOperation(value = "删除数据模型", notes = "删除数据模型", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @PathVariable Long id)  {
        return Response.ok(modelService.del(id)) ;
    }

    /**
     * 根据Id查询数据模型
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询数据模型", notes = "根据Id查询数据模型", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<ModelResponse> get(
            @PathVariable Long id)  {

        return Response.ok(modelService.get(id)) ;
    }
}
