package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.webservices.WebServiceField;
import org.pentaho.di.trans.steps.webservices.WebServiceMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@Component("WebServiceLookup")
@Scope("prototype")
public class WebServiceLookup extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		WebServiceMeta webServiceMeta = (WebServiceMeta) stepMetaInterface;

		webServiceMeta.setUrl(cell.getAttribute("wsUrl"));
		webServiceMeta.setOperationName(cell.getAttribute("wsOperation"));
		webServiceMeta.setOperationRequestName(cell.getAttribute("wsOperationRequest"));
		webServiceMeta.setOperationNamespace(cell.getAttribute("wsOperationNamespace"));

		webServiceMeta.setCallStep(Const.toInt(cell.getAttribute("callStep"), 1000));
		webServiceMeta.setPassingInputData("Y".equalsIgnoreCase(cell.getAttribute("passingInputData")));
		webServiceMeta.setCompatible("Y".equalsIgnoreCase(cell.getAttribute("compatible")));
		webServiceMeta.setRepeatingElementName(cell.getAttribute("repeating_element"));
		webServiceMeta.setReturningReplyAsString("Y".equalsIgnoreCase(cell.getAttribute("reply_as_string")));

		webServiceMeta.setHttpLogin(cell.getAttribute("httpLogin"));
		webServiceMeta.setHttpPassword(cell.getAttribute("httpPassword"));

		webServiceMeta.setProxyHost(cell.getAttribute("proxyHost"));
		webServiceMeta.setProxyPort(cell.getAttribute("proxyPort"));

		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fieldsIn"));
		ArrayList<WebServiceField> fieldsIn = new ArrayList<WebServiceField>();
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			WebServiceField field = new WebServiceField();
			field.setName(jsonObject.optString("name"));
			field.setWsName(jsonObject.optString("wsName"));
			field.setXsdType(jsonObject.optString("xsdType"));
			fieldsIn.add(field);
		}
		webServiceMeta.setFieldsIn(fieldsIn);

		jsonArray = JSONArray.fromObject(cell.getAttribute("fieldsOut"));
		ArrayList<WebServiceField> fieldsOut = new ArrayList<WebServiceField>();
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			WebServiceField field = new WebServiceField();
			field.setName(jsonObject.optString("name"));
			field.setWsName(jsonObject.optString("wsName"));
			field.setXsdType(jsonObject.optString("xsdType"));
			fieldsOut.add(field);
		}
		webServiceMeta.setFieldsOut(fieldsOut);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		WebServiceMeta webServiceMeta = (WebServiceMeta) stepMetaInterface;

		e.setAttribute("wsUrl", webServiceMeta.getUrl());
		e.setAttribute("wsOperation", webServiceMeta.getOperationName());
		e.setAttribute("wsOperationRequest", webServiceMeta.getOperationRequestName());
		e.setAttribute("wsOperationNamespace", webServiceMeta.getOperationNamespace());

		e.setAttribute("callStep", webServiceMeta.getCallStep() + "");
		e.setAttribute("passingInputData", webServiceMeta.isPassingInputData() ? "Y" : "N");
		e.setAttribute("compatible", webServiceMeta.isCompatible() ? "Y" : "N");
		e.setAttribute("repeating_element", webServiceMeta.getRepeatingElementName());
		e.setAttribute("reply_as_string", webServiceMeta.isReturningReplyAsString() ? "Y" : "N");

		e.setAttribute("httpLogin", webServiceMeta.getHttpLogin());
		e.setAttribute("httpPassword", webServiceMeta.getHttpPassword());

		e.setAttribute("proxyHost", webServiceMeta.getProxyHost());
		e.setAttribute("proxyPort", webServiceMeta.getProxyPort());
//
//		if ( webServiceMeta.getInFieldContainerName() != null
//			      || webServiceMeta.getInFieldArgumentName() != null || !webServiceMeta.getFieldsIn().isEmpty() ) {
//
//		}

		JSONArray jsonArray = new JSONArray();
		List<WebServiceField> fieldsIn = webServiceMeta.getFieldsIn();
		if(fieldsIn != null) {
			for(WebServiceField field : fieldsIn) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", field.getName());
				jsonObject.put("wsName", field.getWsName());
				jsonObject.put("xsdType", field.getXsdType());
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fieldsIn", jsonArray.toString());

		jsonArray = new JSONArray();
		List<WebServiceField> fieldsOut = webServiceMeta.getFieldsOut();
		if(fieldsOut != null) {
			for(WebServiceField field : fieldsOut) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", field.getName());
				jsonObject.put("wsName", field.getWsName());
				jsonObject.put("xsdType", field.getXsdType());
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fieldsOut", jsonArray.toString());

		return e;
	}

}
