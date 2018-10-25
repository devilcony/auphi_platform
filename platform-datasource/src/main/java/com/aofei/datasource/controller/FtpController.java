package com.aofei.datasource.controller;

import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.datasource.model.request.FtpRequest;
import com.aofei.datasource.model.response.FtpResponse;
import com.aofei.datasource.service.IFtpService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * <p>
 *  FTP管理 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Log4j
@Api(tags = { "数据源管理-FTP Server管理模块接口" })
@RestController
@RequestMapping(value = "/datasource/ftp", produces = {"application/json;charset=UTF-8"})
public class FtpController extends BaseController {


    @Autowired
    IFtpService ftpService;

    /**
     * TFP服务器列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "TFP服务器列表(分页查询)", notes = "TFP服务器列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "ftpName", value = "TFP服务器名称(模糊查询)", paramType = "query", dataType = "String")

    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<FtpResponse>> page(@ApiIgnore FtpRequest request)  {
        Page<FtpResponse> page = ftpService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * TFP服务器列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "TFP服务器列表", notes = "TFP服务器列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ftpName", value = "TFP服务器名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<FtpResponse>> list(@ApiIgnore FtpRequest request)  {
        List<FtpResponse> list = ftpService.getFtps(request);
        return Response.ok(list) ;
    }

    /**
     * 新建TFP服务器
     * @param request
     * @return
     */
    @ApiResponses(value = {
            @ApiResponse(code = 200009, message = "数据库连接验证失败"),
            @ApiResponse(code = 200, message = "success")})
    @ApiOperation(value = "新建TFP服务器", notes = "新建TFP服务器", httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<FtpResponse> add(
            @RequestBody FtpRequest request)  {
        FtpResponse response =  ftpService.save(request);

        return Response.ok(response) ;
    }

    /**
     * 编辑TFP服务器
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑TFP服务器", notes = "编辑TFP服务器", httpMethod = "POST")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Response<FtpResponse> edit(
            @RequestBody FtpRequest request)  {

        return Response.ok(ftpService.update(request)) ;
    }

    /**
     * 删除TFP服务器
     * @param id
     * @return
     */
    @ApiOperation(value = "删除TFP服务器", notes = "删除TFP服务器", httpMethod = "DELETE")
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.DELETE)
    public Response<Integer> del(
            @ApiParam(value = "TFP服务器ID", required = true)  @PathVariable Long id)  {
        return Response.ok(ftpService.del(id)) ;
    }

    /**
     * 根据Id查询TFP服务器
     * @param id
     * @return
     */
    @ApiOperation(value = "根据Id查询TFP服务器", notes = "根据Id查询TFP服务器", httpMethod = "GET")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response<FtpResponse> get(
            @ApiParam(value = "TFP服务器ID", required = true)   @PathVariable Long id)  {
        return Response.ok(ftpService.get(id)) ;
    }

}
