package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.ifnull.IfNullMeta;
import org.pentaho.di.trans.steps.ifnull.IfNullMeta.Fields;
import org.pentaho.di.trans.steps.ifnull.IfNullMeta.ValueTypes;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("IfNull")
@Scope("prototype")
public class IfNull extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		IfNullMeta ifNullMeta = (IfNullMeta) stepMetaInterface;

		ifNullMeta.setReplaceAllByValue(cell.getAttribute("replaceAllByValue"));
		ifNullMeta.setEmptyStringAll("Y".equalsIgnoreCase(cell.getAttribute("setEmptyStringAll")));
		ifNullMeta.setReplaceAllMask(cell.getAttribute("replaceAllMask"));
		ifNullMeta.setSelectFields("Y".equalsIgnoreCase(cell.getAttribute("selectFields")));
		ifNullMeta.setSelectValuesType("Y".equalsIgnoreCase(cell.getAttribute("selectValuesType")));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("valuetypes"));
		ValueTypes[] valueTypes = new ValueTypes[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			ValueTypes vt = new ValueTypes();
			vt.setTypeName(jsonObject.optString("name"));
			vt.setTypereplaceValue(jsonObject.optString("value"));
			vt.setTypereplaceMask(jsonObject.optString("mask"));
			vt.setTypeEmptyString("Y".equalsIgnoreCase(cell.getAttribute("set_type_empty_string")));
			valueTypes[i] = vt;
		}
		ifNullMeta.setValueTypes(valueTypes);

		jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		Fields[] fields = new Fields[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Fields field = new Fields();
			field.setFieldName(jsonObject.optString("name"));
			field.setReplaceValue(jsonObject.optString("value"));
			field.setReplaceMask(jsonObject.optString("mask"));
			field.setEmptyString("Y".equalsIgnoreCase(cell.getAttribute("set_type_empty_string")));
			fields[i] = field;
		}
		ifNullMeta.setFields(fields);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		IfNullMeta ifNullMeta = (IfNullMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("replaceAllByValue", ifNullMeta.getReplaceAllByValue());
		e.setAttribute("setEmptyStringAll", ifNullMeta.isSetEmptyStringAll() ? "Y" : "N");
		e.setAttribute("replaceAllMask", ifNullMeta.getReplaceAllMask());
		e.setAttribute("selectFields", ifNullMeta.isSelectFields() ? "Y" : "N");
		e.setAttribute("selectValuesType", ifNullMeta.isSelectValuesType() ? "Y" : "N");

		ValueTypes[] valueTypes = ifNullMeta.getValueTypes();
		JSONArray jsonArray = new JSONArray();
		if(valueTypes != null) {
			for(int j=0; j<valueTypes.length; j++) {
				ValueTypes vt = valueTypes[j];
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", vt.getTypeName());
				jsonObject.put("value", vt.getTypereplaceValue());
				jsonObject.put("mask", vt.getTypereplaceMask());
				jsonObject.put("set_type_empty_string", vt.isSetTypeEmptyString() ? "Y" : "N");
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("valuetypes", jsonArray.toString());

		Fields[] fields = ifNullMeta.getFields();
		jsonArray = new JSONArray();
		if(fields != null) {
			for(int j=0; j<fields.length; j++) {
				Fields field = fields[j];
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", field.getFieldName());
				jsonObject.put("value", field.getReplaceValue());
				jsonObject.put("mask", field.getReplaceMask());
				jsonObject.put("set_type_empty_string", field.isSetEmptyString() ? "Y" : "N");
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
