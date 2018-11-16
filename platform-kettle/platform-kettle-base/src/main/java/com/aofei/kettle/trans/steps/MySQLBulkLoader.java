package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.mysqlbulkloader.MySQLBulkLoaderMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("MySQLBulkLoader")
@Scope("prototype")
public class MySQLBulkLoader extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases,
			IMetaStore metaStore) throws Exception {
		MySQLBulkLoaderMeta mySQLBulkLoaderMeta = (MySQLBulkLoaderMeta) stepMetaInterface;

		mySQLBulkLoaderMeta.setDatabaseMeta(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		mySQLBulkLoaderMeta.setSchemaName(cell.getAttribute("schema"));
		mySQLBulkLoaderMeta.setTableName(cell.getAttribute("table"));

		mySQLBulkLoaderMeta.setFifoFileName(cell.getAttribute("fifo_file_name"));
		mySQLBulkLoaderMeta.setDelimiter(cell.getAttribute("delimiter"));
		mySQLBulkLoaderMeta.setEnclosure(cell.getAttribute("enclosure"));
		mySQLBulkLoaderMeta.setEscapeChar(cell.getAttribute("escape_char"));
		mySQLBulkLoaderMeta.setEncoding(cell.getAttribute("encoding"));
		mySQLBulkLoaderMeta.setBulkSize(cell.getAttribute("bulk_size"));

		mySQLBulkLoaderMeta.setReplacingData("Y".equalsIgnoreCase(cell.getAttribute("replace")));
		mySQLBulkLoaderMeta.setIgnoringErrors("Y".equalsIgnoreCase(cell.getAttribute("ignore")));
		mySQLBulkLoaderMeta.setLocalFile("Y".equalsIgnoreCase(cell.getAttribute("local")));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("mapping"));
		String[] fieldTable = new String[jsonArray.size()];
		String[] fieldStream = new String[jsonArray.size()];
		int[] fieldFormatType = new int[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			fieldTable[i] = jsonObject.optString("stream_name");
			fieldStream[i] = jsonObject.optString("field_name");
			fieldFormatType[i] = jsonObject.optInt("field_format_ok", MySQLBulkLoaderMeta.FIELD_FORMAT_TYPE_OK);
		}

		mySQLBulkLoaderMeta.setFieldTable(fieldTable);
		mySQLBulkLoaderMeta.setFieldStream(fieldStream);
		mySQLBulkLoaderMeta.setFieldFormatType(fieldFormatType);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		MySQLBulkLoaderMeta mySQLBulkLoaderMeta = (MySQLBulkLoaderMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);


		DatabaseMeta databaseMeta = mySQLBulkLoaderMeta.getDatabaseMeta();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("schema", mySQLBulkLoaderMeta.getSchemaName());
		e.setAttribute("table", mySQLBulkLoaderMeta.getTableName());

		e.setAttribute("fifo_file_name", mySQLBulkLoaderMeta.getFifoFileName());
		e.setAttribute("delimiter", mySQLBulkLoaderMeta.getDelimiter());
		e.setAttribute("enclosure", mySQLBulkLoaderMeta.getEnclosure());
		e.setAttribute("escape_char", mySQLBulkLoaderMeta.getEscapeChar());
		e.setAttribute("encoding", mySQLBulkLoaderMeta.getEncoding());
		e.setAttribute("bulk_size", mySQLBulkLoaderMeta.getBulkSize());

		e.setAttribute("replace", mySQLBulkLoaderMeta.isReplacingData() ? "Y" : "N");
		e.setAttribute("ignore", mySQLBulkLoaderMeta.isIgnoringErrors() ? "Y" : "N");
		e.setAttribute("local", mySQLBulkLoaderMeta.isLocalFile() ? "Y" : "N");

		JSONArray jsonArray = new JSONArray();
		String[] fieldTable = mySQLBulkLoaderMeta.getFieldTable();
		String[] fieldStream = mySQLBulkLoaderMeta.getFieldStream();
		int[] fieldFormatType = mySQLBulkLoaderMeta.getFieldFormatType();

		if(fieldTable != null) {
			for ( int i = 0; i < fieldTable.length; i++ ) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("stream_name", fieldTable[i]);
				jsonObject.put("field_name", fieldStream[i]);
				jsonObject.put("field_format_ok", fieldFormatType[i]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("mapping", jsonArray.toString());

		return e;
	}

}
