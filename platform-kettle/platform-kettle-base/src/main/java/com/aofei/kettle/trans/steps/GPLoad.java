package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.ReflectUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("GPLoad")
@Scope("prototype")
public class GPLoad extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		//org.pentaho.di.trans.steps.gpload.GPLoadMeta

		String connection = cell.getAttribute("connection");
		ReflectUtils.set(stepMetaInterface, "databaseMeta", DatabaseMeta.findDatabase( databases, connection ));
		ReflectUtils.set(stepMetaInterface, "schemaName", cell.getAttribute("schema"));
		ReflectUtils.set(stepMetaInterface, "tableName", cell.getAttribute("table"));
		ReflectUtils.set(stepMetaInterface, "loadMethod", cell.getAttribute("load_method"));
		ReflectUtils.set(stepMetaInterface, "eraseFiles", "Y".equalsIgnoreCase(cell.getAttribute("erase_files")));

		// tab1
		ReflectUtils.set(stepMetaInterface, "loadAction", cell.getAttribute("load_action"));
		ReflectUtils.set(stepMetaInterface, "updateCondition", cell.getAttribute("update_condition"));

		String mapping = cell.getAttribute("mapping");
		JSONArray jsonArray = JSONArray.fromObject(mapping);
		String[] fieldTable = new String[jsonArray.size()];
		String[] fieldStream = new String[jsonArray.size()];
		String[] dateMask = new String[jsonArray.size()];
		boolean[] matchColumn = new boolean[jsonArray.size()];
		boolean[] updateColumn = new boolean[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			fieldTable[i] = jsonObject.optString("stream_name");
			fieldStream[i] = jsonObject.optString("field_name");
			dateMask[i] = jsonObject.optString("date_mask");
			matchColumn[i] = "Y".equalsIgnoreCase(jsonObject.optString("match_column"));
			updateColumn[i] = "Y".equalsIgnoreCase(jsonObject.optString("update_column"));
		}
		ReflectUtils.set(stepMetaInterface, "fieldTable", fieldTable);
		ReflectUtils.set(stepMetaInterface, "fieldStream", fieldStream);
		ReflectUtils.set(stepMetaInterface, "dateMask", dateMask);
		ReflectUtils.set(stepMetaInterface, "matchColumns", matchColumn);
		ReflectUtils.set(stepMetaInterface, "updateColumn", updateColumn);

		// tab2
		ReflectUtils.set(stepMetaInterface, "localhostPort", cell.getAttribute("localhost_port"));
		String local_hosts = cell.getAttribute("local_hosts");
		jsonArray = JSONArray.fromObject(local_hosts);
		String[] localHosts = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			localHosts[i] = jsonObject.optString("local_host");
		}
		ReflectUtils.set(stepMetaInterface, "localHosts", localHosts);

		// tab3
		ReflectUtils.set(stepMetaInterface, "gploadPath", cell.getAttribute("gpload_path"));
		ReflectUtils.set(stepMetaInterface, "controlFile", cell.getAttribute("control_file"));
		ReflectUtils.set(stepMetaInterface, "errorTableName", cell.getAttribute("error_table"));
		ReflectUtils.set(stepMetaInterface, "logFile", cell.getAttribute("log_file"));
		ReflectUtils.set(stepMetaInterface, "dataFile", cell.getAttribute("data_file"));

		ReflectUtils.set(stepMetaInterface, "nullAs", cell.getAttribute("null_as"));
		ReflectUtils.set(stepMetaInterface, "encoding", cell.getAttribute("encoding"));
		ReflectUtils.set(stepMetaInterface, "maxErrors", cell.getAttribute("errors"));
		ReflectUtils.set(stepMetaInterface, "delimiter", cell.getAttribute("delimiter"));

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		//org.pentaho.di.trans.steps.gpload.GPLoadMeta

		DatabaseMeta databaseMeta = (DatabaseMeta) ReflectUtils.get(stepMetaInterface, "databaseMeta");
		e.setAttribute("connection", databaseMeta!=null ? databaseMeta.getName() : "");
		e.setAttribute("schema", ReflectUtils.getString(stepMetaInterface, "schemaName"));
		e.setAttribute("table", ReflectUtils.getString(stepMetaInterface, "tableName"));
		e.setAttribute("load_method", ReflectUtils.getString(stepMetaInterface, "loadMethod"));
		e.setAttribute("erase_files", ReflectUtils.getBoolean(stepMetaInterface, "eraseFiles") ? "Y" : "N");

		// tab1
		e.setAttribute("load_action", ReflectUtils.getString(stepMetaInterface, "loadAction"));
		e.setAttribute("update_condition", ReflectUtils.getString(stepMetaInterface, "updateCondition"));

		String[] fieldTable = (String[]) ReflectUtils.get(stepMetaInterface, "fieldTable");
		String[] fieldStream = (String[]) ReflectUtils.get(stepMetaInterface, "fieldStream");
		String[] dateMask = (String[]) ReflectUtils.get(stepMetaInterface, "dateMask");
		boolean[] matchColumn = (boolean[]) ReflectUtils.get(stepMetaInterface, "matchColumn");
		boolean[] updateColumn = (boolean[]) ReflectUtils.get(stepMetaInterface, "updateColumn");

		JSONArray jsonArray = new JSONArray();
		if(fieldTable != null) {
			for(int j=0; j<fieldTable.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("stream_name", fieldTable[j]);
				jsonObject.put("field_name", fieldStream[j]);
				jsonObject.put("date_mask", dateMask[j]);
				jsonObject.put("match_column", matchColumn[j] ? "Y" : "N");
				jsonObject.put("update_column", updateColumn[j] ? "Y" : "N");
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("mapping", jsonArray.toString());

		// tab2
		String[] localHosts = (String[]) ReflectUtils.get(stepMetaInterface, "localHosts");
		jsonArray = new JSONArray();
		if(localHosts != null) {
			for(int j=0; j<localHosts.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("local_host", localHosts[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("local_hosts", jsonArray.toString());
		e.setAttribute("localhost_port", ReflectUtils.getString(stepMetaInterface, "localhostPort"));

		// tab3
		e.setAttribute("gpload_path", ReflectUtils.getString(stepMetaInterface, "gploadPath"));
		e.setAttribute("control_file", ReflectUtils.getString(stepMetaInterface, "controlFile"));
		e.setAttribute("error_table", ReflectUtils.getString(stepMetaInterface, "errorTableName"));
		e.setAttribute("log_file", ReflectUtils.getString(stepMetaInterface, "logFile"));
		e.setAttribute("data_file", ReflectUtils.getString(stepMetaInterface, "dataFile"));

		e.setAttribute("null_as", ReflectUtils.getString(stepMetaInterface, "nullAs"));
		e.setAttribute("encoding", ReflectUtils.getString(stepMetaInterface, "encoding"));
		e.setAttribute("errors", ReflectUtils.getString(stepMetaInterface, "maxErrors"));
		e.setAttribute("delimiter", ReflectUtils.getString(stepMetaInterface, "delimiter"));

		return e;
	}

}
