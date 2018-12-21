package com.aofei.kettle.trans.steps;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.calculator.CalculatorMeta;
import org.pentaho.di.trans.steps.calculator.CalculatorMetaFunction;
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

@Component("Calculator")
@Scope("prototype")
public class Calculator extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		CalculatorMeta calculatorMeta = (CalculatorMeta) stepMetaInterface;
		
		String fields = cell.getAttribute("calculation");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		
		calculatorMeta.allocate(jsonArray.size());
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			CalculatorMetaFunction calculatorMetaFunction = new CalculatorMetaFunction();
			calculatorMetaFunction.setFieldName(jsonObject.optString("field_name"));
			calculatorMetaFunction.setCalcType(CalculatorMetaFunction.getCalcFunctionType(jsonObject.optString("calc_type")));
			calculatorMetaFunction.setFieldA(jsonObject.optString("field_a"));
			calculatorMetaFunction.setFieldB(jsonObject.optString("field_b"));
			calculatorMetaFunction.setFieldC(jsonObject.optString("field_c"));
			calculatorMetaFunction.setValueType(ValueMetaFactory.getIdForValueMeta(jsonObject.optString("value_type")));
			calculatorMetaFunction.setValueLength(Const.toInt(jsonObject.optString("value_length"), -1));
			calculatorMetaFunction.setValuePrecision(Const.toInt(jsonObject.optString("value_precision"), -1));
			calculatorMetaFunction.setRemovedFromResult("Y".equalsIgnoreCase(jsonObject.optString("remove")));
			calculatorMetaFunction.setConversionMask(jsonObject.optString("conversion_mask"));
			calculatorMetaFunction.setDecimalSymbol(jsonObject.optString("decimal_symbol"));
			calculatorMetaFunction.setGroupingSymbol(jsonObject.optString("grouping_symbol"));
			calculatorMetaFunction.setCurrencySymbol(jsonObject.optString("currency_symbol"));
			
			calculatorMeta.getCalculation()[i] = calculatorMetaFunction;
		}
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		CalculatorMeta calculatorMeta = (CalculatorMeta) stepMetaInterface;
		
		JSONArray jsonArray = new JSONArray();
		CalculatorMetaFunction[] calculation = calculatorMeta.getCalculation();
		for (CalculatorMetaFunction calculatorMetaFunction : calculation) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("field_name", calculatorMetaFunction.getFieldName());
			jsonObject.put("calc_type", CalculatorMetaFunction.getCalcFunctionDesc(calculatorMetaFunction.getCalcType()));
			jsonObject.put("field_a", calculatorMetaFunction.getFieldA());
			jsonObject.put("field_b", calculatorMetaFunction.getFieldB());
			jsonObject.put("field_c", calculatorMetaFunction.getFieldC());
			jsonObject.put("value_type", ValueMetaFactory.getValueMetaName( calculatorMetaFunction.getValueType() ));
			jsonObject.put("value_length", calculatorMetaFunction.getValueLength());
			jsonObject.put("value_precision", calculatorMetaFunction.getValuePrecision());
			jsonObject.put("remove", calculatorMetaFunction.isRemovedFromResult() ? "Y" : "N");
			jsonObject.put("conversion_mask", calculatorMetaFunction.getConversionMask());
			jsonObject.put("decimal_symbol", calculatorMetaFunction.getDecimalSymbol());
			jsonObject.put("grouping_symbol", calculatorMetaFunction.getGroupingSymbol());
			jsonObject.put("currency_symbol", calculatorMetaFunction.getCurrencySymbol());
			jsonArray.add(jsonObject);
		}
		e.setAttribute("calculation", jsonArray.toString());
		
		return e;
	}

}
