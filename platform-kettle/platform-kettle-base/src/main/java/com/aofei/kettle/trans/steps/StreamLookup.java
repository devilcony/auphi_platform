package com.aofei.kettle.trans.steps;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.streamlookup.StreamLookupMeta;
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

@Component("StreamLookup")
@Scope("prototype")
public class StreamLookup extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		StreamLookupMeta streamLookupMeta = (StreamLookupMeta) stepMetaInterface;
		
		StreamInterface infoStream = streamLookupMeta.getStepIOMeta().getInfoStreams().get( 0 );
		infoStream.setSubject( cell.getAttribute("from") );
		
		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("lookup_key"));
		String[] keystream = new String[jsonArray.size()];
		String[] keylookup = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			keystream[i] = jsonObject.optString("name");
			keylookup[i] = jsonObject.optString("field");
		}
		streamLookupMeta.setKeystream(keystream);
		streamLookupMeta.setKeylookup(keylookup);
		
		jsonArray = JSONArray.fromObject(cell.getAttribute("lookup_value"));
		String[] value = new String[jsonArray.size()];
		String[] valueName = new String[jsonArray.size()];
		String[] valueDefault = new String[jsonArray.size()];
		int[] valueDefaultType = new int[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			value[i] = jsonObject.optString("name");
			valueName[i] = jsonObject.optString("rename");
			valueDefault[i] = jsonObject.optString("default");
			valueDefaultType[i] = ValueMetaFactory.getIdForValueMeta( jsonObject.optString("type") );
		}
		streamLookupMeta.setValue(value);
		streamLookupMeta.setValueDefault(valueDefault);
		streamLookupMeta.setValueName(valueName);
		streamLookupMeta.setValueDefaultType(valueDefaultType);
		
		streamLookupMeta.setMemoryPreservationActive("Y".equalsIgnoreCase(cell.getAttribute("preserve_memory")));
		streamLookupMeta.setUsingIntegerPair("Y".equalsIgnoreCase(cell.getAttribute("integer_pair")));
		streamLookupMeta.setUsingSortedList("Y".equalsIgnoreCase(cell.getAttribute("sorted_list")));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		StreamLookupMeta streamLookupMeta = (StreamLookupMeta) stepMetaInterface;
		
		StreamInterface infoStream = streamLookupMeta.getStepIOMeta().getInfoStreams().get( 0 );
		e.setAttribute("from", infoStream.getStepname());
		
		JSONArray jsonArray = new JSONArray();
		String[] keystream = streamLookupMeta.getKeystream();
		String[] keylookup = streamLookupMeta.getKeylookup();
		for (int i=0; i<keystream.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", keystream[i]);
			jsonObject.put("field", keylookup[i]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("lookup_key", jsonArray.toString());
		
		
		jsonArray = new JSONArray();
		String[] value = streamLookupMeta.getValue();
		String[] valueName = streamLookupMeta.getValueName();
		String[] valueDefault = streamLookupMeta.getValueDefault();
		int[] valueDefaultType = streamLookupMeta.getValueDefaultType();
		for (int i=0; i<value.length; i++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", value[i]);
			jsonObject.put("rename", valueName[i]);
			jsonObject.put("default", valueDefault[i]);
			jsonObject.put("type", ValueMetaFactory.getValueMetaName(valueDefaultType[i]));
			jsonArray.add(jsonObject);
		}
		e.setAttribute("lookup_value", jsonArray.toString());
		
		e.setAttribute("preserve_memory", streamLookupMeta.isMemoryPreservationActive() ? "Y" : "N");
		e.setAttribute("integer_pair", streamLookupMeta.isUsingIntegerPair() ? "Y" : "N");
		e.setAttribute("sorted_list", streamLookupMeta.isUsingSortedList() ? "Y" : "N");
		
		return e;
	}

}
