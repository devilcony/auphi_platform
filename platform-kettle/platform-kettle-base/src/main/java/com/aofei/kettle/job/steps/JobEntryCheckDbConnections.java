package com.aofei.kettle.job.steps;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.job.step.AbstractJobEntry;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("CHECK_DB_CONNECTIONS")
@Scope("prototype")
public class JobEntryCheckDbConnections extends AbstractJobEntry {

	@Override
	public void decode(JobEntryInterface jobEntry, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		org.pentaho.di.job.entries.checkdbconnection.JobEntryCheckDbConnections jobEntryCheckDbConnections = (org.pentaho.di.job.entries.checkdbconnection.JobEntryCheckDbConnections) jobEntry;

		String fields = cell.getAttribute("connections");
		JSONArray jsonArray = JSONArray.fromObject(fields);

		DatabaseMeta[] connections = new DatabaseMeta[jsonArray.size()];
		int[] waittimes = new int[jsonArray.size()];
		String[] waitfors = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			connections[i] = DatabaseMeta.findDatabase(databases, jsonObject.optString("name"));
			waittimes[i] =  Const.toInt(jsonObject.optString("waittime"), 0);
			waitfors[i] = jsonObject.optString("waitfor");
		}

		jobEntryCheckDbConnections.setConnections(connections);
		jobEntryCheckDbConnections.setWaittimes(waittimes);
		jobEntryCheckDbConnections.setWaitfors(waitfors);
	}

	@Override
	public Element encode(JobEntryInterface jobEntry) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.JOB_JOBENTRY_NAME);
		org.pentaho.di.job.entries.checkdbconnection.JobEntryCheckDbConnections jobEntryCheckDbConnections = (org.pentaho.di.job.entries.checkdbconnection.JobEntryCheckDbConnections) jobEntry;

		JSONArray jsonArray = new JSONArray();
		DatabaseMeta[] connections = jobEntryCheckDbConnections.getConnections();
		int[] waittimes = jobEntryCheckDbConnections.getWaittimes();
		String[] waitfors = jobEntryCheckDbConnections.getWaitfors();
		if(connections != null) {
			for(int j=0; j<connections.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", connections[j].getName());
				jsonObject.put("waittime", waittimes[j]);
				jsonObject.put("waitfor", waitfors[j]);
				jsonArray.add(jsonObject);
			}
		}

		e.setAttribute("connections", jsonArray.toString());

		return e;
	}


}

