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
import org.pentaho.di.trans.steps.sortedmerge.SortedMergeMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("SortedMerge")
@Scope("prototype")
public class SortedMerge extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		SortedMergeMeta sortedMergeMeta = (SortedMergeMeta) stepMetaInterface;

		String fields = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		String[] fieldName = new String[jsonArray.size()];
		boolean[] ascending = new boolean[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			fieldName[i] = jsonObject.optString("name");
			ascending[i] = "Y".equalsIgnoreCase("ascending");
		}

		sortedMergeMeta.setFieldName(fieldName);
		sortedMergeMeta.setAscending(ascending);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		SortedMergeMeta sortedMergeMeta = (SortedMergeMeta) stepMetaInterface;

		JSONArray jsonArray = new JSONArray();
		String[] fieldName = sortedMergeMeta.getFieldName();
		boolean[] ascending = sortedMergeMeta.getAscending();
		for(int j=0; j<fieldName.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", fieldName[j]);
			jsonObject.put("ascending", ascending[j] ? "Y" : "N");
			jsonArray.add(jsonObject);
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}

