package com.auphi.designer.trans.steps;

import com.auphi.designer.core.PropsUI;
import com.auphi.designer.trans.step.AbstractStep;
import com.auphi.designer.utils.JSONArray;
import com.auphi.designer.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta;
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
		GetVariableMeta.FieldDefinition[] fieldDefinitions = new GetVariableMeta.FieldDefinition[jsonArray.size()];

		for(int i=0; i<jsonArray.size(); i++) {

			JSONObject jsonObject = jsonArray.getJSONObject(i);

			fieldDefinitions[i].setFieldName(jsonObject.optString("name"));
			fieldDefinitions[i].setVariableString(jsonObject.optString("variable"));
			fieldDefinitions[i].setFieldType(jsonObject.optInt("type"));
			fieldDefinitions[i].setFieldFormat(jsonObject.optString("format"));
			fieldDefinitions[i].setCurrency(jsonObject.optString("currency"));
			fieldDefinitions[i].setDecimal(jsonObject.optString("decimal"));
			fieldDefinitions[i].setGroup(jsonObject.optString("group"));
			fieldDefinitions[i].setFieldLength(jsonObject.optInt("length", -1));
			fieldDefinitions[i].setFieldPrecision(jsonObject.optInt("precision", -1));
			fieldDefinitions[i].setTrimType(ValueMeta.getTrimTypeByCode( jsonObject.optString("trim_type") ));
		}
		getVariableMeta.setFieldDefinitions(fieldDefinitions);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		GetVariableMeta getVariableMeta = (GetVariableMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		
		JSONArray jsonArray = new JSONArray();
		GetVariableMeta.FieldDefinition[] fieldDefinitions = getVariableMeta.getFieldDefinitions();
		if(fieldDefinitions != null) {
			for(int i=0; i<fieldDefinitions.length; i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", fieldDefinitions[i].getFieldName());
				jsonObject.put("variable", fieldDefinitions[i].getVariableString());
				jsonObject.put("type", fieldDefinitions[i].getFieldType());
				jsonObject.put("format", fieldDefinitions[i].getFieldFormat());
				jsonObject.put("currency", fieldDefinitions[i].getCurrency());
				jsonObject.put("decimal", fieldDefinitions[i].getDecimal());
				jsonObject.put("group", fieldDefinitions[i].getGroup());
				jsonObject.put("length", fieldDefinitions[i].getFieldLength());
				jsonObject.put("precision", fieldDefinitions[i].getFieldPrecision());
				jsonObject.put("trim_type", ValueMeta.getTrimTypeCode(fieldDefinitions[i].getTrimType()));
				jsonArray.add(jsonObject);
			}
		}
		
		e.setAttribute("fields", jsonArray.toString());
		
		return e;
	}

}
