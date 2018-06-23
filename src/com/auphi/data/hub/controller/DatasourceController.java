/**
 *
 */
package com.auphi.data.hub.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.DatabaseCodec;
import com.auphi.data.hub.core.DatabaseType;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.*;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.DatasourceService;
import com.auphi.ktrl.engine.KettleEngine;
import com.auphi.ktrl.engine.impl.KettleEngineImpl4_3;
import com.auphi.ktrl.schedule.util.DatabaseUtil;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.util.Constants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.StringUtil;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.ui.database.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据源配置控制器
 *
 * @author zhangfeng
 *
 */
@Controller("datasource")
public class DatasourceController extends BaseMultiActionController {

    private static Log log = LogFactory.getLog(ServiceController.class);

    private final static String INDEX = "admin/datasource";

    @Autowired
    private DatasourceService datasourceService;

    public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
        return new ModelAndView(INDEX);
    }


    public ModelAndView list(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{

        Dto<String,Object> dto = new BaseDto();
        String querySourceName = req.getParameter("queryParam");
        //dto.put("queryParam", querySourceName);
        int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
        System.out.println("repId=" + repId);
        this.setPageParam(dto, req);
        String start= dto.getAsString("start");
        String end= dto.getAsString("end");
        log.info("start="+start);
        log.info("end="+end);
        Integer total=null;
        Connection connection=null;
        PreparedStatement smt=null;
        ResultSet rs=null;
        try {
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            connection = KettleEngineImpl4_3.getRepConnection(rep);

            String sql = "SELECT COUNT(ID_DATABASE) AS TOTAL FROM"+
                    "(SELECT ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME FROM R_DATABASE ";
            if(null!=querySourceName&&""!=querySourceName){
                sql+="WHERE NAME like '%" +querySourceName+"%'";
            }
            sql+="ORDER BY ID_DATABASE desc) D";

            smt=  connection.prepareStatement(sql);
            rs=smt.executeQuery();
            while (rs.next()) {
                total=Integer.valueOf(rs.getInt("TOTAL"));
            }
            log.info("total="+total);
            rs.close();
            smt.close();

            String sql2 = "SELECT * FROM (SELECT ID_DATABASE , NAME , USERNAME , PASSWORD , a.ID_DATABASE_TYPE AS ID_DATABASE_TYPE ,  b.DESCRIPTION   AS DESCRIPTION ,ID_DATABASE_CONTYPE , DATABASE_NAME , PORT , HOST_NAME FROM R_DATABASE a LEFT JOIN R_DATABASE_TYPE b ON  a.ID_DATABASE_TYPE = b.ID_DATABASE_TYPE ";
            if(null!=querySourceName&&""!=querySourceName){
                sql2 +="WHERE NAME like '%" +querySourceName+"%'";
            }
            sql2 +="ORDER BY ID_DATABASE desc) D limit "+start+","+end;
            smt=  connection.prepareStatement(sql2);
            rs=smt.executeQuery();
            List<Datasource> items = new ArrayList<Datasource>();
            while (rs.next()) {
                Datasource datasource=new Datasource();
                Integer database_id=Integer.valueOf(rs.getInt("ID_DATABASE"));
                datasource.setSourceId(database_id);
                String  database_sourcename=rs.getString("NAME");
                datasource.setSourceName(database_sourcename);
                String  database_user=rs.getString("USERNAME");
                datasource.setSourceUserName(database_user);
                String  database_pwd=rs.getString("PASSWORD");
                datasource.setSourcePassword(database_pwd);
                Integer database_type=Integer.valueOf(rs.getInt("ID_DATABASE_TYPE"));

                datasource.setSourceType(database_type);

                String description = rs.getString("DESCRIPTION");
                datasource.setSourceTypeName(description);

                String database_name=rs.getString("DATABASE_NAME");
                datasource.setSourceDataBaseName(database_name);
                String database_port=rs.getString("PORT");
                datasource.setSourcePort(database_port);
                String  database_host=rs.getString("HOST_NAME");
                datasource.setSourceIp(database_host);
                items.add(datasource);
            }
            rs.close();
            smt.close();
            connection.close();

            PaginationSupport<Datasource> page = new PaginationSupport<Datasource>(items, total);
            String jsonString = JsonHelper.encodeObject2Json(page);
            write(jsonString, resp);
        } catch (Exception e) {
            e.printStackTrace();
        }finally
        {
            if(rs != null){
                rs.close();
            }
            if(smt != null){
                smt.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return null;
    }


    public ModelAndView saveSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException, SQLException{
        Integer id = null;
        Connection connection = null;
        Statement smt = null;
        PreparedStatement pstmt = null;
        ResultSet rs=null;
        try{
            int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
            System.out.println("repId=" + repId);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            connection = KettleEngineImpl4_3.getRepConnection(rep);

            smt=connection.createStatement();
            rs=smt.executeQuery("SELECT MAX(ID_DATABASE) AS MAXID FROM R_DATABASE");
            while (rs.next()) {
                id=Integer.valueOf(rs.getInt("MAXID")+1);
            }
            log.info("sourceId="+id);
            rs.close();
            smt.close();

            connection.setAutoCommit(false);
            String sql = "INSERT INTO R_DATABASE (ID_DATABASE,NAME,USERNAME,PASSWORD,ID_DATABASE_TYPE,ID_DATABASE_CONTYPE,DATABASE_NAME,PORT,HOST_NAME) values (?,?,?,?,?,?,?,?,?)";
            pstmt=  connection.prepareStatement(sql);
            pstmt.setObject(1, id);
            pstmt.setObject(2, source.getSourceName());
            pstmt.setObject(3, source.getSourceUserName());
            pstmt.setObject(4, Encr.encryptPasswordIfNotUsingVariables(source.getSourcePassword()));
            pstmt.setObject(5, source.getSourceType());
            pstmt.setObject(6, "1");
            pstmt.setObject(7, source.getSourceDataBaseName());
            pstmt.setObject(8, source.getSourcePort());
            pstmt.setObject(9, source.getSourceIp());
            pstmt.execute();
            connection.commit();
            connection.setAutoCommit(true);
            pstmt.close();

            this.setOkTipMsg("数据源保存成功！", resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg("添加数据源失败请检查后重新添加！", resp);
        }finally{
            if(rs != null){
                rs.close();
            }
            if(smt != null){
                smt.close();
            }
            if(pstmt != null){
                pstmt.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return null;
    }


    public ModelAndView updateSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException, SQLException{
        Connection connection=null;
        PreparedStatement smt=null;
        try{
            int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
            System.out.println("repId=" + repId);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            connection = KettleEngineImpl4_3.getRepConnection(rep);

            connection.setAutoCommit(false);
            String sql = "UPDATE R_DATABASE SET NAME=?,USERNAME=?,PASSWORD=?,ID_DATABASE_TYPE=? ,DATABASE_NAME=?,PORT=?,HOST_NAME=? WHERE ID_DATABASE=?";
            smt=  connection.prepareStatement(sql);
            smt.setObject(1, source.getSourceName());
            smt.setObject(2, source.getSourceUserName());
            smt.setObject(3, Encr.encryptPasswordIfNotUsingVariables(source.getSourcePassword()));
            smt.setObject(4, source.getSourceType());
            smt.setObject(5, source.getSourceDataBaseName());
            smt.setObject(6, source.getSourcePort());
            smt.setObject(7, source.getSourceIp());
            smt.setObject(8, source.getSourceId());
            smt.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
            smt.close();
            connection.close();

            this.setOkTipMsg("数据源修改成功！", resp);
        } catch(Exception e){
            e.printStackTrace();
            this.setFailTipMsg("添加数据源失败请检查后重新编辑！", resp);
        }finally{
            if(smt != null){
                smt.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return null;
    }


    /**
     * 删除数据库
     * @param req
     * @param resp
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public ModelAndView deleteSource(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{

        String idDatabases = ServletRequestUtils.getStringParameter(req,"strChecked","");
        int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
        RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
        KettleEngine kettleEngine = new KettleEngineImpl4_3();
        Repository rep = null;
        JsonUtils.putResponse(resp);
        try{
            rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            String[] names = idDatabases.split(",");
            for(String name : names){
                rep.deleteDatabaseMeta(name);
            }
            rep.disconnect();

            JsonUtils.success("删除成功");
        }catch (Exception e){
            e.printStackTrace();
            if(rep !=null){
                rep.disconnect();
            }
            JsonUtils.fail("数据库删除失败\n\r"+e.getMessage());
        }

        return null;

    }

    public ModelAndView getDbTypeList(HttpServletRequest req,HttpServletResponse resp) throws IOException, SQLException{
        List<Dto<?,?>> dbTypeList = new ArrayList<Dto<?,?>>();
        Connection connection=null;
        PreparedStatement smt=null;
        ResultSet rs = null;
        try{
            String sql = "SELECT ID_DATABASE_TYPE AS DBTYPE,DESCRIPTION FROM R_DATABASE_TYPE";

            int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
            System.out.println("repId=" + repId);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            connection = KettleEngineImpl4_3.getRepConnection(rep);

            smt=  connection.prepareStatement(sql);
            rs=smt.executeQuery();
            while (rs.next()) {
                Dto<String,Object> dto1 = new BaseDto();
                String dbType = String.valueOf(rs.getInt("DBTYPE"));
                String dbTypeName = rs.getString("DESCRIPTION");
                dto1.put("sourceType",dbType);
                dto1.put("sourceTypeName",dbTypeName);
                dbTypeList.add(dto1);
            }
            rs.close();
            smt.close();
            connection.close();

            String jsonString = JsonHelper.encodeObject2Json(dbTypeList);
            write(jsonString, resp);
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }finally{
            if(rs != null){
                rs.close();
            }
            if(smt != null){
                smt.close();
            }
            if(connection != null){
                connection.close();
            }
        }
        return null;
    }

    public ModelAndView getRepList(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        List<Dto<?,?>> dbTypeList = new ArrayList<Dto<?,?>>();
        try{
            UserBean userBean = req.getSession().getAttribute("userBean")==null?null:(UserBean)req.getSession().getAttribute("userBean");
            List<RepositoryBean> listReps = RepositoryUtil.getAllRepositories(userBean);

            for (RepositoryBean repBean : listReps) {
                Dto<String,Object> dto1 = new BaseDto();
                dto1.put("repId",repBean.getRepositoryID());
                dto1.put("repName",repBean.getRepositoryName());
                dbTypeList.add(dto1);
            }
            String jsonString = JsonHelper.encodeObject2Json(dbTypeList);
            write(jsonString, resp);
        }catch(Exception e){
            log.error(e.getMessage(), e);
        }
        return null;
    }



    /**
     * 测试数据库连接
     * @param req
     * @param resp
     * @param source
     * @return
     * @throws IOException
     */
    public ModelAndView testSource(HttpServletRequest req,HttpServletResponse resp,Datasource source) throws IOException{
        Connection connection = null;
        PreparedStatement smt = null;
        ResultSet rs = null;
        String typeName = "";
        try{
            int repId = req.getParameter("repId")==null?0:Integer.parseInt(req.getParameter("repId"));
            System.out.println("repId=" + repId);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            Object rep = kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            connection = KettleEngineImpl4_3.getRepConnection(rep);
            smt=  connection.prepareStatement("SELECT *FROM R_DATABASE_TYPE WHERE ID_DATABASE_TYPE =?");
            smt.setInt(1, Integer.valueOf(source.getSourceType()));
            rs=smt.executeQuery();
            while (rs.next()) {
                typeName=rs.getString("DESCRIPTION");
            }
            source.setSourceTypeName(typeName);
            if(!checkDatasource(source)){
                this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
            } else {
                this.setOkTipMsg("数据源验证已通过，连接成功！", resp);
            }
        } catch(Exception e){
            log.error(e.getMessage(), e);
            this.setFailTipMsg("数据源验证没有通过,请检查你的参数！", resp);
        }
        return null;
    }


    private boolean checkDatasource(Datasource source) throws Exception{
        try{
            DatabaseMeta databaseMeta = new DatabaseMeta(source.getSourceName(),source.getSourceTypeName(),"Native",source.getSourceIp(),
                    source.getSourceDataBaseName(),source.getSourcePort(),source.getSourceUserName(),source.getSourcePassword());
            @SuppressWarnings("deprecation")
            Database database = new Database(databaseMeta);
            database.connect();
            database.disconnect();
            return true;
        }catch(Exception e){
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public ModelAndView getDataSourceList(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        try {
            List<Datasource> list = datasourceService.querySourceList();
            String jsonString = JsonHelper.encodeObject2Json(list);
            write(jsonString, resp);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取支持的数据库类型，如Oracle,MySQL等
     * @throws IOException
     */

    public ModelAndView accessData(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        com.auphi.data.hub.core.util.JSONArray jsonArray = com.auphi.data.hub.core.util.JSONArray.fromObject(DatabaseType.instance().loadSupportedDatabaseTypes());
        String jsonString = JsonHelper.encodeObject2Json(jsonArray);
        write(jsonString, resp);
        return null;
    }



    /**
     * 通过数据库类型获取访问方式：如JNDI、JDBC还是ODBC
     *
     * @throws IOException
     */
    public ModelAndView accessMethod(HttpServletRequest req,HttpServletResponse resp) throws IOException {

        //数据库类型
        String accessData = ServletRequestUtils.getStringParameter(req,"accessData","");

        com.auphi.data.hub.core.util.JSONArray  jsonArray = com.auphi.data.hub.core.util.JSONArray .fromObject(DatabaseType.instance().loadSupportedDatabaseMethodsByTypeId(accessData));
        String jsonString = JsonHelper.encodeObject2Json(jsonArray);
        write(jsonString, resp);
        return null;
    }

    public ModelAndView accessSettings(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        //数据库类型
        String accessData = ServletRequestUtils.getStringParameter(req,"accessData","");

        int accessMethod = ServletRequestUtils.getIntParameter(req,"accessMethod",-1);

        String databaseName = PluginRegistry.getInstance().getPlugin( DatabasePluginType.class, accessData).getIds()[0];

        String fragment = "";
        switch ( accessMethod ) {
            case DatabaseMeta.TYPE_ACCESS_JNDI:
                ClassPathResource cpr = new ClassPathResource(databaseName + "_jndi.json", getClass());
                if(!cpr.exists())
                    cpr = new ClassPathResource("common_jndi.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_NATIVE:
                cpr = new ClassPathResource(databaseName + "_native.json", getClass());
                if(!cpr.exists())
                    cpr = new ClassPathResource("common_native.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_ODBC:
                cpr = new ClassPathResource(databaseName + "_odbc.json", getClass());
                if(!cpr.exists())
                    cpr = new ClassPathResource("common_odbc.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_OCI:
                cpr = new ClassPathResource(databaseName + "_oci.json", getClass());
                if(!cpr.exists())
                    cpr = new ClassPathResource("common_oci.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
            case DatabaseMeta.TYPE_ACCESS_PLUGIN:
                cpr = new ClassPathResource(databaseName + "_plugin.json", getClass());
                if (!cpr.exists())
                    cpr = new ClassPathResource("common_plugin.json", getClass());
                fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8");
                break;
        }
        JsonUtils.putResponse(resp);
        JsonUtils.success(fragment);
        return null;
    }



    /**
     * 根据databaseName加载该数据库的所有配置信息
     *
     * @throws Exception
     */
    public ModelAndView database(HttpServletRequest req,HttpServletResponse resp) throws Exception {

        //数据库标识
        String name = ServletRequestUtils.getStringParameter(req,"name","");

        RepositoriesMeta input = new RepositoriesMeta();
        DatabaseMeta databaseMeta = null;
        int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
        RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
        Repository rep = null;
        try {

            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));

            databaseMeta = rep.loadDatabaseMeta(rep.getDatabaseID(name),null);
            rep.disconnect();
        }catch (Exception e){
            e.printStackTrace();
            if(rep !=null){
                rep.disconnect();
            }

        }

        if(databaseMeta == null)
            databaseMeta = new DatabaseMeta();

        JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);
        String jsonString = JsonHelper.encodeObject2Json(jsonObject);
        write(jsonString, resp);
        return null;
    }

    /**
     * 保存之前的后台校验，对一些非空选项进行检查
     *
     * @throws IOException
     * @throws KettleException
     */
    public ModelAndView check(HttpServletRequest req,HttpServletResponse resp ) throws IOException, KettleException {
        String databaseInfo = ServletRequestUtils.getStringParameter(req,"databaseInfo","");
        JsonUtils.putResponse(resp);
        JSONObject result = new JSONObject();

        checkDatabase(databaseInfo, result);
        if(result.size() > 0) {
            JsonUtils.fail(result.toString());
            return null;
        }

        JsonUtils.success(result.toString());

        return null;
    }

    /**
     * 测试数据库
     *
     * @throws IOException
     * @throws KettleDatabaseException
     */
    public void test(HttpServletRequest req,HttpServletResponse resp) throws IOException, KettleDatabaseException {
        JsonUtils.putResponse(resp);
        String databaseInfo = ServletRequestUtils.getStringParameter(req,"databaseInfo","");
        JSONObject jsonObject = JSONObject.fromObject(databaseInfo);
        DatabaseMeta dbinfo = DatabaseCodec.decode(jsonObject);
        String[] remarks = dbinfo.checkParameters();
        if ( remarks.length == 0 ) {
            String reportMessage = dbinfo.testConnection();
            JsonUtils.success(StringEscapeHelper.encode(reportMessage));
        } else {
            JsonUtils.fail("测试失败，未知错误！");
        }
    }


    /**
     * 持久化数据库信息
     *
     * @throws IOException
     * @throws KettleException
     */

    public void create(HttpServletRequest req,HttpServletResponse resp) throws IOException, KettleException {



        String databaseInfo = ServletRequestUtils.getStringParameter(req,"databaseInfo","");
        JsonUtils.putResponse(resp);
        JSONObject result = new JSONObject();

        DatabaseMeta database = checkDatabase(databaseInfo, result);
        if(result.size() > 0) {
            JsonUtils.fail(result.toString());
            return;
        }
        int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
        RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
        Repository rep = null;
        try {

            KettleEngine kettleEngine = new KettleEngineImpl4_3();
            rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));

            rep.save(database, Const.VERSION_COMMENT_EDIT_VERSION, null);
            rep.disconnect();


            JsonUtils.success(database.getName());
        }catch (Exception e){
            e.printStackTrace();
            if(rep !=null){
                rep.disconnect();
            }
            JsonUtils.fail("添加失败");
        }


    }

    private DatabaseMeta checkDatabase(String databaseInfo, JSONObject result) throws KettleDatabaseException, IOException {
        JSONObject jsonObject = JSONObject.fromObject(databaseInfo);
        DatabaseMeta database = DatabaseCodec.decode(jsonObject);

        if(database.isUsingConnectionPool()) {
            String parameters = "";
            JSONArray pool_params = jsonObject.optJSONArray("pool_params");
            if(pool_params != null) {
                for(int i=0; i<pool_params.size(); i++) {
                    JSONObject jsonObject2 = pool_params.getJSONObject(i);
                    Boolean enabled = jsonObject2.optBoolean("enabled");
                    String parameter = jsonObject2.optString("name");
                    String value = jsonObject2.optString("defValue");

                    if (!enabled) {
                        continue;
                    }

                    if(!StringUtils.hasText(value) ) {
                        parameters = parameters.concat( parameter ).concat( System.getProperty( "line.separator" ) );
                    }
                }

            }

            if(parameters.length() > 0) {
                String message = Messages.getString( "DataHandler.USER_INVALID_PARAMETERS" ).concat( parameters );
                result.put("message", message);
                return database;
            }
        }

        String[] remarks = database.checkParameters();
        String message = "";

        if ( remarks.length != 0 ) {
            for (int i = 0; i < remarks.length; i++) {
                message = message.concat("* ").concat(remarks[i]).concat(System.getProperty("line.separator"));
            }
            result.put("message", message);

            return database;
        }

        return database;
    }

    public ModelAndView databases(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        try {
            JsonUtils.putResponse(resp);
            int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            Repository rep = null;

            try {
                KettleEngine kettleEngine = new KettleEngineImpl4_3();
                rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));

//
//
//					List<Map<String,String>> list = Lists.newArrayList();
//					for(String name:names){
//						Map<String,String> map = Maps.newHashMap();
//						map.put("sourceId",name);
//						map.put("sourceName",name);
//						list.add(map);
//					}

                //tony 20171213

                String names[] = rep.getDatabaseNames(false);
                List<JSONObject> databaseMetas = Lists.newArrayList();

                for(String name:names){
                    DatabaseMeta databaseMeta = rep.loadDatabaseMeta(rep.getDatabaseID(name),null);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("sourceName",databaseMeta.getName());
                    jsonObject.put("databaseName",databaseMeta.getDatabaseName());
                    jsonObject.put("sourceId",databaseMeta.getObjectId().getId());
                    databaseMetas.add(jsonObject);

                }

                rep.disconnect();
                String jsonString = JsonHelper.encodeObject2Json(databaseMetas);
                write(jsonString, resp);

            }catch (Exception e){
                e.printStackTrace();
                if(rep !=null){
                    rep.disconnect();
                }
                JsonUtils.fail("获取失败！"+e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public ModelAndView schemaNames(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        try {
            JsonUtils.putResponse(resp);
            int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
            String databaseId = ServletRequestUtils.getStringParameter(req,"databaseId","");
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            Repository rep = null;
            Database database = null;

            try {

                KettleEngine kettleEngine = new KettleEngineImpl4_3();
                rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));

                if(!StringUtil.isEmpty(databaseId)){
                    Long objectId = Long.valueOf(databaseId);

                    DatabaseMeta databaseMeta = rep.loadDatabaseMeta(new LongObjectId(objectId),null);

                    database = new Database(BaseStepDialog.loggingObject,databaseMeta);
                    database.connect();

                    String names[] = database.getSchemas();

                    List<Map<String,String>> list = Lists.newArrayList();
                    for(String name:names){
                        Map<String,String> map = Maps.newHashMap();
                        map.put("value",name);
                        map.put("text",name);
                        list.add(map);
                    }

                    rep.disconnect();
                    database.disconnect();
                    String jsonString = JsonHelper.encodeObject2Json(list);
                    write(jsonString, resp);

                }

                JsonUtils.fail("获取失败");

            }catch (Exception e){
                e.printStackTrace();
                if(rep !=null){
                    rep.disconnect();
                }
                if(database !=null){
                    database.disconnect();
                }

                JsonUtils.fail("获取失败！"+e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public ModelAndView tables(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        try {
            JsonUtils.putResponse(resp);
            int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
            String databaseId = ServletRequestUtils.getStringParameter(req,"databaseId","");
            String schemanamein = ServletRequestUtils.getStringParameter(req,"schemanamein",null);
            String tableName = ServletRequestUtils.getStringParameter(req,"tableName",null);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            Repository rep = null;
            Database database = null;
            RepositoriesMeta repositories = new RepositoriesMeta();

            try {

                KettleEngine kettleEngine = new KettleEngineImpl4_3();
                rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));


                if(!StringUtil.isEmpty(databaseId)){
                    Long objectId = Long.valueOf(databaseId);

                    DatabaseMeta databaseMeta = rep.loadDatabaseMeta(new LongObjectId(objectId),null);

                    database = new Database(databaseMeta);
                    database.connect();


                    String names[] = database.getTablenames(schemanamein,false);

                    List<Map<String,Object>> list = Lists.newArrayList();
                    for(String name:names){
                        Map<String,Object> map = Maps.newHashMap();
                        map.put("text",name);
                        map.put("pid",-1);
                        map.put("leaf",true);
                        map.put("checked",!StringUtil.isEmpty(tableName) && tableName.contains(name));
                        list.add(map);
                    }


                    rep.disconnect();
                    database.disconnect();
                    String jsonString = JsonHelper.encodeObject2Json(list);
                    write(jsonString, resp);

                }

                JsonUtils.fail("获取失败！");

            }catch (Exception e){
                e.printStackTrace();
                if(rep !=null){
                    rep.disconnect();
                }
                if(database !=null){
                    database.disconnect();
                }

                JsonUtils.fail("获取失败！"+e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            JsonUtils.fail("获取失败！"+e.getMessage());
        }
        return null;
    }


    public ModelAndView tablefields(HttpServletRequest req,HttpServletResponse resp) throws IOException{
        try {
            Map<String,String> res = Maps.newHashMap();
            int repId = ServletRequestUtils.getIntParameter(req,"repId",-1);
            String databaseId = ServletRequestUtils.getStringParameter(req,"databaseId","");
            String schemanamein = ServletRequestUtils.getStringParameter(req,"schemanamein",null);
            String tablename = ServletRequestUtils.getStringParameter(req,"tablename",null);
            RepositoryBean repBean = RepositoryUtil.getRepositoryByID(repId);
            Repository rep = null;
            Database database = null;

            try {


                KettleEngine kettleEngine = new KettleEngineImpl4_3();
                rep = (Repository) kettleEngine.getRepFromDatabase(repBean.getRepositoryName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));

                if(!StringUtil.isEmpty(databaseId)){
                    Long objectId = Long.valueOf(databaseId);

                    DatabaseMeta databaseMeta = rep.loadDatabaseMeta(new LongObjectId(objectId),null);

                    database = new Database(databaseMeta);
                    database.connect();


                    String schemaTable = databaseMeta.getQuotedSchemaTableCombination(schemanamein, tablename);

                    RowMetaInterface rowMeta = database.getTableFields(schemaTable);

                    String destTablefields = null;

                    if(rowMeta!=null){
                        String[] fieldNames = rowMeta.getFieldNames();
                        destTablefields = org.apache.commons.lang.StringUtils.join(fieldNames, ",");
                        res.put("destTablefields", destTablefields);
                    }

                    String[] keys = DatabaseUtil.getPrimaryKeyColumnNames(database,schemanamein,tablename);

                    String destTableKey =  (keys!=null && keys.length > 0) ? org.apache.commons.lang.StringUtils.join(keys, ",") :destTablefields;

                    res.put("destTableKey", destTableKey);
                    setOkTipMsg("Success",res,resp);
                    return null;

                }
                setFailTipMsg("获取失败！", resp);

            }catch (Exception e){
                e.printStackTrace();
                if(rep !=null){
                    rep.disconnect();
                }
                if(database !=null){
                    database.disconnect();
                }

                setFailTipMsg(e.getMessage(), resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
            setFailTipMsg(e.getMessage(), resp);
        }
        return null;
    }



}
