package com.aofei.kettle.core.database;

import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.App;
import com.aofei.kettle.bean.DatabaseNode;
import com.aofei.kettle.repository.RepositoryCodec;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.JsonUtils;
import com.aofei.kettle.utils.StringEscapeHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.DBCache;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.*;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.logging.LoggingObjectType;
import org.pentaho.di.core.logging.SimpleLoggingObject;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.ui.core.database.dialog.SQLEditor;
import org.pentaho.di.ui.repository.dialog.RepositoryDialogInterface;
import org.pentaho.ui.database.Messages;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping(value="/database")
@Api(tags = "数据源连接 - 接口api")
public class KettleDatabaseController {

	@ApiOperation(value = "该方法返回所有全局的数据库连接名称，只返回连接名称", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/listNames")
	protected void listNames() throws KettleException, IOException {
		Repository repository = App.getInstance().getRepository();
		String[] strings =  repository.getDatabaseNames(true);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < strings.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", strings[i]);
			jsonArray.add(jsonObject);
		}
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取资源库内的一个数据库连接信息", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "连接名称，非必填，不填就创建个默认的连接", required=false, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/load")
	protected void database(@RequestParam(required=false) String name) throws Exception {
		DatabaseMeta databaseMeta = null;
		if(StringUtils.hasText(name)) {
			Repository repository = App.getInstance().getRepository();
			ObjectId id_database = repository.getDatabaseID(name);
			databaseMeta = repository.loadDatabaseMeta(id_database, null);
		}
		if(databaseMeta == null)
			databaseMeta = new DatabaseMeta();

		JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);
		JsonUtils.response(jsonObject);
	}

	@ApiOperation(value = "清除数据库缓存，有时在数据库新建表后KETTLE获取不到，需要先清下缓存", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "连接名称，非必填", required=false, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/clearDBCache")
	protected void clearDBCache(@RequestParam(required=false) String name) throws KettleException, IOException {
		if(StringUtils.hasText(name))
			DBCache.getInstance().clear( name );
		else
			DBCache.getInstance().clear( null );

		JsonUtils.success("缓存清除成功！");
	}

	@ApiOperation(value = "获取支持的数据库类型，如Oracle,MySQL等", httpMethod = "POST")
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/accessData")
	public void loadAccessData() throws IOException {
		JSONArray jsonArray = JSONArray.fromObject(DatabaseType.instance().loadSupportedDatabaseTypes());
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "通过数据库类型获取访问方式：如JNDI、JDBC还是ODBC", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "accessData", value = "数据库类型", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/accessMethod")
	public void loadAccessMethod(@RequestParam String accessData) throws IOException {
		JSONArray jsonArray = JSONArray.fromObject(DatabaseType.instance().loadSupportedDatabaseMethodsByTypeId(accessData));
		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取数据库配置面板，需要数据库类型及访问方式确定，主要包含了URL、username、password、端口等信息", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "accessData", value = "数据库类型", required=true, paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "accessMethod", value = "数据库访问方式", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/accessSettings")
	public void loadAccessSettings(@RequestParam String accessData, @RequestParam int accessMethod) throws IOException {
	    String databaseName = PluginRegistry.getInstance().getPlugin( DatabasePluginType.class, accessData).getIds()[0];
	    databaseName = databaseName.toLowerCase();

	    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	    String fragment = "";
	    switch ( accessMethod ) {
	    case DatabaseMeta.TYPE_ACCESS_JNDI:
	    	Resource[] resources = resolver.getResources("classpath*:jdbc/" + databaseName + "_jndi.json");
	    	if(resources.length == 0)
	    		resources = resolver.getResources("classpath*:jdbc/common_jndi.json");

	    	InputStream input = resources[0].getInputStream();
	    	fragment = IOUtils.toString(input, "utf-8");
	    	IOUtils.closeQuietly(input);

	    	break;
	    case DatabaseMeta.TYPE_ACCESS_NATIVE:
	    	resources = resolver.getResources("classpath*:jdbc/" + databaseName + "_native.json");
	    	if(resources.length == 0)
	    		resources = resolver.getResources("classpath*:jdbc/common_native.json");

	    	input = resources[0].getInputStream();
	    	fragment = IOUtils.toString(input, "utf-8");
	    	IOUtils.closeQuietly(input);

	    	break;
	    case DatabaseMeta.TYPE_ACCESS_ODBC:
	    	resources = resolver.getResources("classpath*:jdbc/" + databaseName + "_odbc.json");
	    	if(resources.length == 0)
	    		resources = resolver.getResources("classpath*:jdbc/common_odbc.json");

	    	input = resources[0].getInputStream();
	    	fragment = IOUtils.toString(input, "utf-8");
	    	IOUtils.closeQuietly(input);

	    	break;
	    case DatabaseMeta.TYPE_ACCESS_OCI:
	    	resources = resolver.getResources("classpath*:jdbc/" + databaseName + "_oci.json");
	    	if(resources.length == 0)
	    		resources = resolver.getResources("classpath*:jdbc/common_oci.json");

	    	input = resources[0].getInputStream();
	    	fragment = IOUtils.toString(input, "utf-8");
	    	IOUtils.closeQuietly(input);

	    	break;
	      case DatabaseMeta.TYPE_ACCESS_PLUGIN:
			resources = resolver.getResources("classpath*:jdbc/" + databaseName + "_plugin.json");
	    	if(resources.length == 0)
	    		resources = resolver.getResources("classpath*:jdbc/common_plugin.json");

	    	input = resources[0].getInputStream();
	    	fragment = IOUtils.toString(input, "utf-8");
	    	IOUtils.closeQuietly(input);

			break;
	    }

	    JsonUtils.success(fragment);
	}

	@ApiOperation(value = "测试数据库", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库信息", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/test")
	public void test(@RequestParam String databaseInfo) throws IOException, KettleDatabaseException {
	    JSONObject jsonObject = JSONObject.fromObject(databaseInfo);
	    DatabaseMeta dbinfo = DatabaseCodec.decode(jsonObject);
	    String[] remarks = dbinfo.checkParameters();
	    if ( remarks.length == 0 ) {
	    	String reportMessage = dbinfo.testConnection();
			JsonUtils.success(StringEscapeHelper.encode(reportMessage));
	    } else {
	    	System.out.println("====");
	    }
	}

	@RequestMapping(method=RequestMethod.POST, value="/features")
	public @ResponseBody Map features(@RequestParam String databaseInfo) throws KettleValueException, IOException, KettleDatabaseException {
	    JSONObject jsonObject = JSONObject.fromObject(databaseInfo);
	    DatabaseMeta dbinfo = DatabaseCodec.decode(jsonObject);
	    List<RowMetaAndData> buffer = dbinfo.getFeatureSummary();

	    HashMap result = new HashMap();
	    if ( buffer.size() > 0 ) {
	    	RowMetaInterface rowMeta = buffer.get( 0 ).getRowMeta();
			List<ValueMetaInterface> valueMetas = rowMeta.getValueMetaList();

			JSONArray columns = new JSONArray();
			JSONObject metaData = new JSONObject();
			JSONArray fields = new JSONArray();
			for (int i = 0; i < valueMetas.size(); i++) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(i);
				fields.add(valueMeta.getName());

				JSONObject column = new JSONObject();
				column.put("dataIndex", valueMeta.getName());
				column.put("width", 480);
				column.put("header", valueMeta.getComments() == null ? valueMeta.getName() : valueMeta.getComments());
				columns.add(column);
			}
			metaData.put("fields", fields);
			metaData.put("root", "firstRecords");
			result.put("metaData", metaData);
			result.put("columns", columns);

			JSONArray firstRecords = new JSONArray();
	        for ( RowMetaAndData row : buffer ) {
	        	HashMap record = new HashMap();
	        	Object[] rowData = row.getData();

				for (int colNr = 0; colNr < rowMeta.size(); colNr++) {
					String string;
					ValueMetaInterface valueMetaInterface;
					try {
						valueMetaInterface = rowMeta.getValueMeta(colNr);
						if (valueMetaInterface.isStorageBinaryString()) {
							Object nativeType = valueMetaInterface.convertBinaryStringToNativeType((byte[]) rowData[colNr]);
							string = valueMetaInterface.getStorageMetadata().getString(nativeType);
						} else {
							string = rowMeta.getString(rowData, colNr);
						}
					} catch (Exception e) {
						string = "Conversion error: " + e.getMessage();
					}

					ValueMetaInterface valueMeta = rowMeta.getValueMeta( colNr );
					record.put(valueMeta.getName(), string);

				}
				firstRecords.add(record);
	        }

	        result.put("firstRecords", firstRecords);

	    }

	    return result;
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

	@ApiOperation(value = "保存之前的后台校验，对一些非空选项进行检查", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库信息", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/check")
	public void check(@RequestParam String databaseInfo) throws IOException, KettleException {
		JSONObject result = new JSONObject();

		checkDatabase(databaseInfo, result);
	    if(result.size() > 0) {
	    	JsonUtils.fail(result.toString());
			return;
	    }

	    JsonUtils.success(result.toString());
	}

	@ApiOperation(value = "校验数据库连接，如果校验无误就保存，失败就返回失败信息", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库连接信息，JSON串", paramType="query", dataType = "string")
	})
	@RequestMapping("/checkAndSave")
	public @ResponseBody void checkAndSave(@RequestParam String databaseInfo, @CurrentUser CurrentUserResponse user ) throws IOException, KettleException {

		JSONObject result = new JSONObject();

		DatabaseMeta databaseMeta = checkDatabase(databaseInfo, result);
	    if(result.size() > 0) {
	    	JsonUtils.fail(result.toString());
			return;
	    }
		databaseMeta.setOrganizerId(user.getOrganizerId());
		databaseMeta.setCreateUser(user.getUsername());
	    databaseMeta.setUpdateUser(user.getUsername());
	    Repository repository = App.getInstance().getRepository();
	    repository.save(databaseMeta, "add database", null);

	    JsonUtils.success(result.toString());
	}

	@ApiOperation(value = "持久化数据库信息，注意这里是持久化到全局，而不是资源库中", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库信息", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/create")
	public void create(@RequestParam String databaseInfo) throws IOException, KettleException {
		JSONObject result = new JSONObject();

		DatabaseMeta database = checkDatabase(databaseInfo, result);
	    if(result.size() > 0) {
			JsonUtils.fail(result.toString());
			return;
	    }

	    RepositoriesMeta repositories = new RepositoriesMeta();
	    if(repositories.readData()) {
	    	DatabaseMeta previousMeta = repositories.searchDatabase(database.getName());
	    	if(previousMeta != null) {
	    		repositories.removeDatabase(repositories.indexOfDatabase(previousMeta));
	    	}
	    	repositories.addDatabase( database );
	    	repositories.writeData();

	    	JsonUtils.success(database.getName());
	    }
	}

	@ApiOperation(value = "移除全局数据库信息", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseName", value = "数据库连接名称", required=true, paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/remove")
	public void remove(@RequestParam String databaseName) throws IOException, KettleException {
		JSONObject result = new JSONObject();


	    RepositoriesMeta repositories = new RepositoriesMeta();
	    if(repositories.readData()) {
	    	DatabaseMeta previousMeta = repositories.searchDatabase(databaseName);
	    	if(previousMeta != null) {
	    		repositories.removeDatabase(repositories.indexOfDatabase(previousMeta));
	    	}
	    	repositories.writeData();

	    	JsonUtils.success(result.toString());
	    }
	}

	/**
	 * 校验数据库环境，确定该数据库是否已经被初始化
	 *
	 * @param reposityInfo
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/checkInit")
	protected void checkInit(@RequestParam String connection) throws Exception {
		JSONObject jsonObject = new JSONObject();

		RepositoriesMeta input = new RepositoriesMeta();
		if (input.readData()) {
			DatabaseMeta database = input.searchDatabase(connection);
			if (database != null) {
				if (!database.getDatabaseInterface().supportsRepository()) {
					System.out.println("Show database type is not supported warning...");
					jsonObject.put("unSupportedDatabase", true);
				}

				System.out.println("Connecting to database for repository creation...");
				Database db = new Database( loggingObject, database );
				db.connect(null);

				String userTableName = database.quoteField(KettleDatabaseRepository.TABLE_R_USER);
				boolean upgrade = db.checkTableExists( userTableName );
				if (upgrade) {
					jsonObject.put("opertype", "update");
				} else {
					jsonObject.put("opertype", "create");
				}
			}
		}

		JsonUtils.response(jsonObject);
	}

	/**
	 * 生成数据库初始化数据库脚本
	 *
	 * @param reposityInfo
	 * @param upgrade
	 * @throws IOException
	 * @throws KettleException
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/initSQL")
	protected void initSQL(@RequestParam String reposityInfo, @RequestParam boolean upgrade) throws IOException, KettleException {
		JSONObject jsonObject = JSONObject.fromObject(reposityInfo);

		StringBuffer sql = new StringBuffer();
		KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) RepositoryCodec.decode(jsonObject);
		if ( repositoryMeta.getConnection() != null ) {
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

		JsonUtils.success(StringEscapeHelper.encode(sql.toString()));
	}


	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/execute")
	protected void execute(@RequestParam String reposityInfo, @RequestParam String script) throws Exception {
		JSONObject jsonObject = JSONObject.fromObject(reposityInfo);

		KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) RepositoryCodec.decode(jsonObject);
		if ( repositoryMeta.getConnection() != null ) {
			KettleDatabaseRepository rep = (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class,  repositoryMeta, Repository.class );
	        rep.init( repositoryMeta );

	        DatabaseMeta connection = repositoryMeta.getConnection();
	        StringBuffer message = new StringBuffer();

	        Database db = new Database( loggingObject, connection );
	        boolean first = true;
	        PartitionDatabaseMeta[] partitioningInformation = connection.getPartitioningInformation();

	        for ( int partitionNr = 0; first
	          || ( partitioningInformation != null && partitionNr < partitioningInformation.length ); partitionNr++ ) {
	          first = false;
	          String partitionId = null;
	          if ( partitioningInformation != null && partitioningInformation.length > 0 ) {
	        	  partitionId = partitioningInformation[partitionNr].getPartitionId();
	          }
	          try {
	        	  db.connect( partitionId );
	        	  List<SqlScriptStatement> statements = connection.getDatabaseInterface().getSqlScriptStatements( StringEscapeHelper.decode(script) );

	        	  int nrstats = 0;
	              for ( SqlScriptStatement sql : statements ) {
	                if ( sql.isQuery() ) {
	                  // A Query

	                  nrstats++;
	                    List<Object[]> rows = db.getRows( sql.getStatement(), 1000 );
	                    RowMetaInterface rowMeta = db.getReturnRowMeta();
	                    if ( rows.size() > 0 ) {
//	                      PreviewRowsDialog prd =
//	                        new PreviewRowsDialog( shell, ci, SWT.NONE, BaseMessages.getString(
//	                          PKG, "SQLEditor.ResultRows.Title", Integer.toString( nrstats ) ), rowMeta, rows );
//	                      prd.open();
	                    } else {
//	                      MessageBox mb = new MessageBox( shell, SWT.ICON_INFORMATION | SWT.OK );
//	                      mb.setMessage( BaseMessages.getString( PKG, "SQLEditor.NoRows.Message", sql ) );
//	                      mb.setText( BaseMessages.getString( PKG, "SQLEditor.NoRows.Title" ) );
//	                      mb.open();
	                    }
	                } else {

	                  // A DDL statement
	                  nrstats++;
	                  int startLogLine = KettleLogStore.getLastBufferLineNr();
	                  try {

	                    db.execStatement( sql.getStatement() );

	                    message.append( BaseMessages.getString( SQLEditor.class, "SQLEditor.Log.SQLExecuted", sql ) );
	                    message.append( Const.CR );

	                    // Clear the database cache, in case we're using one...
	                    if ( DBCache.getInstance() != null ) {
	                    	DBCache.getInstance().clear( connection.getName() );
	                    }

	                    // mark the statement in green in the dialog...
	                    //
	                    sql.setOk( true );
	                  } catch ( Exception dbe ) {
	                    sql.setOk( false );
	                    String error = BaseMessages.getString( SQLEditor.class, "SQLEditor.Log.SQLExecError", sql, dbe.toString() );
	                    message.append( error ).append( Const.CR );
	                  } finally {
	                    int endLogLine = KettleLogStore.getLastBufferLineNr();
	                    sql.setLoggingText( KettleLogStore.getAppender().getLogBufferFromTo(
	                      db.getLogChannelId(), true, startLogLine, endLogLine ).toString() );
	                    sql.setComplete( true );
	                  }
	                }
	              }

	              message.append( BaseMessages.getString( SQLEditor.class, "SQLEditor.Log.StatsExecuted", Integer.toString( nrstats ) ) );
	  	        if ( partitionId != null ) {
	  	        	message.append( BaseMessages.getString( SQLEditor.class, "SQLEditor.Log.OnPartition", partitionId ) );
	  	        }
	  	        message.append( Const.CR );
	          } finally {
	        	  db.disconnect();
	          }
	        }

	        JsonUtils.success(StringEscapeHelper.encode(message.toString()));

		}

	}

	@ApiOperation(value = "返回指定数据库所有的存储过程名称", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库连接信息", paramType="query", dataType = "string")
	})
	@RequestMapping(method = RequestMethod.POST, value = "/procedures")
	protected @ResponseBody List procedures(@RequestParam String databaseInfo) throws IOException, KettleException, SQLException {
		JSONObject databaseInfoJson = JSONObject.fromObject(databaseInfo);
		DatabaseMeta databaseMeta = DatabaseCodec.decode(databaseInfoJson);
		Database db = new Database( loggingObject, databaseMeta );

		ArrayList list = new ArrayList();
		try {
			db.connect();
			String[] procs = db.getProcedures();
			for(String procedure : procs) {
				HashMap rec = new HashMap();
				rec.put("name", procedure);
				list.add(rec);
			}
		} finally {
			db.disconnect();
		}

		return list;
	}

	@ApiOperation(value = "返回指定数据库所有的存储过程名称2", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "database", value = "连接名称", paramType="query", dataType = "string")
	})
	@RequestMapping("/procedures2")
	protected @ResponseBody List procedures2(@RequestParam String database) throws IOException, KettleException, SQLException {
		Repository repository = App.getInstance().getRepository();
		ObjectId id_database = repository.getDatabaseID(database);

		DatabaseMeta databaseMeta = repository.loadDatabaseMeta(id_database, null);
		Database db = new Database( loggingObject, databaseMeta );

		ArrayList list = new ArrayList();
		try {
			db.connect();
			String[] procs = db.getProcedures();
			for(String procedure : procs) {
				HashMap rec = new HashMap();
				rec.put("name", procedure);
				list.add(rec);
			}
		} finally {
			db.disconnect();
		}

		return list;
	}

	@ApiOperation(value = "浏览数据库", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "databaseInfo", value = "数据库连接信息", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "nodeId", value = "root代表根节点请求,schema代表请求模式,table代表请求表", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "text", value = "节点的字面值", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "includeElement", value = "包含的节点类型", paramType="query", dataType = "string")
	})
	@RequestMapping(method = RequestMethod.POST, value = "/explorer")
	protected @ResponseBody List explorer(@RequestParam String databaseInfo, @RequestParam String nodeId, @RequestParam String text, @RequestParam int includeElement) throws IOException, KettleException, SQLException {

		JSONObject databaseInfoJson = JSONObject.fromObject(databaseInfo);
		DatabaseMeta databaseMeta = DatabaseCodec.decode(databaseInfoJson);

		ArrayList result = new ArrayList();
		if(StringUtils.hasText(nodeId)) {
			if("root".equals(nodeId)) {
				result.add(DatabaseNode.initNode("模式", "schema"));
				result.add(DatabaseNode.initNode("表", "table"));
				result.add(DatabaseNode.initNode("视图", "view"));
				result.add(DatabaseNode.initNode("同义词", "synonym"));
			} else if("schema".equals(nodeId) && databaseMeta.supportsSchemas() && DatabaseNodeType.includeSchema(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();
					DatabaseMetaData dbmd = db.getDatabaseMetaData();
					Map<String, String> connectionExtraOptions = databaseMeta.getExtraOptions();
					if (dbmd.supportsSchemasInTableDefinitions()) {
						ArrayList<String> list = new ArrayList<String>();

						String schemaFilterKey = databaseMeta.getPluginId() + "." + DatabaseMetaInformation.FILTER_SCHEMA_LIST;
						if ((connectionExtraOptions != null) && connectionExtraOptions.containsKey(schemaFilterKey)) {
							String schemasFilterCommaList = connectionExtraOptions.get(schemaFilterKey);
							String[] schemasFilterArray = schemasFilterCommaList.split(",");
							for (int i = 0; i < schemasFilterArray.length; i++) {
								list.add(schemasFilterArray[i].trim());
							}
						}
						if (list.size() == 0) {
							String sql = databaseMeta.getSQLListOfSchemas();
							if (!Const.isEmpty(sql)) {
								Statement schemaStatement = db.getConnection().createStatement();
								ResultSet schemaResultSet = schemaStatement.executeQuery(sql);
								while (schemaResultSet != null && schemaResultSet.next()) {
									String schemaName = schemaResultSet.getString("name");
									list.add(schemaName);
								}
								schemaResultSet.close();
								schemaStatement.close();
							} else {
								ResultSet schemaResultSet = dbmd.getSchemas();
								while (schemaResultSet != null && schemaResultSet.next()) {
									String schemaName = schemaResultSet.getString(1);
									list.add(schemaName);
								}
								schemaResultSet.close();
							}
						}

						for(String schema : list)
							result.add(DatabaseNode.initNode(schema, "schemaTable", "schema", !DatabaseNodeType.includeTable(includeElement)));
					}

				} finally {
					db.disconnect();
				}
			}  else if("schemaTable".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> tableMap = db.getTableMap(text);
					List<String> tableKeys = new ArrayList<String>(tableMap.keySet());
					Collections.sort(tableKeys);
					for (String schema : tableKeys) {
						List<String> tables = new ArrayList<String>(tableMap.get(schema));
						Collections.sort(tables);
						for (String tableName : tables)
							result.add(DatabaseNode.initNode(tableName, schema, "datatable", true));
					}

				} finally {
					db.disconnect();
				}
			} else if("table".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> tableMap = db.getTableMap(databaseMeta.getUsername());
					List<String> tableKeys = new ArrayList<String>(tableMap.keySet());
					Collections.sort(tableKeys);
					for (String schema : tableKeys) {
						List<String> tables = new ArrayList<String>(tableMap.get(schema));
						Collections.sort(tables);
						for (String tableName : tables)
							result.add(DatabaseNode.initNode(tableName, schema, "datatable", true));
					}
				} finally {
					db.disconnect();
				}
			} else if("view".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> viewMap = db.getViewMap();
					List<String> viewKeys = new ArrayList<String>(viewMap.keySet());
					Collections.sort(viewKeys);
					for (String schema : viewKeys) {
						List<String> views = new ArrayList<String>(viewMap.get(schema));
						Collections.sort(views);
						for (String viewName : views)
							result.add(DatabaseNode.initNode(viewName, schema, "dataview", true));
					}
				} finally {
					db.disconnect();
				}
			} else if("synonym".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> synonymMap = db.getSynonymMap();
					List<String> synonymKeys = new ArrayList<String>(synonymMap.keySet());
					Collections.sort(synonymKeys);
					for (String schema : synonymKeys) {
						List<String> synonyms = new ArrayList<String>(synonymMap.get(schema));
						Collections.sort(synonyms);
						for (String synonymName : synonyms)
							result.add(DatabaseNode.initNode(synonymName, schema, "synonym", true));
					}
				} finally {
					db.disconnect();
				}
			}
		} else {
			result.add(DatabaseNode.initNode(databaseMeta.getName(), "root", true));
		}

		return result;
	}

	@ApiOperation(value = "浏览数据库2", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "database", value = "数据库连接名称", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "nodeId", value = "root代表根节点请求,schema代表请求模式,table代表请求表", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "text", value = "节点的字面值", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "includeElement", value = "包含的节点类型", paramType="query", dataType = "string")
	})
	@RequestMapping(method = RequestMethod.POST, value = "/explorer2")
	protected @ResponseBody List explorer2(@RequestParam String database, @RequestParam(required=false) String nodeId, @RequestParam(required=false) String text, @RequestParam int includeElement) throws IOException, KettleException, SQLException {
		Repository repository = App.getInstance().getRepository();
		ObjectId id_database = repository.getDatabaseID(database);
		DatabaseMeta databaseMeta = repository.loadDatabaseMeta(id_database, null);

		ArrayList result = new ArrayList();
		if(StringUtils.hasText(nodeId)) {
			if("root".equals(nodeId)) {
				result.add(DatabaseNode.initNode("模式", "schema"));
				result.add(DatabaseNode.initNode("表", "table"));
				result.add(DatabaseNode.initNode("视图", "view"));
				result.add(DatabaseNode.initNode("同义词", "synonym"));
			} else if("schema".equals(nodeId) && databaseMeta.supportsSchemas() && DatabaseNodeType.includeSchema(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();
					DatabaseMetaData dbmd = db.getDatabaseMetaData();
					Map<String, String> connectionExtraOptions = databaseMeta.getExtraOptions();
					if (dbmd.supportsSchemasInTableDefinitions()) {
						ArrayList<String> list = new ArrayList<String>();

						String schemaFilterKey = databaseMeta.getPluginId() + "." + DatabaseMetaInformation.FILTER_SCHEMA_LIST;
						if ((connectionExtraOptions != null) && connectionExtraOptions.containsKey(schemaFilterKey)) {
							String schemasFilterCommaList = connectionExtraOptions.get(schemaFilterKey);
							String[] schemasFilterArray = schemasFilterCommaList.split(",");
							for (int i = 0; i < schemasFilterArray.length; i++) {
								list.add(schemasFilterArray[i].trim());
							}
						}
						if (list.size() == 0) {
							String sql = databaseMeta.getSQLListOfSchemas();
							if (!Const.isEmpty(sql)) {
								Statement schemaStatement = db.getConnection().createStatement();
								ResultSet schemaResultSet = schemaStatement.executeQuery(sql);
								while (schemaResultSet != null && schemaResultSet.next()) {
									String schemaName = schemaResultSet.getString("name");
									list.add(schemaName);
								}
								schemaResultSet.close();
								schemaStatement.close();
							} else {
								ResultSet schemaResultSet = dbmd.getSchemas();
								while (schemaResultSet != null && schemaResultSet.next()) {
									String schemaName = schemaResultSet.getString(1);
									list.add(schemaName);
								}
								schemaResultSet.close();
							}
						}

						for(String schema : list)
							result.add(DatabaseNode.initNode(schema, "schemaTable", "schema", !DatabaseNodeType.includeTable(includeElement)));
					}

				} finally {
					db.disconnect();
				}
			}  else if("schemaTable".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> tableMap = db.getTableMap(text);
					List<String> tableKeys = new ArrayList<String>(tableMap.keySet());
					Collections.sort(tableKeys);
					for (String schema : tableKeys) {
						List<String> tables = new ArrayList<String>(tableMap.get(schema));
						Collections.sort(tables);
						for (String tableName : tables)
							result.add(DatabaseNode.initNode(tableName, schema, "datatable", true));
					}

				} finally {
					db.disconnect();
				}
			} else if("table".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> tableMap = db.getTableMap(databaseMeta.getUsername());
					List<String> tableKeys = new ArrayList<String>(tableMap.keySet());
					Collections.sort(tableKeys);
					for (String schema : tableKeys) {
						List<String> tables = new ArrayList<String>(tableMap.get(schema));
						Collections.sort(tables);
						for (String tableName : tables)
							result.add(DatabaseNode.initNode(tableName, schema, "datatable", true));
					}
				} finally {
					db.disconnect();
				}
			} else if("view".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> viewMap = db.getViewMap();
					List<String> viewKeys = new ArrayList<String>(viewMap.keySet());
					Collections.sort(viewKeys);
					for (String schema : viewKeys) {
						List<String> views = new ArrayList<String>(viewMap.get(schema));
						Collections.sort(views);
						for (String viewName : views)
							result.add(DatabaseNode.initNode(viewName, schema, "dataview", true));
					}
				} finally {
					db.disconnect();
				}
			} else if("synonym".equals(nodeId) && DatabaseNodeType.includeTable(includeElement)) {
				Database db = new Database( loggingObject, databaseMeta );
				try {
					db.connect();

					Map<String, Collection<String>> synonymMap = db.getSynonymMap();
					List<String> synonymKeys = new ArrayList<String>(synonymMap.keySet());
					Collections.sort(synonymKeys);
					for (String schema : synonymKeys) {
						List<String> synonyms = new ArrayList<String>(synonymMap.get(schema));
						Collections.sort(synonyms);
						for (String synonymName : synonyms)
							result.add(DatabaseNode.initNode(synonymName, schema, "synonym", true));
					}
				} finally {
					db.disconnect();
				}
			}
		} else {
			result.add(DatabaseNode.initNode(databaseMeta.getName(), "root", true));
		}

		return result;
	}

	/**
	 * 删除数据库中的数据表
	 *
	 * @param reposityInfo
	 * @param password
	 * @throws IOException
	 * @throws KettleException
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/drop")
	protected void drop(@RequestParam String reposityInfo, @RequestParam String password) throws IOException, KettleException {
		JSONObject jsonObject = JSONObject.fromObject(reposityInfo);

		KettleDatabaseRepositoryMeta repositoryMeta = (KettleDatabaseRepositoryMeta) RepositoryCodec.decode(jsonObject);
		if ( repositoryMeta.getConnection() != null ) {
			KettleDatabaseRepository reposity = (KettleDatabaseRepository) PluginRegistry.getInstance().loadClass( RepositoryPluginType.class,  repositoryMeta, Repository.class );
			reposity.init( repositoryMeta );

	        try {
	        	reposity.connect( "admin", password );
	            try {
	            	reposity.dropRepositorySchema();

	            	JsonUtils.success(BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.RemovedRepositoryTables.Title" ),
	            			BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.RemovedRepositoryTables.Message" ));
	            } catch ( KettleDatabaseException dbe ) {
	            	JsonUtils.fail( BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.UnableToRemoveRepository.Title" ),
	            			BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.UnableToRemoveRepository.Message" )  + Const.CR + dbe.getMessage());
	            	return;
	            }
	          } catch ( KettleException e ) {
	        	  JsonUtils.fail( BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.UnableToVerifyAdminUser.Title" ),
	        			  BaseMessages.getString( RepositoryDialogInterface.class, "RepositoryDialog.Dialog.UnableToVerifyAdminUser.Message" )  + Const.CR + e.getMessage());
	        	  return;
	          } finally {
	        	  reposity.disconnect();
	          }
		}
	}

	public static final LoggingObjectInterface loggingObject = new SimpleLoggingObject("DatabaseController", LoggingObjectType.DATABASE, null );
}
