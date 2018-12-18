package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.databaselookup.DatabaseLookupMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("DBLookup")
@Scope("prototype")
public class DBLookup extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		DatabaseLookupMeta databaseLookupMeta = (DatabaseLookupMeta) stepMetaInterface;

		databaseLookupMeta.setDatabaseMeta(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		databaseLookupMeta.setSchemaName(cell.getAttribute("schema"));
		databaseLookupMeta.setTablename(cell.getAttribute("table"));

		databaseLookupMeta.setCached("Y".equalsIgnoreCase(cell.getAttribute("cache")));
		databaseLookupMeta.setCacheSize(Const.toInt(cell.getAttribute("cache_size"), 0));
		databaseLookupMeta.setLoadingAllDataInCache("Y".equalsIgnoreCase(cell.getAttribute("cache_load_all")));

		databaseLookupMeta.setEatingRowOnLookupFailure("Y".equalsIgnoreCase(cell.getAttribute("eat_row_on_failure")));
		databaseLookupMeta.setFailingOnMultipleResults("Y".equalsIgnoreCase(cell.getAttribute("fail_on_multiple")));
		databaseLookupMeta.setOrderByClause(cell.getAttribute("orderby"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("key"));
		JSONArray jsonArray2 = JSONArray.fromObject(cell.getAttribute("value"));

		String[] tableKeyField = new String[jsonArray.size()];
		String[] keyCondition = new String[jsonArray.size()];
		String[] streamKeyField1 = new String[jsonArray.size()];
		String[] streamKeyField2 = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			streamKeyField1[i] = jsonObject.optString("name");
			tableKeyField[i] = jsonObject.optString("field");
			keyCondition[i] = jsonObject.optString("condition");
			streamKeyField2[i] = jsonObject.optString("name2");
		}

		String[] returnValueField = new String[jsonArray2.size()];
		String[] returnValueNewName = new String[jsonArray2.size()];
		String[] returnValueDefault = new String[jsonArray2.size()];
		int[] returnValueDefaultType = new int[jsonArray2.size()];
		for (int i = 0; i < jsonArray2.size(); i++) {
			JSONObject jsonObject = jsonArray2.getJSONObject(i);

			returnValueField[i] = jsonObject.optString("name");
			returnValueNewName[i] = jsonObject.optString("rename");
			returnValueDefault[i] = jsonObject.optString("defaultVal");
			returnValueDefaultType[i] = ValueMeta.getType(jsonObject.optString("type"));
		}

		databaseLookupMeta.allocate(jsonArray.size(), jsonArray2.size());

		databaseLookupMeta.setTableKeyField(tableKeyField);
		databaseLookupMeta.setKeyCondition(keyCondition);
		databaseLookupMeta.setStreamKeyField1(streamKeyField1);
		databaseLookupMeta.setStreamKeyField2(streamKeyField2);

		databaseLookupMeta.setReturnValueField(returnValueField);
		databaseLookupMeta.setReturnValueNewName(returnValueNewName);
		databaseLookupMeta.setReturnValueDefault(returnValueDefault);
		databaseLookupMeta.setReturnValueDefaultType(returnValueDefaultType);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		DatabaseLookupMeta databaseLookupMeta = (DatabaseLookupMeta) stepMetaInterface;

		DatabaseMeta databaseMeta = databaseLookupMeta.getDatabaseMeta();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("schema", databaseLookupMeta.getSchemaName());
		e.setAttribute("table", databaseLookupMeta.getTableName());

		e.setAttribute("cache", databaseLookupMeta.isCached() ? "Y" : "N");
		e.setAttribute("cache_size", databaseLookupMeta.getCacheSize() + "");
		e.setAttribute("cache_load_all", databaseLookupMeta.isLoadingAllDataInCache() ? "Y" : "N");

		e.setAttribute("eat_row_on_failure", databaseLookupMeta.isEatingRowOnLookupFailure() ? "Y": "N");
		e.setAttribute("fail_on_multiple", databaseLookupMeta.isFailingOnMultipleResults() ? "Y": "N");
		e.setAttribute("orderby", databaseLookupMeta.getOrderByClause());

		JSONArray jsonArray = new JSONArray();
		String[] tableKeyField = databaseLookupMeta.getTableKeyField();
		String[] keyCondition = databaseLookupMeta.getKeyCondition();
		String[] streamKeyField1 = databaseLookupMeta.getStreamKeyField1();
		String[] streamKeyField2 = databaseLookupMeta.getStreamKeyField2();

		for(int j=0; j<tableKeyField.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", streamKeyField1[j]);
			jsonObject.put("field", tableKeyField[j]);
			jsonObject.put("condition", keyCondition[j]);
			jsonObject.put("name2", streamKeyField2[j]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("key", jsonArray.toString());


		jsonArray = new JSONArray();
		String[] returnValueField = databaseLookupMeta.getReturnValueField();
		String[] returnValueNewName = databaseLookupMeta.getReturnValueNewName();
		String[] returnValueDefault = databaseLookupMeta.getReturnValueDefault();
		int[] returnValueDefaultType = databaseLookupMeta.getReturnValueDefaultType();

		for(int j=0; j<tableKeyField.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", returnValueField[j]);
			jsonObject.put("rename", returnValueNewName[j]);
			jsonObject.put("defaultVal", returnValueDefault[j]);
			jsonObject.put("type", ValueMeta.getTypeDesc(returnValueDefaultType[j]));
			jsonArray.add(jsonObject);
		}
		e.setAttribute("value", jsonArray.toString());

		return e;
	}

}
