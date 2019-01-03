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
import org.pentaho.di.trans.steps.denormaliser.DenormaliserMeta;
import org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("Denormaliser")
@Scope("prototype")
public class Denormaliser extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		DenormaliserMeta denormaliserMeta = (DenormaliserMeta) stepMetaInterface;

		denormaliserMeta.setKeyField(cell.getAttribute("key_field"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("group"));
		String[] groupField = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groupField[i] = jsonObject.optString("field");
		}
		denormaliserMeta.setGroupField(groupField);

		jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		DenormaliserTargetField[] fields = new DenormaliserTargetField[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			DenormaliserTargetField field = new DenormaliserTargetField();
			field.setFieldName(jsonObject.optString("field_name"));
			field.setKeyValue(jsonObject.optString("key_value"));
			field.setTargetName(jsonObject.optString("target_name"));
			field.setTargetType(jsonObject.optString("target_type"));
			field.setTargetFormat(jsonObject.optString("target_format"));
			field.setTargetLength(jsonObject.optInt("target_length", -1));
			field.setTargetPrecision(jsonObject.optInt("target_precision", -1));

			field.setTargetDecimalSymbol(jsonObject.optString("target_decimal_symbol"));
			field.setTargetGroupingSymbol(jsonObject.optString("target_grouping_symbol"));
			field.setTargetCurrencySymbol(jsonObject.optString("target_currency_symbol"));
			field.setTargetNullString(jsonObject.optString("target_null_string"));
			field.setTargetAggregationType(jsonObject.optInt("target_aggregation_type", 0));

			fields[i] = field;
		}
		denormaliserMeta.setDenormaliserTargetField(fields);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		DenormaliserMeta denormaliserMeta = (DenormaliserMeta) stepMetaInterface;

		e.setAttribute("key_field", denormaliserMeta.getKeyField());
		String[] groupField = denormaliserMeta.getGroupField();
		JSONArray jsonArray = new JSONArray();
		if(groupField != null) {
			for(int j=0; j<groupField.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("field", groupField[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("group", jsonArray.toString());

		DenormaliserTargetField[] fields = denormaliserMeta.getDenormaliserTargetField();
		jsonArray = new JSONArray();
		if(fields != null) {
			for(int j=0; j<fields.length; j++) {
				DenormaliserTargetField denormaliserTargetField = fields[j];
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("field_name", denormaliserTargetField.getFieldName());
				jsonObject.put("key_value", denormaliserTargetField.getKeyValue());
				jsonObject.put("target_name", denormaliserTargetField.getTargetName());
				jsonObject.put("target_type", denormaliserTargetField.getTargetTypeDesc());
				jsonObject.put("target_format", denormaliserTargetField.getTargetFormat());
				jsonObject.put("target_length", denormaliserTargetField.getTargetLength());
				jsonObject.put("target_precision", denormaliserTargetField.getTargetPrecision());

				jsonObject.put("target_decimal_symbol", denormaliserTargetField.getTargetDecimalSymbol());
				jsonObject.put("target_grouping_symbol", denormaliserTargetField.getTargetGroupingSymbol());
				jsonObject.put("target_currency_symbol", denormaliserTargetField.getTargetCurrencySymbol());
				jsonObject.put("target_null_string", denormaliserTargetField.getTargetNullString());
				jsonObject.put("target_aggregation_type", denormaliserTargetField.getTargetAggregationType());

				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
