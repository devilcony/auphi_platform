package com.aofei.kettle.trans.steps;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.sort.SortRowsMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("SortRows")
@Scope("prototype")
public class SortRows extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		SortRowsMeta sortRowsMeta = (SortRowsMeta) stepMetaInterface;
		
		sortRowsMeta.setDirectory(cell.getAttribute("directory"));
		sortRowsMeta.setPrefix(cell.getAttribute("prefix"));
		sortRowsMeta.setSortSize(cell.getAttribute("sort_size"));
		sortRowsMeta.setFreeMemoryLimit(cell.getAttribute("free_memory"));
		sortRowsMeta.setCompressFiles("Y".equalsIgnoreCase(cell.getAttribute("compress")));
		sortRowsMeta.setCompressFilesVariable(cell.getAttribute("compress_variable"));
		sortRowsMeta.setOnlyPassingUniqueRows("Y".equalsIgnoreCase(cell.getAttribute("unique_rows")));
		
		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("fields"));
		String[] fieldName = new String[jsonArray.size()];
		boolean[] ascending = new boolean[jsonArray.size()];
		boolean[] caseSensitive = new boolean[jsonArray.size()];
		boolean[] collatorEnabled = new boolean[jsonArray.size()];
		int[] collatorStrength = new int[jsonArray.size()];
		boolean[] preSortedField = new boolean[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			fieldName[i] = jsonObject.optString("name");
			ascending[i] = "Y".equalsIgnoreCase(jsonObject.optString("ascending"));
			caseSensitive[i] = "Y".equalsIgnoreCase(jsonObject.optString("case_sensitive"));
			collatorEnabled[i] = "Y".equalsIgnoreCase(jsonObject.optString("collator_enabled"));
			collatorStrength[i] = jsonObject.optInt("collator_strength", 0);
			preSortedField[i] = "Y".equalsIgnoreCase(jsonObject.optString("presorted"));
		}

		sortRowsMeta.setFieldName(fieldName);
		sortRowsMeta.setAscending(ascending);
		sortRowsMeta.setCaseSensitive(caseSensitive);
		sortRowsMeta.setCollatorEnabled(collatorEnabled);
		sortRowsMeta.setCollatorStrength(collatorStrength);
		sortRowsMeta.setPreSortedField(preSortedField);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		SortRowsMeta sortRowsMeta = (SortRowsMeta) stepMetaInterface;
		
		e.setAttribute("directory", sortRowsMeta.getDirectory());
		e.setAttribute("prefix", sortRowsMeta.getPrefix());
		e.setAttribute("sort_size", sortRowsMeta.getSortSize());
		e.setAttribute("free_memory", sortRowsMeta.getFreeMemoryLimit());
		e.setAttribute("compress", sortRowsMeta.getCompressFiles() ? "Y" : "N");
		e.setAttribute("compress_variable", sortRowsMeta.getCompressFilesVariable());
		e.setAttribute("unique_rows", sortRowsMeta.isOnlyPassingUniqueRows() ? "Y" : "N");
		
		JSONArray jsonArray = new JSONArray();
		String[] fieldName = sortRowsMeta.getFieldName();
		boolean[] ascending = sortRowsMeta.getAscending();
		boolean[] caseSensitive = sortRowsMeta.getCaseSensitive();
		boolean[] collatorEnabled = sortRowsMeta.getCollatorEnabled();
		int[] collatorStrength = sortRowsMeta.getCollatorStrength();
		boolean[] preSortedField = sortRowsMeta.getPreSortedField();
		
		if(fieldName != null) {
			for(int j=0; j<fieldName.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", fieldName[j]);
				jsonObject.put("ascending",ascending[j] ? "Y" : "N");
				jsonObject.put("case_sensitive",caseSensitive[j] ? "Y" : "N");
				jsonObject.put("collator_enabled",collatorEnabled[j] ? "Y" : "N");
				jsonObject.put("collator_strength", collatorStrength[j]);
				jsonObject.put("presorted",preSortedField[j] ? "Y" : "N");
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());
		
		return e;
	}

}
