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

@Component("COLUMNS_EXIST")
@Scope("prototype")
public class JobEntryColumnsExist extends AbstractJobEntry {

	@Override
	public void decode(JobEntryInterface jobEntry, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		org.pentaho.di.job.entries.columnsexist.JobEntryColumnsExist jobEntryColumnsExist = (org.pentaho.di.job.entries.columnsexist.JobEntryColumnsExist) jobEntry;

		jobEntryColumnsExist.setDatabase(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		jobEntryColumnsExist.setSchemaname(cell.getAttribute("schemaname"));
		jobEntryColumnsExist.setTablename(cell.getAttribute("tablename"));


		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		String[] arguments= new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			arguments[i] = jsonObject.optString("field");
		}
		jobEntryColumnsExist.setArguments(arguments);
	}

	@Override
	public Element encode(JobEntryInterface jobEntry) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.JOB_JOBENTRY_NAME);
		org.pentaho.di.job.entries.columnsexist.JobEntryColumnsExist jobEntryColumnsExist = (org.pentaho.di.job.entries.columnsexist.JobEntryColumnsExist) jobEntry;

		DatabaseMeta databaseMeta = jobEntryColumnsExist.getDatabase();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("schemaname", jobEntryColumnsExist.getSchemaname());
		e.setAttribute("tablename", jobEntryColumnsExist.getTablename());

		JSONArray jsonArray = new JSONArray();
		String[] arguments = jobEntryColumnsExist.getArguments();
		if(arguments != null) {
			for(int j=0; j<arguments.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("field", arguments[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}


}
