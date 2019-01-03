package com.aofei.sys.controller;


import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.sys.model.request.PlatformLogRequest;
import com.aofei.sys.model.response.PlatformLogResponse;
import com.aofei.sys.service.IPlatformLogService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-09-12 20:07
 */
@Log4j
@Api(tags = { "系统管理-系统日志管理模块接口" })
@RestController
@RequestMapping(value = "/sys/log", produces = {"application/json;charset=UTF-8"})
public class SysLogController extends BaseController {

    @Autowired
    IPlatformLogService platformLogService;

    /**
     * 系统日志列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "系统日志列表(分页查询)", notes = "系统日志列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "startCreateTime", value = "开始时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "endCreateTime", value = "结束时间(格式:yyyy-MM-dd)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<PlatformLogResponse>> page(@ApiIgnore PlatformLogRequest request)  {
        Page<PlatformLogResponse> page = platformLogService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

    /**
     * 清空日志
     * @return
     */
    @ApiOperation(value = "清空日志", notes = "清空日志", httpMethod = "DELETE")
    @RequestMapping(value = "/clear", method = RequestMethod.DELETE)
    public Response<Integer> clear()  {
        return Response.ok(platformLogService.delete(new EntityWrapper<>()));
    }

    /**
     * 清空日志
     * @return
     */
    @ApiOperation(value = "批量删除", notes = "清空日志", httpMethod = "DELETE")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Response<Integer> delete(@RequestBody List<Long> logIds)  {

        return Response.ok(platformLogService.deleteBatchIds(logIds));
    }

}
