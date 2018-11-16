package com.aofei.sys.controller;


import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.Response;
import com.aofei.sys.model.request.RepositoryDatabaseRequest;
import com.aofei.sys.model.response.RepositoryDatabaseResponse;
import com.aofei.sys.service.IRepositoryDatabaseService;
import com.aofei.sys.utils.DatabaseCodec;
import com.aofei.sys.utils.RepositoryCodec;
import io.swagger.annotations.*;
import lombok.extern.log4j.Log4j;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    IRepositoryDatabaseService repositoryDatabaseService;


    /**
     * 资源库列表(分页查询)
     * @param request
     * @return
     */
    @ApiOperation(value = "资源库连接列表", notes = "资源库连接列表", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "repositoryName", value = "资源库连接名称(模糊查询)", paramType = "query", dataType = "String")
    })
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public Response<List<RepositoryDatabaseResponse>> list(@ApiIgnore RepositoryDatabaseRequest request)  {
        List<RepositoryDatabaseResponse> list = repositoryDatabaseService.getRepositoryDatabases(request);
        return Response.ok(list) ;
    }


    /**
     * 新建资源库
     * @param request
     * @return
     */
    @ApiOperation(value = "新建资源库数据库链接", notes = "新建资源库数据库链接", httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 200009, message = "数据库连接验证失败"),
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Response<String> create(
            @RequestBody JSONObject request) throws KettleDatabaseException {

        DatabaseMeta databaseMeta =  DatabaseCodec.decode(request);

        RepositoryDatabaseRequest databaseRequest = DatabaseCodec.decode(databaseMeta);

        RepositoryDatabaseResponse response = repositoryDatabaseService.save(databaseRequest);

        return Response.ok(response.getRepositoryConnectionName()) ;
    }






    /**
     * 生成数据库初始化数据库脚本
     *
     * @param request
     * @throws IOException
     * @throws KettleException
     */
    @ApiOperation(value = "生成数据库初始化数据库脚本", notes = "生成数据库初始化数据库脚本", httpMethod = "POST")
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/initSQL")
    protected Response<String> initSQL(@RequestBody JSONObject request) throws IOException, KettleException {

        StringBuffer sql = new StringBuffer();
        KettleDatabaseRepositoryMeta repositoryMeta= (KettleDatabaseRepositoryMeta) RepositoryCodec.decode(request);
        JSONObject extraOptions  = request.optJSONObject("extraOptions");
        String connectionName = extraOptions.optString("database");

        RepositoryDatabaseResponse databaseResponse = repositoryDatabaseService.getByConnectionName(connectionName);

        DatabaseMeta databaseMeta =  DatabaseCodec.decode(databaseResponse);


        repositoryMeta.setConnection(databaseMeta);

        if ( repositoryMeta.getConnection() != null ) {

            Database db = new Database( loggingObject, databaseMeta );
            db.connect(null);
            String userTableName = databaseMeta.quoteField(KettleDatabaseRepository.TABLE_R_USER);
            boolean upgrade = db.checkTableExists( userTableName );

            KettleDatabaseRepository rep = (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class,  repositoryMeta, Repository.class );

            rep.init( repositoryMeta );

            ArrayList<String> statements = new ArrayList<String>();
            rep.connectionDelegate.connect(true, true);
            rep.createRepositorySchema(null, upgrade, statements, true);


            sql.append( "-- Repository creation/upgrade DDL: " ).append( Const.CR );
            sql.append( "--" ).append( Const.CR );
            sql.append( "-- Nothing was created nor modified in the target repository database." ).append( Const.CR );
            sql.append( "-- Hit the OK button to execute the generated SQL or Close to reject the changes." ).append( Const.CR );
            sql.append( "-- Please note that it is possible to change/edit the generated SQL before execution." ).append( Const.CR );
            sql.append( "--" ).append( Const.CR );
            for (String statement : statements) {
                if (statement.endsWith(";")) {
                    sql.append(statement).append(Const.CR);
                } else {
                    sql.append(statement).append(";").append(Const.CR).append(Const.CR);
                }
            }
        }

       return Response.ok(sql.toString());
    }

    /**
     * 根据Id查询用户
     * @param connectionName
     * @return
     */
    @ApiOperation(value = "根据连接名获取详情", notes = "根据连接名获取详情", httpMethod = "GET")
    @RequestMapping(value = "get/{connectionName}/", method = RequestMethod.GET)
    public Response<DatabaseMeta> get(
            @ApiParam(value = "数据库连接名称", required = true)  @PathVariable String connectionName) throws KettleDatabaseException {
        RepositoryDatabaseResponse databaseResponse = repositoryDatabaseService.getByConnectionName(connectionName);

        DatabaseMeta databaseMeta = DatabaseCodec.decode(databaseResponse);

        return Response.ok(databaseMeta) ;
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


    public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("RepositoryDatabaseController", LoggingObjectType.DATABASE, null );

}

