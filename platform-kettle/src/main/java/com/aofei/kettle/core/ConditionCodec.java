package com.aofei.kettle.core;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aofei.kettle.core.row.ValueMetaAndDataCodec;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.ValueMetaAndData;

public class ConditionCodec {

	public static JSONObject encode(Condition condition) throws KettleValueException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("negated", condition.isNegated());
		jsonObject.put("operator", condition.getOperator());

	    if ( condition.isAtomic() ) {
	    	jsonObject.put("left_valuename", condition.getLeftValuename());
	    	jsonObject.put("func", condition.getFunction());
	    	jsonObject.put("right_valuename", condition.getRightValuename());
	    	
	      if ( condition.getRightExact() != null ) {
	    	  ValueMetaAndData rightExact = condition.getRightExact();
	    	  jsonObject.put("right_exact", ValueMetaAndDataCodec.encode(rightExact));
	      }
	    } else {
	    	JSONArray conditions = new JSONArray();
			for (int i = 0; i < condition.nrConditions(); i++) {
				Condition child = condition.getCondition(i);
				conditions.add(encode(child));
			}
			
			jsonObject.put("conditions", conditions);
	    }
	    
	    return jsonObject;
	}
	
	public static Condition decode(JSONObject jsonObject) throws KettleValueException {
		Condition condition = new Condition();
		condition.setNegated(jsonObject.getBoolean("negated"));
		condition.setOperator(jsonObject.getInteger("operator"));

		JSONArray conditions = jsonObject.getJSONArray("conditions");
		if (conditions == null || conditions.size() == 0) {
			condition.setLeftValuename(jsonObject.getString("left_valuename"));
			condition.setFunction(jsonObject.getInteger("func"));
			condition.setRightValuename(jsonObject.getString("right_valuename"));
			JSONObject right_exact = jsonObject.getJSONObject("right_exact");
			if (right_exact != null) {
				ValueMetaAndData exact = ValueMetaAndDataCodec.decode(right_exact);
				condition.setRightExact(exact);
			}
		} else {
			for (int i = 0; i < conditions.size(); i++) {
				JSONObject child = conditions.getJSONObject(i);
				condition.addCondition(decode(child));
			}
		}
		
		return condition;
	}
	
}
