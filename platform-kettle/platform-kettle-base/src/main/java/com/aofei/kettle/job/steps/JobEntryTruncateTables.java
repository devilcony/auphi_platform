package com.aofei.kettle.job.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.job.step.AbstractJobEntry;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("TRUNCATE_TABLES")
@Scope("prototype")
public class JobEntryTruncateTables extends AbstractJobEntry {

	@Override
	public void decode(JobEntryInterface jobEntry, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore)
			throws Exception {
		org.pentaho.di.job.entries.truncatetables.JobEntryTruncateTables jobEntryTruncateTables = (org.pentaho.di.job.entries.truncatetables.JobEntryTruncateTables) jobEntry;
		jobEntryTruncateTables.setDatabase(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		jobEntryTruncateTables.argFromPrevious = "Y".equalsIgnoreCase(cell.getAttribute("arg_from_previous"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		String[] arguments= new String[jsonArray.size()];
		String[] schemaname= new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			arguments[i] = jsonObject.optString("name");
			schemaname[i] = jsonObject.optString("schemaname");
		}

		jobEntryTruncateTables.arguments = arguments;
		jobEntryTruncateTables.schemaname = schemaname;
	}

	@Override
	public Element encode(JobEntryInterface jobEntry) throws Exception {
		org.pentaho.di.job.entries.truncatetables.JobEntryTruncateTables jobEntryTruncateTables = (org.pentaho.di.job.entries.truncatetables.JobEntryTruncateTables) jobEntry;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.JOB_JOBENTRY_NAME);

		DatabaseMeta database = jobEntryTruncateTables.getDatabase();
		e.setAttribute("connection", database != null ? database.getName() : "");
		e.setAttribute("arg_from_previous", jobEntryTruncateTables.argFromPrevious ? "Y" : "N");

		String[] arguments = jobEntryTruncateTables.arguments;
		String[] schemaname = jobEntryTruncateTables.schemaname;

		JSONArray jsonArray = new JSONArray();
		if(arguments != null) {
			for(int j=0; j<arguments.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", arguments[j]);
				jsonObject.put("schemaname", schemaname[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
