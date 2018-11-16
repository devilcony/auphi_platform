package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.stepsmetrics.StepsMetricsMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("StepsMetrics")
@Scope("prototype")
public class StepsMetrics extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		StepsMetricsMeta stepsMetricsMeta = (StepsMetricsMeta) stepMetaInterface;

		String fields = cell.getAttribute("steps");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		String[] stepName = new String[jsonArray.size()];
		String[] stepCopyNr = new String[jsonArray.size()];
		String[] stepRequired = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			stepName[i] = jsonObject.optString("name");
			stepCopyNr[i] = jsonObject.optString("copyNr");
			stepRequired[i] = jsonObject.optString("stepRequired");
		}

		stepsMetricsMeta.allocate(jsonArray.size());
		stepsMetricsMeta.setStepName(stepName);
		stepsMetricsMeta.setStepCopyNr(stepCopyNr);
		stepsMetricsMeta.setStepRequired(stepRequired);

		stepsMetricsMeta.setStepNameFieldName(cell.getAttribute("stepnamefield"));
		stepsMetricsMeta.setStepIdFieldName(cell.getAttribute("stepidfield"));
		stepsMetricsMeta.setStepLinesInputFieldName(cell.getAttribute("steplinesinputfield"));
		stepsMetricsMeta.setStepLinesOutputFieldName(cell.getAttribute("steplinesoutputfield"));
		stepsMetricsMeta.setStepLinesReadFieldName(cell.getAttribute("steplinesreadfield"));
		stepsMetricsMeta.setStepLinesUpdatedFieldName(cell.getAttribute("steplinesupdatedfield"));

		stepsMetricsMeta.setStepLinesWrittenFieldName(cell.getAttribute("steplineswrittentfield"));
		stepsMetricsMeta.setStepLinesErrorsFieldName(cell.getAttribute("steplineserrorsfield"));
		stepsMetricsMeta.setStepSecondsFieldName(cell.getAttribute("stepsecondsfield"));

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		StepsMetricsMeta stepsMetricsMeta = (StepsMetricsMeta) stepMetaInterface;

		String[] stepName = stepsMetricsMeta.getStepName();
		String[] stepCopyNr = stepsMetricsMeta.getStepCopyNr();
		String[] stepRequired = stepsMetricsMeta.getStepRequired();

		JSONArray jsonArray = new JSONArray();
		if(stepName != null) {
			for(int j=0; j<stepName.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", stepName[j]);
				jsonObject.put("copyNr", stepCopyNr[j]);
				jsonObject.put("stepRequired", stepRequired[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("steps", jsonArray.toString());

		e.setAttribute("stepnamefield", stepsMetricsMeta.getStepNameFieldName());
		e.setAttribute("stepidfield", stepsMetricsMeta.getStepIdFieldName());
		e.setAttribute("steplinesinputfield", stepsMetricsMeta.getStepLinesInputFieldName());
		e.setAttribute("steplinesoutputfield", stepsMetricsMeta.getStepLinesOutputFieldName());
		e.setAttribute("steplinesreadfield", stepsMetricsMeta.getStepLinesReadFieldName());
		e.setAttribute("steplinesupdatedfield", stepsMetricsMeta.getStepLinesUpdatedFieldName());

		e.setAttribute("steplineswrittentfield", stepsMetricsMeta.getStepLinesWrittenFieldName());
		e.setAttribute("steplineserrorsfield", stepsMetricsMeta.getStepLinesErrorsFieldName());
		e.setAttribute("stepsecondsfield", stepsMetricsMeta.getStepSecondsFieldName());

		return e;
	}

}
