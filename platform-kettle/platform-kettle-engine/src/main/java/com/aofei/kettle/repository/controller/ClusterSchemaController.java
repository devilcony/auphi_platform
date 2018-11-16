package com.aofei.kettle.repository.controller;

import com.aofei.kettle.App;
import com.aofei.kettle.cluster.ClusterSchemaCodec;
import com.aofei.kettle.repository.KettleDataSourceRepository;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleDependencyException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/clusterschema")
@Api(tags = "集群接口api")
public class ClusterSchemaController {

	@ApiOperation(value = "获取资源库中所有的集群信息", httpMethod = "POST")
	@ResponseBody
	@RequestMapping("/list")
	protected void clusterschemas() throws IOException, KettleException {
		Repository repository = App.getInstance().getRepository();

		ObjectId[] clusterIds = repository.getClusterIDs(false);
		List<SlaveServer> slaveServers = repository.getSlaveServers();
		JSONArray jsonArray = new JSONArray();
		for(ObjectId clusterId: clusterIds) {
			ClusterSchema clusterSchema = repository.loadClusterSchema(clusterId, slaveServers, null);
			jsonArray.add(ClusterSchemaCodec.encode(clusterSchema));
		}

		JsonUtils.response(jsonArray);
	}

	@ApiOperation(value = "移除集群信息，", httpMethod = "POST")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "集群名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping("/remove")
	protected void remove(String name) throws IOException, KettleException, ParserConfigurationException, SAXException {
		Repository repository = App.getInstance().getRepository();
		ObjectId id_cluster = repository.getClusterID(name);

		if(id_cluster == null) {
			JsonUtils.fail("未找到name=" + name + "的Kettle集群");
			return;
		}

		try {
			if(repository instanceof KettleDatabaseRepository) {
				KettleDatabaseRepository databaseRepository = (KettleDatabaseRepository) repository;
				databaseRepository.delClusterSlaves(id_cluster);
			} else if(repository instanceof KettleDataSourceRepository) {
				KettleDataSourceRepository dataSourceRepository = (KettleDataSourceRepository) repository;
				dataSourceRepository.delClusterSlaves(id_cluster);
			}

			repository.deleteClusterSchema(id_cluster);
			JsonUtils.success("Kettle集群成功删除！");
		} catch(KettleDependencyException e) {
			JsonUtils.fail("移除失败，该Kettle集群被其他对象占用：" + e.getMessage());
		}
	}

}
