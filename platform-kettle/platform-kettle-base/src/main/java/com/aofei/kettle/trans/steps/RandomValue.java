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
import org.pentaho.di.trans.steps.randomvalue.RandomValueMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("RandomValue")
@Scope("prototype")
public class RandomValue extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		RandomValueMeta randomValueMeta = (RandomValueMeta) stepMetaInterface;

		String fields = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		String[] fieldName = new String[jsonArray.size()];
		int[] fieldType = new int[jsonArray.size()];

		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			fieldName[i] = jsonObject.optString("name");
			fieldType[i] = jsonObject.optInt("type");
		}
		randomValueMeta.setFieldName(fieldName);
		randomValueMeta.setFieldType(fieldType);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		RandomValueMeta randomValueMeta = (RandomValueMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		JSONArray jsonArray = new JSONArray();
		String[] fieldName = randomValueMeta.getFieldName();
		if(fieldName != null) {
			for(int i=0; i<fieldName.length; i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", fieldName[i]);
				jsonObject.put("type", randomValueMeta.getFieldType()[i]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
