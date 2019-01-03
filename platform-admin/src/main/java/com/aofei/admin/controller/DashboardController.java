package com.aofei.admin.controller;

import com.aofei.base.annotation.Authorization;
import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.schedule.model.response.DashboardResponse;
import com.aofei.schedule.service.IMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @auther Tony
 * @create 2018-09-15 15:45
 */
@Api(tags = { "首页-仪表盘" })
@Authorization
@RestController
@RequestMapping(value = "/dashboard", produces = {"application/json;charset=UTF-8"})
public class DashboardController extends BaseController {



    @Autowired
    private IMonitorService monitorService;


    /**
     * 注销
     *
     * @return
     */
    @ApiOperation(value = "首页-仪表盘", notes = "首页-仪表盘", httpMethod = "GET")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public Response<DashboardResponse> index(@ApiIgnore @CurrentUser CurrentUserResponse user) throws KettleException {
        DashboardResponse response = monitorService.getDashboardCount(user);

        return Response.ok(response);
    }




}
