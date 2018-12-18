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
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.validator.Validation;
import org.pentaho.di.trans.steps.validator.ValidatorMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@Component("Validator")
@Scope("prototype")
public class Validator extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		ValidatorMeta validatorMeta = (ValidatorMeta) stepMetaInterface;

		validatorMeta.setValidatingAll("Y".equalsIgnoreCase(cell.getAttribute("validate_all")));
		validatorMeta.setConcatenatingErrors("Y".equalsIgnoreCase(cell.getAttribute("concat_errors")));
		validatorMeta.setConcatenationSeparator(cell.getAttribute("concat_separator"));

		String validationsString = cell.getAttribute("validations");
		JSONArray jsonArray = JSONArray.fromObject(validationsString);
		validatorMeta.allocate(jsonArray.size());

		ArrayList<Validation> validations = new ArrayList<Validation>();
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			Validation validation = new Validation();
			validation.setFieldName(jsonObject.optString("name"));
			validation.setName(jsonObject.optString("validation_name"));
			validation.setMaximumLength(jsonObject.optString("max_length"));
			validation.setMinimumLength(jsonObject.optString("min_length"));

			validation.setNullAllowed("Y".equalsIgnoreCase(jsonObject.optString("null_allowed")));
			validation.setOnlyNullAllowed("Y".equalsIgnoreCase(jsonObject.optString("only_null_allowed")));
			validation.setOnlyNumericAllowed("Y".equalsIgnoreCase(jsonObject.optString("only_numeric_allowed")));

			validation.setDataType(ValueMeta.getType(jsonObject.optString("data_type")));
			validation.setDataTypeVerified("Y".equalsIgnoreCase(jsonObject.optString("data_type_verified")));

			validation.setConversionMask(jsonObject.optString("conversion_mask"));
			validation.setDecimalSymbol(jsonObject.optString("decimal_symbol"));
			validation.setGroupingSymbol(jsonObject.optString("grouping_symbol"));

			validation.setMaximumValue(jsonObject.optString("max_value"));
			validation.setMinimumValue(jsonObject.optString("min_value"));

			validation.setStartString(jsonObject.optString("start_string"));
			validation.setEndString(jsonObject.optString("end_string"));
			validation.setStartStringNotAllowed(jsonObject.optString("start_string_not_allowed"));
			validation.setEndStringNotAllowed(jsonObject.optString("end_string_not_allowed"));

			validation.setRegularExpression(jsonObject.optString("regular_expression"));
			validation.setRegularExpressionNotAllowed(jsonObject.optString("regular_expression_not_allowed"));

			validation.setErrorCode(jsonObject.optString("error_code"));
			validation.setErrorDescription(jsonObject.optString("error_description"));

			validation.setSourcingValues("Y".equalsIgnoreCase(jsonObject.optString("is_sourcing_values")));
			validation.setSourcingStepName(jsonObject.optString("sourcing_step"));

			validation.setSourcingField(jsonObject.optString("sourcing_field"));

			JSONArray allowed_value = jsonObject.optJSONArray("allowed_value");
			if(allowed_value != null && allowed_value.size() > 0) {
				String[] allowedValues = new String[allowed_value.size()];
				for (int j = 0; j < allowed_value.size(); j++) {
					JSONObject jsonObject2 = allowed_value.getJSONObject(j);
					allowedValues[j] = jsonObject2.optString("value");
				}

				validation.setAllowedValues(allowedValues);
			}

			validations.add(validation);
		}

		validatorMeta.setValidations(validations);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		ValidatorMeta validatorMeta = (ValidatorMeta) stepMetaInterface;

		e.setAttribute("validate_all", validatorMeta.isValidatingAll() ? "Y" : "N");
		e.setAttribute("concat_errors", validatorMeta.isConcatenatingErrors() ? "Y" : "N");
		e.setAttribute("concat_separator", validatorMeta.getConcatenationSeparator());

		JSONArray jsonArray = new JSONArray();
		List<Validation> validations = validatorMeta.getValidations();
		if(validatorMeta != null) {
			for (Validation validation : validations) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", validation.getFieldName());
				jsonObject.put("validation_name", validation.getName());
				jsonObject.put("max_length", validation.getMaximumLength());
				jsonObject.put("min_length", validation.getMinimumLength());

				jsonObject.put("null_allowed", validation.isNullAllowed() ? "Y" : "N");
				jsonObject.put("only_null_allowed", validation.isOnlyNullAllowed() ? "Y" : "N");
				jsonObject.put("only_numeric_allowed", validation.isOnlyNumericAllowed() ? "Y" : "N");

				jsonObject.put("data_type", ValueMeta.getTypeDesc(validation.getDataType()) );
				jsonObject.put("data_type_verified", validation.isDataTypeVerified() ? "Y" : "N");
				jsonObject.put("conversion_mask", validation.getConversionMask());
				jsonObject.put("decimal_symbol", validation.getDecimalSymbol());
				jsonObject.put("grouping_symbol", validation.getGroupingSymbol());

				jsonObject.put("max_value", validation.getMaximumValue());
				jsonObject.put("min_value", validation.getMinimumValue());

				jsonObject.put("start_string", validation.getStartString());
				jsonObject.put("end_string", validation.getEndString());
				jsonObject.put("start_string_not_allowed", validation.getStartStringNotAllowed());
				jsonObject.put("end_string_not_allowed", validation.getEndStringNotAllowed());

				jsonObject.put("regular_expression", validation.getRegularExpression());
				jsonObject.put("regular_expression_not_allowed", validation.getRegularExpressionNotAllowed());

				jsonObject.put("error_code", validation.getErrorCode());
				jsonObject.put("error_description", validation.getErrorDescription());

				jsonObject.put("is_sourcing_values", validation.isSourcingValues() ? "Y" : "N");
				StepMeta sourcingStep = validation.getSourcingStep();
				jsonObject.put("sourcing_step", sourcingStep == null ? validation.getSourcingStep() : sourcingStep.getName());

				jsonObject.put("sourcing_field", validation.getSourcingField());

				JSONArray jsonArray2 = new JSONArray();
				String[] allowedValues = validation.getAllowedValues();
				if (allowedValues != null) {
					for (String allowedValue : allowedValues) {
						JSONObject jsonObject2 = new JSONObject();
						jsonObject2.put("value", allowedValue);

						jsonArray2.add(jsonObject2);
					}
				}
				jsonObject.put("allowed_value", jsonArray2);

				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("validations", jsonArray.toString());

		return e;
	}

}
