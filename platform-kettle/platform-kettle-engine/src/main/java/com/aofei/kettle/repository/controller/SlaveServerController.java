package com.aofei.kettle.repository.controller;

import com.aofei.kettle.App;
import com.aofei.kettle.cluster.SlaveServerCodec;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleDependencyException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.www.SlaveServerJobStatus;
import org.pentaho.di.www.SlaveServerStatus;
import org.pentaho.di.www.SlaveServerTransStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/slaveserver")
@Api(tags = "子服务器接口api")
public class SlaveServerController {

	@ApiOperation(value = "获取子服务器监控信息，", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "子服务器名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/status")
	protected void slaveserver(String name) throws Exception {
		Repository repository = App.getInstance().getRepository();

		ObjectId slaveId = repository.getSlaveID(name);
		SlaveServer slaveServer = repository.loadSlaveServer(slaveId, null);
		SlaveServerStatus status = slaveServer.getStatus();
		System.out.println("cpuCores: " + status.getCpuCores());
		System.out.println("cpuProcessTim: " + status.getCpuProcessTime());

		System.out.println("threadCount: " + status.getThreadCount());

		List<SlaveServerTransStatus> transStatusList = status.getTransStatusList();
		System.out.println(transStatusList);
		for(SlaveServerTransStatus transStatus : transStatusList) {
			System.out.println(transStatus.getId());
		}

		List<SlaveServerJobStatus> jobStatusList = status.getJobStatusList();
		System.out.println(jobStatusList);
		for(SlaveServerJobStatus jobStatus : jobStatusList) {
			System.out.println(jobStatus.getId());
		}

	}

	@ApiOperation(value = "获取所有子服务器名称，注意只返回名称", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/names")
	protected void slavenames() throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();

		String[] slaveNames = repository.getSlaveNames(false);
		JSONArray jsonArray = new JSONArray();
		for(String slaveName: slaveNames) {
			jsonArray.add(slaveName);
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "获取子服务器信息，", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "子服务器名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/load")
	protected void load(String name) throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();

		SlaveServer slaveServer = null;
		if(StringUtils.hasText(name)) {
			ObjectId id_slave = repository.getSlaveID(name);
			slaveServer = repository.loadSlaveServer(id_slave, null);
		} else {
			slaveServer = new SlaveServer();
		}

		JsonUtils.response(SlaveServerCodec.encode(slaveServer));
	}

	@ApiOperation(value = "持久化子服务器信息", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "slaveInfo", value = "子服务器信息，JSON串", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/persist")
	protected void persist(String slaveInfo) throws IOException, KettleException, ParserConfigurationException, SAXException {
		Repository repository = App.getInstance().getRepository();
		JSONObject jsonObject = JSONObject.fromObject(slaveInfo);
		SlaveServer slaveServer = SlaveServerCodec.decode(jsonObject);
		repository.save(slaveServer, "保存执行器：" + slaveServer.getName(), null);

		JsonUtils.success("执行器保存成功！");
	}

	@ApiOperation(value = "移除子服务器信息，", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "子服务器名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/remove")
	protected void remove(String name) throws IOException, KettleException, ParserConfigurationException, SAXException {
		Repository repository = App.getInstance().getRepository();
		ObjectId id_slave = repository.getSlaveID(name);

		if(id_slave == null) {
			JsonUtils.fail("未找到name=" + name + "的执行器");
			return;
		}

		try {
			repository.deleteSlave(id_slave);
			JsonUtils.success("执行器成功删除！");
		} catch(KettleDependencyException e) {
			JsonUtils.fail("移除失败，该执行器被其他对象占用：" + e.getMessage());
		}
	}

	@ApiOperation(value = "测试子服务器是否可用，", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "子服务器名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/test")
	protected void test(String name) throws IOException, KettleException, ParserConfigurationException, SAXException {
		Repository repository = App.getInstance().getRepository();
		ObjectId id_slave = repository.getSlaveID(name);

		if(id_slave == null) {
			System.err.println("未找到name=" + name + "的执行器");
			return;
		} else {
			System.out.println("开始测试执行器" + name + "");
		}

		SlaveServer slaveServer = repository.loadSlaveServer(id_slave, null);
		System.out.println("执行器信息：" + slaveServer);
		try {
			slaveServer.getStatus();
			JsonUtils.success("执行器连接成功！");
		} catch (Exception e) {
			e.printStackTrace();
			JsonUtils.fail("执行器连接失败！");
		}

	}

}
