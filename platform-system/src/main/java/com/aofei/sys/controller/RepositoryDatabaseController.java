package com.aofei.sys.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.kettle.core.database.DatabaseCodec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 资源库链接 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Log4j
@Api(tags = { "系统管理-资源库数据源模块接口" })
@RestController
@RequestMapping(value = "/sys/repository/database", produces = {"application/json;charset=UTF-8"})
public class RepositoryDatabaseController extends BaseController {


    /**
     * 新建资源库
     * @param request
     * @return
     */
    @ApiOperation(value = "新建资源库数据库链接", notes = "新建资源库数据库链接", httpMethod = "POST")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<String> create(
            @RequestBody JSON request)  {

        return Response.ok(request.toString()) ;
    }


    /**
     * 新建资源库
     * @param request
     * @return
     */
    @ApiOperation(value = "新建资源库", notes = "新建资源库", httpMethod = "POST")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Response<String> test(
            @RequestBody JSONObject request) throws KettleDatabaseException {

        DatabaseMeta dbinfo = DatabaseCodec.decode(request);
        String remarks = dbinfo.testConnection();

        return Response.ok(remarks) ;
    }


}

