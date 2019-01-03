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
import org.pentaho.di.trans.steps.checksum.CheckSumMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("CheckSum")
@Scope("prototype")
public class CheckSum extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;

		String checkSumType = cell.getAttribute("checksumtype");
		int type=0;
		for(String str : CheckSumMeta.checksumtypeCodes){
			if(str.equalsIgnoreCase(checkSumType))
				break;
			type++;
		}
		checkSumMeta.setCheckSumType(type);
		checkSumMeta.setResultType(checkSumMeta.getResultTypeByDesc( cell.getAttribute( "resultType" ) ));
		checkSumMeta.setResultFieldName(cell.getAttribute("resultfieldName"));
		checkSumMeta.setCompatibilityMode("Y".equalsIgnoreCase(cell.getAttribute("compatibilityMode")));

		String fields = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		String[] fieldName = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			fieldName[i] = jsonObject.optString("name");
		}
		checkSumMeta.setFieldName(fieldName);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		CheckSumMeta checkSumMeta = (CheckSumMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("checksumtype", checkSumMeta.getCheckSumType());
		e.setAttribute("resultType", CheckSumMeta.resultTypeCode[checkSumMeta.getResultType()]);
		e.setAttribute("resultfieldName", checkSumMeta.getResultFieldName());
		e.setAttribute("compatibilityMode", checkSumMeta.isCompatibilityMode() ? "Y" : "N");

		JSONArray jsonArray = new JSONArray();
		String[] fieldName = checkSumMeta.getFieldName();
		for(int j=0; j<fieldName.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", fieldName[j]);
			jsonArray.add(jsonObject);
		}

		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
