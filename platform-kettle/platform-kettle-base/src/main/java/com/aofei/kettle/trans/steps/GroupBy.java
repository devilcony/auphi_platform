package com.aofei.kettle.trans.steps;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.groupby.GroupByMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("GroupBy")
@Scope("prototype")
public class GroupBy extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		GroupByMeta groupByMeta = (GroupByMeta) stepMetaInterface;
		groupByMeta.setPassAllRows("Y".equalsIgnoreCase(cell.getAttribute("all_rows")));
		groupByMeta.setDirectory(cell.getAttribute("directory"));
		groupByMeta.setPrefix(cell.getAttribute("prefix"));
		groupByMeta.setAddingLineNrInGroup("Y".equalsIgnoreCase(cell.getAttribute("add_linenr")));
		groupByMeta.setLineNrInGroupField(cell.getAttribute("linenr_fieldname"));
		groupByMeta.setAlwaysGivingBackOneRow("Y".equalsIgnoreCase(cell.getAttribute("give_back_row")));
		
		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("group"));
		String[] groupField = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			groupField[i] = jsonObject.optString("field");
		}
		groupByMeta.setGroupField(groupField);
		
		
		jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		String[] aggregateField = new String[jsonArray.size()];
		String[] subjectField = new String[jsonArray.size()];
		int[] aggregateType = new int[jsonArray.size()];
		String[] valueField = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			aggregateField[i] = jsonObject.optString("aggregate");
			subjectField[i] = jsonObject.optString("subject");
			aggregateType[i] = GroupByMeta.getType(jsonObject.optString("type"));
			valueField[i] = jsonObject.optString("valuefield");
		}
		groupByMeta.setAggregateField(aggregateField);
		groupByMeta.setSubjectField(subjectField);
		groupByMeta.setAggregateType(aggregateType);
		groupByMeta.setValueField(valueField);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		GroupByMeta groupByMeta = (GroupByMeta) stepMetaInterface;
		
		e.setAttribute("all_rows", groupByMeta.passAllRows() ? "Y" : "N");
		e.setAttribute("directory", groupByMeta.getDirectory());
		e.setAttribute("prefix", groupByMeta.getPrefix());
		e.setAttribute("add_linenr", groupByMeta.isAddingLineNrInGroup() ? "Y" : "N");
		e.setAttribute("linenr_fieldname", groupByMeta.getLineNrInGroupField());
		e.setAttribute("give_back_row", groupByMeta.isAlwaysGivingBackOneRow() ? "Y" : "N");
		
		JSONArray jsonArray = new JSONArray();
		String[] groupFields = groupByMeta.getGroupField();
		for (String groupField : groupFields) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("field", groupField);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("group", jsonArray.toString());
		
		jsonArray = new JSONArray();
		String[] aggregateField = groupByMeta.getAggregateField();
		String[] subjectField = groupByMeta.getSubjectField();
		int[] aggregateType = groupByMeta.getAggregateType();
		String[] valueField = groupByMeta.getValueField();
		for (int i=0; i<aggregateField.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("aggregate", aggregateField[i]);
			jsonObject.put("subject", subjectField[i]);
			jsonObject.put("type", GroupByMeta.getTypeDesc(aggregateType[i]));
			jsonObject.put("valuefield", valueField[i]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("fields", jsonArray.toString());
		return e;
	}

}
