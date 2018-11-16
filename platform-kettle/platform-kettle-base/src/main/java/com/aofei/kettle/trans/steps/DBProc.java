package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.dbproc.DBProcMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("DBProc")
@Scope("prototype")
public class DBProc extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		DBProcMeta dbProcMeta = (DBProcMeta) stepMetaInterface;

		dbProcMeta.setDatabase(DatabaseMeta.findDatabase(databases, cell.getAttribute("connection")));
		dbProcMeta.setProcedure(cell.getAttribute("procedure"));
		dbProcMeta.setAutoCommit("Y".equalsIgnoreCase(cell.getAttribute("auto_commit")));
		dbProcMeta.setResultName(cell.getAttribute("name"));
		dbProcMeta.setResultType(ValueMeta.getType(cell.getAttribute("type")));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("arg"));
		String[] argument = new String[jsonArray.size()];
		String[] argumentDirection = new String[jsonArray.size()];
		int[] argumentType = new int[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			argument[i] = jsonObject.optString("name");
			argumentDirection[i] = jsonObject.optString("direction");
			argumentType[i] = ValueMeta.getType(jsonObject.optString("type"));
		}

		dbProcMeta.setArgument(argument);
		dbProcMeta.setArgumentDirection(argumentDirection);
		dbProcMeta.setArgumentType(argumentType);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		DBProcMeta dbProcMeta = (DBProcMeta) stepMetaInterface;

		DatabaseMeta databaseMeta = dbProcMeta.getDatabase();
		e.setAttribute("connection", databaseMeta == null ? "" : databaseMeta.getName());
		e.setAttribute("procedure", dbProcMeta.getProcedure());
		e.setAttribute("auto_commit", dbProcMeta.isAutoCommit() ? "Y" : "N");

		e.setAttribute("name", dbProcMeta.getResultName());
		e.setAttribute("type", ValueMeta.getTypeDesc(dbProcMeta.getResultType()));

		JSONArray jsonArray = new JSONArray();
		String[] argument = dbProcMeta.getArgument();
		String[] argumentDirection = dbProcMeta.getArgumentDirection();
		int[] argumentType = dbProcMeta.getArgumentType();

		if(argument != null) {
			for ( int i = 0; i < argument.length; i++ ) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", argument[i]);
				jsonObject.put("direction", argumentDirection[i]);
				jsonObject.put("type", ValueMeta.getTypeDesc(argumentType[i]));
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("arg", jsonArray.toString());


		return e;
	}

}
