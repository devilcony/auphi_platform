package com.aofei.kettle.cluster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import com.aofei.kettle.cluster.SlaveServerCodec;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.xml.sax.SAXException;

public class ClusterSchemaCodec {

	public static JSONObject encode(ClusterSchema clusterSchema) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", clusterSchema.getName());
		jsonObject.put("base_port", clusterSchema.getBasePort());
		jsonObject.put("sockets_buffer_size", clusterSchema.getSocketsBufferSize());

		jsonObject.put("sockets_flush_interval", clusterSchema.getSocketsFlushInterval());
		jsonObject.put("sockets_compressed", clusterSchema.isSocketsCompressed() ? "Y" : "N");
		jsonObject.put("dynamic", clusterSchema.isDynamic() ? "Y" : "N");

		JSONArray slaveservers = new JSONArray();
		for (int j = 0; j < clusterSchema.getSlaveServers().size(); j++) {
			SlaveServer slaveServer = clusterSchema.getSlaveServers().get(j);
			slaveservers.add(SlaveServerCodec.encode(slaveServer));
		}
		jsonObject.put("slaveservers", slaveservers);

		return jsonObject;
	}

	public static ClusterSchema decode(JSONObject jsonObject, List<SlaveServer> referenceSlaveServers) throws ParserConfigurationException, SAXException, IOException {
		ClusterSchema clusterSchema = new ClusterSchema();
		clusterSchema.setName(jsonObject.optString( "name" ));
		clusterSchema.setBasePort(jsonObject.optString( "base_port" ));
		clusterSchema.setSocketsBufferSize(jsonObject.optString( "sockets_buffer_size" ));
		clusterSchema.setSocketsFlushInterval(jsonObject.optString( "sockets_flush_interval" ));
		clusterSchema.setSocketsCompressed("Y".equalsIgnoreCase( jsonObject.optString( "sockets_compressed" ) ));
		clusterSchema.setDynamic("Y".equalsIgnoreCase( jsonObject.optString( "dynamic" ) ));

		ArrayList<SlaveServer> slaveServers = new ArrayList<SlaveServer>();
		JSONArray slavesNode = jsonObject.optJSONArray("slaveservers");
		if(slavesNode != null) {
			for (int i = 0; i < slavesNode.size(); i++) {
				JSONObject slaveServerJson = slavesNode.getJSONObject(i);
				SlaveServer slaveServer = SlaveServer.findSlaveServer(referenceSlaveServers, slaveServerJson.optString("name"));
				if (slaveServer != null) {
					slaveServers.add(slaveServer);
				}
			}
			clusterSchema.setSlaveServers(slaveServers);
		}

		return clusterSchema;
	}

}
