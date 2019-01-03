package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.pgbulkloader.PGBulkLoaderMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("PGBulkLoader")
@Scope("prototype")
public class PGBulkLoader extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases,
			IMetaStore metaStore) throws Exception {
		PGBulkLoaderMeta pgBulkLoaderMeta = (PGBulkLoaderMeta) stepMetaInterface;

		pgBulkLoaderMeta.setDatabaseMeta(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		pgBulkLoaderMeta.setSchemaName(cell.getAttribute("schema"));
		pgBulkLoaderMeta.setTableName(cell.getAttribute("table"));

		//TODO PsqlPath
		//pgBulkLoaderMeta.setPsqlPath(cell.getAttribute("PsqlPath"));
		pgBulkLoaderMeta.setLoadAction(cell.getAttribute("load_action"));
		pgBulkLoaderMeta.setDbNameOverride(cell.getAttribute("dbname_override"));
		pgBulkLoaderMeta.setEnclosure(cell.getAttribute("enclosure"));
		pgBulkLoaderMeta.setDelimiter(cell.getAttribute("delimiter"));
		pgBulkLoaderMeta.setStopOnError("Y".equalsIgnoreCase(cell.getAttribute("stop_on_error")));


		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("mapping"));
		String[] fieldTable = new String[jsonArray.size()];
		String[] fieldStream = new String[jsonArray.size()];
		String[] dateMask = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			fieldTable[i] = jsonObject.optString("stream_name");
			fieldStream[i] = jsonObject.optString("field_name");
			dateMask[i] = jsonObject.optString("date_mask");
		}

		pgBulkLoaderMeta.setFieldTable(fieldTable);
		pgBulkLoaderMeta.setFieldStream(fieldStream);
		pgBulkLoaderMeta.setDateMask(dateMask);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		PGBulkLoaderMeta pgBulkLoaderMeta = (PGBulkLoaderMeta) stepMetaInterface;

		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		DatabaseMeta databaseMeta = pgBulkLoaderMeta.getDatabaseMeta();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("schema", pgBulkLoaderMeta.getSchemaName());
		e.setAttribute("table", pgBulkLoaderMeta.getTableName());
		//TODO PsqlPath
		//e.setAttribute("PsqlPath", pgBulkLoaderMeta.getPsqlPath());
		e.setAttribute("load_action", pgBulkLoaderMeta.getLoadAction());
		e.setAttribute("dbname_override", pgBulkLoaderMeta.getDbNameOverride());
		e.setAttribute("enclosure", pgBulkLoaderMeta.getEnclosure());
		e.setAttribute("delimiter", pgBulkLoaderMeta.getDelimiter());
		e.setAttribute("stop_on_error", pgBulkLoaderMeta.isStopOnError() ? "Y" : "N");

		JSONArray jsonArray = new JSONArray();
		String[] fieldTable = pgBulkLoaderMeta.getFieldTable();
		String[] fieldStream = pgBulkLoaderMeta.getFieldStream();
		String[] dateMask = pgBulkLoaderMeta.getDateMask();

		if(fieldTable != null) {
			for ( int i = 0; i < fieldTable.length; i++ ) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("stream_name", fieldTable[i]);
				jsonObject.put("field_name", fieldStream[i]);
				jsonObject.put("date_mask", dateMask[i]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("mapping", jsonArray.toString());





		return e;
	}

}
