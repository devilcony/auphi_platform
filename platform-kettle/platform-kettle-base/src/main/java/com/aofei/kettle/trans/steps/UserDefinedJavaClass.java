package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.StringEscapeHelper;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.userdefinedjavaclass.StepDefinition;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UsageParameter;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassDef;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassDef.ClassType;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta.FieldInfo;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

@Component("UserDefinedJavaClass")
@Scope("prototype")
public class UserDefinedJavaClass extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		UserDefinedJavaClassMeta userDefinedJavaClassMeta = (UserDefinedJavaClassMeta) stepMetaInterface;

		String str = cell.getAttribute("definitions");
		ArrayList<UserDefinedJavaClassDef> definitions = new ArrayList<UserDefinedJavaClassDef>();
		if(StringUtils.hasText(str)) {
			JSONArray jsonArray = JSONArray.fromObject(str);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String class_name = jsonObject.optString("class_name");
				String class_type = jsonObject.optString("class_type");
				String class_source = StringEscapeHelper.decode(jsonObject.optString("class_source"));

				definitions.add(new UserDefinedJavaClassDef(ClassType.valueOf(class_type), class_name, class_source));
			}
		}
		userDefinedJavaClassMeta.replaceDefinitions(definitions);

		str = cell.getAttribute("fields");
		if(StringUtils.hasText(str)) {
			JSONArray jsonArray = JSONArray.fromObject(str);
			ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String name = jsonObject.optString("field_name");
				int type = ValueMeta.getType(jsonObject.optString("field_type"));
				int length = jsonObject.optInt("field_length", -1);
				int precision = jsonObject.optInt("field_precision", -1);

				fields.add(new FieldInfo(name, type, length, precision));
			}

			userDefinedJavaClassMeta.replaceFields(fields);
		}

		str = cell.getAttribute("usage_parameters");
		if(StringUtils.hasText(str)) {
			JSONArray jsonArray = JSONArray.fromObject(str);
			ArrayList<UsageParameter> usageParameters = new ArrayList<UsageParameter>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				UsageParameter usageParameter = new UsageParameter();

				usageParameter.tag = jsonObject.optString("parameter_tag");
				usageParameter.value = jsonObject.optString("parameter_value");
				usageParameter.description = jsonObject.optString("parameter_description");

				usageParameters.add(usageParameter);
			}

			userDefinedJavaClassMeta.setUsageParameters(usageParameters);
		}

		str = cell.getAttribute("info_steps");
		if(StringUtils.hasText(str)) {
			JSONArray jsonArray = JSONArray.fromObject(str);
			ArrayList<StepDefinition> stepDefinitions = new ArrayList<StepDefinition>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				StepDefinition stepDefinition = new StepDefinition();

				stepDefinition.tag = jsonObject.optString("step_tag");
				stepDefinition.stepName = jsonObject.optString("step_name");
				stepDefinition.description = jsonObject.optString("step_description");

				stepDefinitions.add(stepDefinition);
			}

			userDefinedJavaClassMeta.setInfoStepDefinitions(stepDefinitions);
		}

		str = cell.getAttribute("target_steps");
		if(StringUtils.hasText(str)) {
			JSONArray jsonArray = JSONArray.fromObject(str);
			ArrayList<StepDefinition> stepDefinitions = new ArrayList<StepDefinition>();
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				StepDefinition stepDefinition = new StepDefinition();

				stepDefinition.tag = jsonObject.optString("step_tag");
				stepDefinition.stepName = jsonObject.optString("step_name");
				stepDefinition.description = jsonObject.optString("step_description");

				stepDefinitions.add(stepDefinition);
			}

			userDefinedJavaClassMeta.setTargetStepDefinitions(stepDefinitions);
		}

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		UserDefinedJavaClassMeta userDefinedJavaClassMeta = (UserDefinedJavaClassMeta) stepMetaInterface;

		JSONArray jsonArray = new JSONArray();
		List<UserDefinedJavaClassDef> definitions = userDefinedJavaClassMeta.getDefinitions();
		if(definitions != null) {
			for(UserDefinedJavaClassDef definition: definitions) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("class_name", definition.getClassName());
				jsonObject.put("class_type", definition.getClassType());
				jsonObject.put("class_source", StringEscapeHelper.encode( definition.getSource() ));
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("definitions", jsonArray.toString());

		jsonArray = new JSONArray();
		List<FieldInfo> fieldInfo = userDefinedJavaClassMeta.getFieldInfo();
		if(fieldInfo != null) {
			for(FieldInfo field: fieldInfo) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("field_name", field.name);
				jsonObject.put("field_type", ValueMeta.getTypeDesc(field.type));
				jsonObject.put("field_length", field.length);
				jsonObject.put("field_precision", field.precision);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("fields", jsonArray.toString());

		jsonArray = new JSONArray();
		List<UsageParameter> usageParameters = userDefinedJavaClassMeta.getUsageParameters();
		if(usageParameters != null) {
			for(UsageParameter usageParameter: usageParameters) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("parameter_tag", usageParameter.tag);
				jsonObject.put("parameter_value", usageParameter.value);
				jsonObject.put("parameter_description", usageParameter.description);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("usage_parameters", jsonArray.toString());

		jsonArray = new JSONArray();
		List<StepDefinition> stepDefinitions = userDefinedJavaClassMeta.getInfoStepDefinitions();
		if(stepDefinitions != null) {
			for(StepDefinition stepDefinition: stepDefinitions) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("step_tag", stepDefinition.tag);
				jsonObject.put("step_name", stepDefinition.stepName);
				jsonObject.put("step_description", stepDefinition.description);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("info_steps", jsonArray.toString());


		jsonArray = new JSONArray();
		List<StepDefinition> targetStepDefinitions = userDefinedJavaClassMeta.getTargetStepDefinitions();
		if(stepDefinitions != null) {
			for(StepDefinition stepDefinition: targetStepDefinitions) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("step_tag", stepDefinition.tag);
				jsonObject.put("step_name", stepDefinition.stepName);
				jsonObject.put("step_description", stepDefinition.description);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("target_steps", jsonArray.toString());

		return e;
	}

}

