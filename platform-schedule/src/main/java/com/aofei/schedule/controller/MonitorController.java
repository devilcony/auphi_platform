package com.aofei.schedule.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.base.model.vo.DataGrid;
import com.aofei.schedule.model.request.JobDetailsRequest;
import com.aofei.schedule.model.request.MonitorRequest;
import com.aofei.schedule.model.response.JobDetailsResponse;
import com.aofei.schedule.model.response.MonitorResponse;
import com.aofei.schedule.service.IMonitorService;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = { "调度管理-监控(调度执行日志)" })
@Authorization
@RestController
@RequestMapping(value = "/schedule/monitor", produces = {"application/json;charset=UTF-8"})
public class MonitorController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CycleScheduleController.class);

    @Autowired
    private IMonitorService monitorService;


    /**
     * 资源库列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "调度执行日志列表(分页查询)", notes = "调度执行日志列表(分页查询)", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页码(默认1)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页数量(默认10)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "jobGroup", value = "分组(模糊查询)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "jobName", value = "调度名称(模糊查询)", paramType = "query", dataType = "String")

    })
    @RequestMapping(value = "/listPage", method = RequestMethod.GET)
    public Response<DataGrid<MonitorResponse>> page(
            @ApiIgnore MonitorRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user)  {
        request.setOrganizerId(user.getOrganizerId());
        Page<MonitorResponse> page = monitorService.getPage(getPagination(request), request);
        return Response.ok(buildDataGrid(page)) ;
    }

}
