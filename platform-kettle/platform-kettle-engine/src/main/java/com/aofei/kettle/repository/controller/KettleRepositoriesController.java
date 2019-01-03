package com.aofei.kettle.repository.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.PluginTypeInterface;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.repository.kdr.KettleDatabaseRepositoryDialog;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aofei.kettle.App;
import com.aofei.kettle.core.database.DatabaseCodec;
import com.aofei.kettle.repository.RepositoryCodec;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.JsonUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping("/repository")
@Api(tags = "资源库维护接口api")
public class KettleRepositoriesController {

	@ApiOperation(value = "返回所有的资源库信息", httpMethod = "POST")
	@RequestMapping(method = RequestMethod.POST, value = "/list")
	protected @ResponseBody List list() throws KettleException, IOException {
		RepositoriesMeta input = new RepositoriesMeta();
		ArrayList list = new ArrayList();
		if (input.readData()) {
			for (int i = 0; i < input.nrRepositories(); i++) {
				RepositoryMeta repositoryMeta = input.getRepository(i);
				list.add(RepositoryCodec.encode(repositoryMeta));
			}
		}
		return list;
	}
	
	@ApiOperation(value = "获取一个全局数据库连接，注意该接口并非返回资源库中的数据库连接，如果该连接不存在就会创建一个默认的")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "连接名称", paramType="query", dataType = "string")
	})
	@RequestMapping(method = RequestMethod.POST, value = "/database")
	protected @ResponseBody void database(@RequestParam(required=false) String name) throws Exception {
		RepositoriesMeta input = new RepositoriesMeta();
		
		DatabaseMeta databaseMeta = null;
		if(StringUtils.hasText(name) && input.readData()) {
			for (int i = 0; i < input.nrDatabases(); i++) {
				if(input.getDatabase(i).getName().equals(name)) {
					databaseMeta = input.getDatabase(i);
					break;
				}
			}
		}
		
		if(databaseMeta == null)
			databaseMeta = new DatabaseMeta();
		
		JSONObject jsonObject = DatabaseCodec.encode(databaseMeta);
		JsonUtils.response(jsonObject);
	}
	
	@ApiOperation(value = "获取资源库类型，目前支持文件资源库和数据库资源库")
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/types")
	protected void types() throws IOException {
		JSONArray jsonArray = new JSONArray();
		
		PluginRegistry registry = PluginRegistry.getInstance();
	    Class<? extends PluginTypeInterface> pluginType = RepositoryPluginType.class;
	    List<PluginInterface> plugins = registry.getPlugins( pluginType );

	    for ( int i = 0; i < plugins.size(); i++ ) {
	      PluginInterface plugin = plugins.get( i );
	      
	      JSONObject jsonObject = new JSONObject();
	      jsonObject.put("type", plugin.getIds()[0]);
	      jsonObject.put("name", plugin.getName());
	      jsonArray.add(jsonObject);
	    }

	    JsonUtils.response(jsonArray);
	}
	
	@ApiOperation(value = "获取资源库信息")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "reposityId", value = "资源库ID", paramType="path", dataType = "string")
	})
	@RequestMapping(method = RequestMethod.GET, value = "/{reposityId}")
	protected @ResponseBody void reposity(@PathVariable String reposityId) throws KettleException, IOException {
		RepositoriesMeta input = new RepositoriesMeta();
		if (input.readData()) {
			RepositoryMeta repositoryMeta = input.searchRepository( reposityId );
			if(repositoryMeta != null) {
				JsonUtils.response(RepositoryCodec.encode(repositoryMeta));
			}
		}
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/add")
	protected void add(@RequestParam String reposityInfo, @RequestParam boolean add) throws IOException, KettleException {
		JSONObject jsonObject = JSONObject.fromObject(reposityInfo);
		
		RepositoryMeta repositoryMeta = RepositoryCodec.decode(jsonObject);
		Repository reposity = PluginRegistry.getInstance().loadClass( RepositoryPluginType.class,  repositoryMeta, Repository.class );
		reposity.init( repositoryMeta );
	        
		if ( repositoryMeta instanceof KettleDatabaseRepositoryMeta && !StringUtils.hasText(jsonObject.optJSONObject("extraOptions").optString("database")) ) {
			JsonUtils.fail(BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.Error.Title" ), 
					BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.ErrorNoConnection.Message" ));
			return;
		} else if(!StringUtils.hasText(repositoryMeta.getName())) {
			JsonUtils.fail(BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.Error.Title" ), 
					BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.ErrorNoId.Message" ));
			return;
		} else if(!StringUtils.hasText(repositoryMeta.getDescription())) {
			JsonUtils.fail(BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.Error.Title" ), 
					BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.ErrorNoName.Message" ));
			return;
		} else {
			RepositoriesMeta input = new RepositoriesMeta();
			input.readData();
			
			if(add) {
				if(input.searchRepository(repositoryMeta.getName()) != null) {
					JsonUtils.fail(BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.Error.Title" ), 
							BaseMessages.getString( KettleDatabaseRepositoryDialog.class, "RepositoryDialog.Dialog.ErrorIdExist.Message", repositoryMeta.getName()));
					return;
				} else {
					input.addRepository(repositoryMeta);
					input.writeData();
				}
			} else {
				RepositoryMeta previous = input.searchRepository(repositoryMeta.getName());
				input.removeRepository(input.indexOfRepository(previous));
				input.addRepository(repositoryMeta);
				input.writeData();
			}
		}
		
		JsonUtils.success("操作成功！");
	}
	
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/remove")
	protected void remove(@RequestParam String repositoryName) throws KettleException, IOException {
		RepositoriesMeta input = new RepositoriesMeta();
		input.readData();
		
		RepositoryMeta previous = input.searchRepository(repositoryName);
		input.removeRepository(input.indexOfRepository(previous));
		input.writeData();
		
		JsonUtils.success("操作成功！");
	}
	
	@ApiOperation(value = "系统登录", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "loginInfo", value = "登录信息", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/login")
	protected void login(@RequestParam String loginInfo) throws IOException, KettleException {
		
		
		JSONObject jsonObject = JSONObject.fromObject(loginInfo);
		
		RepositoriesMeta input = new RepositoriesMeta();
		if (input.readData()) {
			RepositoryMeta repositoryMeta = input.searchRepository( jsonObject.optString("reposityId") );
			if(repositoryMeta != null) {
				Repository repository = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta.getId(), Repository.class );
			    repository.init( repositoryMeta );
			    repository.connect( jsonObject.optString("username"), jsonObject.optString("password") );
			    
			    
			    
			    Props.getInstance().setLastRepository( repositoryMeta.getName() );
			    Props.getInstance().setLastRepositoryLogin( jsonObject.optString("username") );
			    Props.getInstance().setProperty( PropsUI.STRING_START_SHOW_REPOSITORIES, jsonObject.optBoolean("atStartupShown") ? "Y" : "N");
			    
			    Props.getInstance().saveProps();
			    
			    App.getInstance().selectRepository(repository);
			}
		}
		
		JsonUtils.success("登录成功！");
	}
	
	@ApiOperation(value = "资源库登录", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "用户名", paramType="query", dataType = "string"),
        @ApiImplicitParam(name = "password", value = "密码", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/userLogin")
	protected void userLogin(String username, String password) throws KettleException, IOException {
		try {
			Repository repository = App.getInstance().getRepository();
			repository.connect(username, password);
			
			JsonUtils.success("登录成功！");
		} catch(Exception e) {
			e.printStackTrace();
			JsonUtils.fail("登录失败");
		}
	}
}
