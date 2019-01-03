package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta.FieldDefinition;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("GetVariable")
@Scope("prototype")
public class GetVariable extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		GetVariableMeta getVariableMeta = (GetVariableMeta) stepMetaInterface;

		String fields = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fields);

		getVariableMeta.allocate(jsonArray.size());
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			FieldDefinition field = new FieldDefinition();
			field.setFieldName(jsonObject.optString("name"));
			field.setVariableString(jsonObject.optString("variable"));
			field.setFieldType(ValueMetaFactory.getIdForValueMeta(jsonObject.optString("type")));
			field.setFieldFormat(jsonObject.optString("format"));
			field.setCurrency(jsonObject.optString("currency"));
			field.setDecimal(jsonObject.optString("decimal"));
			field.setGroup(jsonObject.optString("group"));
			field.setFieldLength(jsonObject.optInt("length", -1));
			field.setFieldPrecision(jsonObject.optInt("precision", -1));
			field.setTrimType(ValueMeta.getTrimTypeByCode(jsonObject.optString("trim_type")));

			getVariableMeta.getFieldDefinitions()[i] = field;
		}
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		GetVariableMeta getVariableMeta = (GetVariableMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);


		FieldDefinition[] fields = getVariableMeta.getFieldDefinitions();
		JSONArray jsonArray = new JSONArray();

		if(fields != null) {
			for(FieldDefinition field : fields) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", field.getFieldName());
				jsonObject.put("variable", field.getVariableString());
				jsonObject.put("type", ValueMetaFactory.getValueMetaName(field.getFieldType()));
				jsonObject.put("format", field.getFieldFormat());
				jsonObject.put("currency", field.getCurrency());
				jsonObject.put("decimal", field.getDecimal());
				jsonObject.put("group", field.getGroup());
				jsonObject.put("length", field.getFieldLength() + "");
				jsonObject.put("precision", field.getFieldPrecision() + "");
				jsonObject.put("trim_type", ValueMeta.getTrimTypeCode(field.getTrimType()));
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
