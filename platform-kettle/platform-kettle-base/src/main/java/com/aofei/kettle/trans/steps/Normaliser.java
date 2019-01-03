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
import org.pentaho.di.trans.steps.normaliser.NormaliserMeta;
import org.pentaho.di.trans.steps.normaliser.NormaliserMeta.NormaliserField;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("Normaliser")
@Scope("prototype")
public class Normaliser extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		NormaliserMeta normaliserMeta = (NormaliserMeta) stepMetaInterface;
		normaliserMeta.setTypeField(cell.getAttribute("typefield"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		NormaliserField[] normaliserFields = new NormaliserField[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			NormaliserField field = new NormaliserField();
			field.setName(jsonObject.optString("name"));
			field.setValue(jsonObject.optString("value"));
			field.setNorm(jsonObject.optString("norm"));
			normaliserFields[i] = field;
		}
		normaliserMeta.setNormaliserFields(normaliserFields);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		NormaliserMeta normaliserMeta = (NormaliserMeta) stepMetaInterface;

		e.setAttribute("typefield", normaliserMeta.getTypeField());

		NormaliserField[] normaliserFields = normaliserMeta.getNormaliserFields();
		JSONArray jsonArray = new JSONArray();
		if(normaliserFields != null) {
			for(int j=0; j<normaliserFields.length; j++) {
				NormaliserField field = normaliserFields[j];
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", field.getName());
				jsonObject.put("value", field.getValue());
				jsonObject.put("norm", field.getNorm());
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
