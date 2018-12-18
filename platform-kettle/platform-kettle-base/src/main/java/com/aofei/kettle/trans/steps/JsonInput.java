package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.ReflectUtils;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("JsonInput")
@Scope("prototype")
public class JsonInput extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
//		JsonInputMeta jsonInputMeta = (JsonInputMeta) stepMetaInterface;
//
//		jsonInputMeta.setIncludeFilename( "Y".equalsIgnoreCase( cell.getAttribute( "include" )) );
//		jsonInputMeta.setFilenameField( cell.getAttribute( "include_field" ) );
//		jsonInputMeta.setAddResultFile( "Y".equalsIgnoreCase( cell.getAttribute( "addresultfile" )) );
//		jsonInputMeta.setReadUrl( "Y".equalsIgnoreCase( cell.getAttribute( "readurl" )) );
//		jsonInputMeta.setIgnoreEmptyFile( "Y".equalsIgnoreCase( cell.getAttribute( "IsIgnoreEmptyFile" )) );
//		jsonInputMeta.setIgnoreMissingPath( "Y".equalsIgnoreCase( cell.getAttribute( "ignoreMissingPath" )) );

		ReflectUtils.set(stepMetaInterface, "includeFilename", "Y".equalsIgnoreCase(cell.getAttribute("include")));
		ReflectUtils.set(stepMetaInterface, "filenameField", cell.getAttribute("include_field"));
		ReflectUtils.set(stepMetaInterface, "addResultFile", "Y".equalsIgnoreCase(cell.getAttribute("addresultfile")));
		ReflectUtils.set(stepMetaInterface, "readUrl", "Y".equalsIgnoreCase(cell.getAttribute("readurl")));
		ReflectUtils.set(stepMetaInterface, "ignoreEmptyFile", "Y".equalsIgnoreCase(cell.getAttribute("IsIgnoreEmptyFile")));
		ReflectUtils.set(stepMetaInterface, "ignoreMissingPath", "Y".equalsIgnoreCase(cell.getAttribute("ignoreMissingPath")));


//		jsonInputMeta.setdoNotFailIfNoFile( "Y".equalsIgnoreCase( cell.getAttribute( "doNotFailIfNoFile" )) );
//		jsonInputMeta.setIncludeRowNumber( "Y".equalsIgnoreCase( cell.getAttribute( "rownum" )) );
//		jsonInputMeta.setRowNumberField( cell.getAttribute( "rownum_field" ) );

		ReflectUtils.set(stepMetaInterface, "doNotFailIfNoFile", "Y".equalsIgnoreCase(cell.getAttribute("doNotFailIfNoFile")));
		ReflectUtils.set(stepMetaInterface, "includeRowNumber", "Y".equalsIgnoreCase(cell.getAttribute("rownum")));
		ReflectUtils.set(stepMetaInterface, "rowNumberField", cell.getAttribute("rownum_field"));


		String file = cell.getAttribute("file");
		String fields = cell.getAttribute("fields");
		JSONArray fileArray = StringUtils.hasText(file) ? JSONArray.fromObject(file) : new JSONArray();
		JSONArray fieldsArray = StringUtils.hasText(fields) ? JSONArray.fromObject(fields) : new JSONArray();
		ReflectUtils.call(stepMetaInterface, "allocate", fileArray.size(), fieldsArray.size());

		String[] fileName = new String[fileArray.size()];
		String[] fileMask = new String[fileArray.size()];
		String[] excludeFileMask = new String[fileArray.size()];
		String[] fileRequired = new String[fileArray.size()];
		String[] includeSubFolders = new String[fileArray.size()];
		for(int i=0; i<fileArray.size(); i++) {
			JSONObject jsonObject = fileArray.getJSONObject(i);

			fileName[i] = jsonObject.optString( "name" );
	        fileMask[i] = jsonObject.optString( "filemask" );
	        excludeFileMask[i] = jsonObject.optString( "exclude_filemask" );
	        fileRequired[i] = jsonObject.optString( "file_required" ) ;
	        includeSubFolders[i] = jsonObject.optString( "include_subfolders" );
		}
		ReflectUtils.set(stepMetaInterface, "fileName", fileName);
		ReflectUtils.set(stepMetaInterface, "fileMask", fileMask);
		ReflectUtils.set(stepMetaInterface, "excludeFileMask", excludeFileMask);
		ReflectUtils.set(stepMetaInterface, "fileRequired", fileRequired);
		ReflectUtils.set(stepMetaInterface, "includeSubFolders", includeSubFolders);
//		jsonInputMeta.setFileName(fileName);
//		jsonInputMeta.setFileMask(fileMask);
//		jsonInputMeta.setExcludeFileMask(excludeFileMask);
//		jsonInputMeta.setFileRequired(fileRequired);
//		jsonInputMeta.setIncludeSubFolders(includeSubFolders);
//
//		Object[] inputFields = new Object[fieldsArray.size()];
		ClassLoader classLoader = stepMetaInterface.getClass().getClassLoader();
		Class<?> clazz = classLoader.loadClass("org.pentaho.di.trans.steps.jsoninput.JsonInputField");
		for(int i=0; i<fieldsArray.size(); i++) {
			JSONObject jsonObject = fieldsArray.getJSONObject(i);

			Object jsonInputField = clazz.newInstance();
			ReflectUtils.set(jsonInputField, "name", jsonObject.optString( "name" ) );
			ReflectUtils.set(jsonInputField, "path", jsonObject.optString( "path" ) );
			ReflectUtils.set(jsonInputField, "type", ValueMeta.getType(jsonObject.optString( "name" )));
			ReflectUtils.set(jsonInputField, "format", jsonObject.optString( "format" ) );

			ReflectUtils.set(jsonInputField, "currencySymbol", jsonObject.optString( "currency" ) );
			ReflectUtils.set(jsonInputField, "decimalSymbol", jsonObject.optString( "decimal" ) );
			ReflectUtils.set(jsonInputField, "groupSymbol", jsonObject.optString( "group" ) );
			ReflectUtils.set(jsonInputField, "length", jsonObject.optInt( "length", -1) );
			ReflectUtils.set(jsonInputField, "precision", jsonObject.optInt( "precision", -1) );

			ReflectUtils.set(jsonInputField, "trimType", ValueMetaBase.getTrimTypeByCode(jsonObject.optString( "trim_type" )));
			ReflectUtils.set(jsonInputField, "repeated",  !"N".equalsIgnoreCase(jsonObject.optString( "repeat" )));
//			jsonInputField.setName( jsonObject.optString( "name" ) );
//			jsonInputField.setPath( jsonObject.optString( "path" ) );
//			jsonInputField.setType( ValueMeta.getType( jsonObject.optString( "type" ) ) );
//			jsonInputField.setFormat( jsonObject.optString( "format" ) );
//			jsonInputField.setCurrencySymbol( jsonObject.optString( "currency" ) );
//			jsonInputField.setDecimalSymbol( jsonObject.optString( "decimal" ) );
//			jsonInputField.setGroupSymbol( jsonObject.optString( "group" ) );
//			jsonInputField.setLength( Const.toInt( jsonObject.optString( "length" ), -1 ) );
//			jsonInputField.setPrecision( Const.toInt( jsonObject.optString( "precision" ), -1 ) );
//			jsonInputField.setTrimType( JsonInputField.getTrimTypeByCode( jsonObject.optString( "trim_type" ) ) );
//			jsonInputField.setRepeated( !"N".equalsIgnoreCase( jsonObject.optString( "repeat" ) ) );

			ReflectUtils.getArray(stepMetaInterface, "inputFields")[i] = jsonInputField;

		}

		ReflectUtils.set(stepMetaInterface, "rowLimit", Const.toLong( cell.getAttribute( "limit" ), 0L ));
//	    jsonInputMeta.setRowLimit(Const.toLong( cell.getAttribute( "limit" ), 0L ));


		ReflectUtils.set(stepMetaInterface, "inFields", "Y".equalsIgnoreCase(cell.getAttribute("IsInFields")));
		ReflectUtils.set(stepMetaInterface, "isAFile", "Y".equalsIgnoreCase(cell.getAttribute("IsAFile")));
		ReflectUtils.set(stepMetaInterface, "fieldValue", cell.getAttribute("valueField"));
		ReflectUtils.set(stepMetaInterface, "shortFileNameField", cell.getAttribute("shortFileFieldName"));
		ReflectUtils.set(stepMetaInterface, "pathField", cell.getAttribute("pathFieldName"));
		ReflectUtils.set(stepMetaInterface, "isHiddenField", cell.getAttribute("hiddenFieldName"));
//	    jsonInputMeta.setInFields( "Y".equalsIgnoreCase(cell.getAttribute("IsInFields")) );
//	    jsonInputMeta.setIsAFile( "Y".equalsIgnoreCase(cell.getAttribute("IsAFile")) );
//	    jsonInputMeta.setFieldValue( cell.getAttribute("valueField") );
//	    jsonInputMeta.setShortFileNameField( cell.getAttribute("shortFileFieldName") );
//	    jsonInputMeta.setPathField( cell.getAttribute("pathFieldName") );
//	    jsonInputMeta.setIsHiddenField( cell.getAttribute("hiddenFieldName") );

		ReflectUtils.set(stepMetaInterface, "lastModificationDateField", cell.getAttribute("lastModificationTimeFieldName"));
		ReflectUtils.set(stepMetaInterface, "uriField", cell.getAttribute("uriNameFieldName"));
		ReflectUtils.set(stepMetaInterface, "rootUriField", cell.getAttribute("rootUriNameFieldName"));
		ReflectUtils.set(stepMetaInterface, "extensionField", cell.getAttribute("extensionFieldName"));
		ReflectUtils.set(stepMetaInterface, "sizeField", cell.getAttribute("sizeFieldName"));
//	    jsonInputMeta.setLastModificationDateField( cell.getAttribute("lastModificationTimeFieldName") );
//	    jsonInputMeta.setUriField( cell.getAttribute("uriNameFieldName") );
//	    jsonInputMeta.setRootUriField( cell.getAttribute("rootUriNameFieldName") );
//	    jsonInputMeta.setExtensionField( cell.getAttribute("extensionFieldName") );
//	    jsonInputMeta.setSizeField( cell.getAttribute("sizeFieldName") );
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("include", ReflectUtils.getFieldBoolean(stepMetaInterface, "includeFilename") ? "Y" : "N");
		e.setAttribute("include_field", ReflectUtils.getString(stepMetaInterface, "filenameField"));
		e.setAttribute("rownum", ReflectUtils.getFieldBoolean(stepMetaInterface, "includeRowNumber") ? "Y" : "N");
		e.setAttribute("addresultfile", ReflectUtils.getFieldBoolean(stepMetaInterface, "addResultFile") ? "Y" : "N");
		e.setAttribute("readurl", ReflectUtils.getFieldBoolean(stepMetaInterface, "readurl") ? "Y" : "N");

		String[] fileName = (String[]) ReflectUtils.get(stepMetaInterface, "fileName");
		String[] fileMask = (String[]) ReflectUtils.get(stepMetaInterface, "fileMask");
		String[] exludeFileMask = (String[]) ReflectUtils.get(stepMetaInterface, "exludeFileMask");
		String[] fileRequired = (String[]) ReflectUtils.get(stepMetaInterface, "fileRequired");
		String[] includeSubFolders = (String[]) ReflectUtils.get(stepMetaInterface, "includeSubFolders");

		JSONArray jsonArray = new JSONArray();
		if(fileName != null) {
			for ( int i = 0; i < fileName.length; i++ ) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", fileName[i]);
				jsonObject.put("filemask", fileMask[i]);
				jsonObject.put("exclude_filemask", exludeFileMask[i]);
				jsonObject.put("file_required", fileRequired[i]);
				jsonObject.put("include_subfolders", includeSubFolders[i]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("file", jsonArray.toString());

		e.setAttribute("IsIgnoreEmptyFile", ReflectUtils.getBoolean(stepMetaInterface, "ignoreEmptyFile") ? "Y" : "N");
		e.setAttribute("doNotFailIfNoFile", ReflectUtils.getFieldBoolean(stepMetaInterface, "doNotFailIfNoFile") ? "Y" : "N");
		e.setAttribute("ignoreMissingPath", ReflectUtils.getFieldBoolean(stepMetaInterface, "ignoreMissingPath") ? "Y" : "N");
		e.setAttribute("rownum_field", ReflectUtils.getString(stepMetaInterface, "rowNumberField"));
//
//		e.setAttribute("IsIgnoreEmptyFile", jsonInputMeta.isIgnoreEmptyFile() ? "Y" : "N");
//		e.setAttribute("doNotFailIfNoFile", jsonInputMeta.isdoNotFailIfNoFile() ? "Y" : "N");
//		e.setAttribute("ignoreMissingPath", jsonInputMeta.isIgnoreMissingPath() ? "Y" : "N");
//		e.setAttribute("rownum_field", jsonInputMeta.getRowNumberField());
//
//
		Object[] inputFields = (Object[]) ReflectUtils.get(stepMetaInterface, "inputFields");
	    jsonArray = new JSONArray();
	    if(inputFields != null) {
	    	for(Object inputField: inputFields) {
	    		JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", ReflectUtils.get(inputField, "name"));
				jsonObject.put("path", ReflectUtils.get(inputField, "path"));
				jsonObject.put("type", ReflectUtils.get(inputField, "typeDesc"));
				jsonObject.put("format", ReflectUtils.get(inputField, "format"));
				jsonObject.put("currency", ReflectUtils.get(inputField, "currencySymbol"));

				jsonObject.put("decimal", ReflectUtils.get(inputField, "decimalSymbol"));
				jsonObject.put("group", ReflectUtils.get(inputField, "groupSymbol"));
				jsonObject.put("length", ReflectUtils.get(inputField, "length"));
				jsonObject.put("precision", ReflectUtils.get(inputField, "precision"));
				jsonObject.put("trim_type", ReflectUtils.getString(inputField, "trimTypeCode"));
				jsonObject.put("repeat", ReflectUtils.getBoolean(inputField, "repeated") ? "Y" : "N");
				jsonArray.add(jsonObject);
	    	}
	    }
	    e.setAttribute("fields", jsonArray.toString());

	    e.setAttribute("limit", String.valueOf(ReflectUtils.get(stepMetaInterface, "rowLimit")));
	    e.setAttribute("IsInFields", ReflectUtils.getBoolean(stepMetaInterface, "inFields") ? "Y" : "N");
	    e.setAttribute("IsAFile", ReflectUtils.getBoolean(stepMetaInterface, "isAFile") ? "Y" : "N");
	    e.setAttribute("valueField", ReflectUtils.getString(stepMetaInterface, "fieldValue"));

//
//	    e.setAttribute("limit", String.valueOf(jsonInputMeta.getRowLimit()));
//		e.setAttribute("IsInFields", jsonInputMeta.isInFields() ? "Y" : "N");
//		e.setAttribute("IsAFile", jsonInputMeta.getIsAFile() ? "Y" : "N");
//		e.setAttribute("valueField", jsonInputMeta.getFieldValue());

	    e.setAttribute("shortFileFieldName", ReflectUtils.getString(stepMetaInterface, "shortFileNameField"));
	    e.setAttribute("pathFieldName", ReflectUtils.getString(stepMetaInterface, "pathField"));
	    e.setAttribute("hiddenFieldName", ReflectUtils.getString(stepMetaInterface, "hiddenField"));
	    e.setAttribute("lastModificationTimeFieldName", ReflectUtils.getString(stepMetaInterface, "lastModificationDateField"));

//		e.setAttribute("shortFileFieldName", jsonInputMeta.getShortFileNameField());
//		e.setAttribute("pathFieldName", jsonInputMeta.getPathField());
//		e.setAttribute("hiddenFieldName", jsonInputMeta.isHiddenField());
//		e.setAttribute("lastModificationTimeFieldName", jsonInputMeta.getLastModificationDateField());


	    e.setAttribute("uriNameFieldName", ReflectUtils.getString(stepMetaInterface, "uriField"));
	    e.setAttribute("rootUriNameFieldName", ReflectUtils.getString(stepMetaInterface, "rootUriField"));
	    e.setAttribute("extensionFieldName", ReflectUtils.getString(stepMetaInterface, "extensionField"));
	    e.setAttribute("sizeFieldName", ReflectUtils.getString(stepMetaInterface, "sizeField"));
//
//		e.setAttribute("uriNameFieldName", jsonInputMeta.getUriField());
//		e.setAttribute("rootUriNameFieldName", jsonInputMeta.getRootUriField());
//		e.setAttribute("extensionFieldName", jsonInputMeta.getExtensionField());
//		e.setAttribute("sizeFieldName", jsonInputMeta.getSizeField());

		return e;
	}

}
