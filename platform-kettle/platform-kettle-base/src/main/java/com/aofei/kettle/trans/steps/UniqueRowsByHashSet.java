package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.uniquerowsbyhashset.UniqueRowsByHashSetMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("UniqueRowsByHashSet")
@Scope("prototype")
public class UniqueRowsByHashSet extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		UniqueRowsByHashSetMeta uniqueRowsByHashSetMeta = (UniqueRowsByHashSetMeta) stepMetaInterface;

		uniqueRowsByHashSetMeta.setStoreValues("Y".equalsIgnoreCase(cell.getAttribute("store_values")));
		uniqueRowsByHashSetMeta.setRejectDuplicateRow("Y".equalsIgnoreCase(cell.getAttribute("reject_duplicate_row")));
		uniqueRowsByHashSetMeta.setErrorDescription(cell.getAttribute("error_description"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		String[] compareFields = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			compareFields[i] = jsonObject.optString("name");
		}

		uniqueRowsByHashSetMeta.setCompareFields(compareFields);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		UniqueRowsByHashSetMeta uniqueRowsByHashSetMeta = (UniqueRowsByHashSetMeta) stepMetaInterface;

		e.setAttribute("store_values", uniqueRowsByHashSetMeta.getStoreValues() ? "Y" : "N");
		e.setAttribute("reject_duplicate_row", uniqueRowsByHashSetMeta.isRejectDuplicateRow() ? "Y" : "N");
		e.setAttribute("error_description", uniqueRowsByHashSetMeta.getErrorDescription());

		JSONArray jsonArray = new JSONArray();
		String[] compareFields = uniqueRowsByHashSetMeta.getCompareFields();

		if(compareFields != null) {
			for(int j=0; j<compareFields.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", compareFields[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
