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
import org.pentaho.di.trans.steps.excelinput.ExcelInputField;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("ExcelInput")
@Scope("prototype")
public class ExcelInput extends AbstractStep {
	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases,
			IMetaStore metaStore) throws Exception {
		ExcelInputMeta excel = (ExcelInputMeta) stepMetaInterface;

		// file tab
		excel.setSpreadSheetType(SpreadSheetType.valueOf(cell.getAttribute("spreadsheet_type")));

		String file = cell.getAttribute("file");
		JSONArray fileArray = JSONArray.fromObject(file);
		String[] fileName = new String[fileArray.size()];
		String[] fileMask = new String[fileArray.size()];
		String[] excludeFileMask = new String[fileArray.size()];
		String[] fileRequired = new String[fileArray.size()];
		String[] includeSubFolders = new String[fileArray.size()];
		for (int i = 0; i < fileArray.size(); i++) {
			JSONObject jsonObject = fileArray.getJSONObject(i);
			fileName[i] = jsonObject.optString("name");
			fileMask[i] = jsonObject.optString("filemask");
			excludeFileMask[i] = jsonObject.optString("exclude_filemask");
			fileRequired[i] = jsonObject.optString("file_required");
			includeSubFolders[i] = jsonObject.optString("include_subfolders");

		}
		excel.setFileName(fileName);
		excel.setFileMask(fileMask);
		excel.setExcludeFileMask(excludeFileMask);
		excel.setFileRequired(fileRequired);
		excel.setIncludeSubFolders(includeSubFolders);

		excel.setAcceptingFilenames("Y".equalsIgnoreCase(cell.getAttribute("accept_filenames")));
		excel.setAcceptingField(cell.getAttribute("accept_field"));
		excel.setAcceptingStepName(cell.getAttribute("accept_stepname"));

		// sheet tab
		String sheets = cell.getAttribute("sheets");
		JSONArray sheetsArray = JSONArray.fromObject(sheets);
		String[] sheetName = new String[sheetsArray.size()];
		int[] startRow = new int[sheetsArray.size()];
		int[] startColumn = new int[sheetsArray.size()];
		for (int i = 0; i < sheetsArray.size(); i++) {
			JSONObject jsonObject = sheetsArray.getJSONObject(i);

			sheetName[i] = jsonObject.optString("name");
	        startRow[i] = Const.toInt( jsonObject.optString("startrow"), 0 );
	        startColumn[i] = Const.toInt( jsonObject.optString("startcol"), 0 );
		}
		excel.setSheetName(sheetName);
		excel.setStartRow(startRow);
		excel.setStartColumn(startColumn);

		// content tab
		excel.setStartsWithHeader("Y".equalsIgnoreCase(cell.getAttribute("header")));
		excel.setIgnoreEmptyRows("Y".equalsIgnoreCase(cell.getAttribute("noempty")));
		excel.setStopOnEmpty("Y".equalsIgnoreCase(cell.getAttribute("stoponempty")));
		excel.setRowLimit(Const.toLong(cell.getAttribute("limit"), 0L));
		excel.setEncoding(cell.getAttribute("encoding"));
		excel.setAddResultFile("Y".equalsIgnoreCase(cell.getAttribute("add_to_result_filenames")));

		// error tab
		excel.setStrictTypes("Y".equalsIgnoreCase(cell.getAttribute("strict_types")));
		excel.setErrorIgnored("Y".equalsIgnoreCase(cell.getAttribute("error_ignored")));
		excel.setErrorLineSkipped("Y".equalsIgnoreCase(cell.getAttribute("error_line_skipped")));
		excel.setWarningFilesDestinationDirectory(cell.getAttribute("bad_line_files_destination_directory"));
		excel.setBadLineFilesExtension(cell.getAttribute("bad_line_files_extension"));
		excel.setErrorFilesDestinationDirectory(cell.getAttribute("error_line_files_destination_directory"));
		excel.setErrorFilesExtension(cell.getAttribute("error_line_files_extension"));
		excel.setLineNumberFilesDestinationDirectory(cell.getAttribute("line_number_files_destination_directory"));
		excel.setLineNumberFilesExtension(cell.getAttribute("line_number_files_extension"));

		// fields tab
		String fields = cell.getAttribute("fields");
		JSONArray fieldsArray = JSONArray.fromObject(fields);
		ExcelInputField[] inputFields = new ExcelInputField[fieldsArray.size()];
		for (int i = 0; i < fieldsArray.size(); i++) {
			ExcelInputField field = new ExcelInputField();
			JSONObject jsonObject = fieldsArray.getJSONObject(i);
			field.setName(jsonObject.optString("name"));
			field.setType(jsonObject.optString("type"));
			field.setLength(Const.toInt(jsonObject.optString("length"), -1));
			field.setPrecision(Const.toInt(jsonObject.optString("precision"), -1));
			String repeat = jsonObject.optString("repeat");
			field.setTrimType(ExcelInputMeta.getTrimTypeByCode(jsonObject.optString("trim_type")));
			field.setRepeated(null == repeat ? false : repeat.equalsIgnoreCase("Y"));
			field.setFormat(jsonObject.optString("format"));
			field.setCurrencySymbol(jsonObject.optString("currency"));
			field.setDecimalSymbol(jsonObject.optString("decimal"));
			field.setGroupSymbol(jsonObject.optString("group"));

			inputFields[i] = field;
		}
		excel.setField(inputFields);

		// other tab
		excel.setFileField(cell.getAttribute("filefield"));
		excel.setSheetField(cell.getAttribute("sheetfield"));
		excel.setSheetRowNumberField(cell.getAttribute("sheetrownumfield"));
		excel.setRowNumberField(cell.getAttribute("rownumfield"));
		excel.setShortFileNameField(cell.getAttribute("shortFileFieldName"));
		excel.setExtensionField(cell.getAttribute("extensionFieldName"));
		excel.setPathField(cell.getAttribute("pathFieldName"));
		excel.setSizeField(cell.getAttribute("sizeFieldName"));
		excel.setIsHiddenField(cell.getAttribute("hiddenFieldName"));
		excel.setLastModificationDateField(cell.getAttribute("lastModificationTimeFieldName"));
		excel.setUriField(cell.getAttribute("uriNameFieldName"));
		excel.setRootUriField(cell.getAttribute("rootUriNameFieldName"));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		ExcelInputMeta excel = (ExcelInputMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		// file tab
		SpreadSheetType spreadSheetType = excel.getSpreadSheetType();
		if(spreadSheetType == null) spreadSheetType = SpreadSheetType.JXL;
		e.setAttribute("spreadsheet_type",  spreadSheetType.toString());

		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < excel.getFileName().length; i++) {
			JSONObject json1 = new JSONObject();
			json1.put("name", excel.getFileName()[i]);
			json1.put("filemask", excel.getFileMask()[i]);
			json1.put("exclude_filemask", excel.getExludeFileMask()[i]);
			json1.put("file_required", excel.getFileRequired()[i]);
			json1.put("include_subfolders", excel.getIncludeSubFolders()[i]);
			jsonArray.add(json1);
		}
		e.setAttribute("file", jsonArray.toString());

		e.setAttribute("accept_filenames", excel.isAcceptingFilenames() ? "Y" : "N");
		e.setAttribute("accept_field", excel.getAcceptingField());
		e.setAttribute("accept_stepname", excel.getAcceptingStepName());


		// sheet tab
		jsonArray = new JSONArray();
		for (int i = 0; i < excel.getSheetName().length; i++) {
			JSONObject json3 = new JSONObject();
			json3.put("name", excel.getSheetName()[i]);
			json3.put("startrow", excel.getStartRow()[i]);
			json3.put("startcol", excel.getStartColumn()[i]);
			jsonArray.add(json3);
		}
		e.setAttribute("sheets", jsonArray.toString());

		// content tab
		e.setAttribute("header", excel.startsWithHeader() ? "Y" : "N");
		e.setAttribute("noempty", excel.ignoreEmptyRows() ? "Y" : "N");
		e.setAttribute("stoponempty", excel.stopOnEmpty() ? "Y" : "N");
		e.setAttribute("limit", String.valueOf(excel.getRowLimit()));
		e.setAttribute("encoding", excel.getEncoding());
		e.setAttribute("add_to_result_filenames", excel.isAddResultFile() ? "Y" : "N");


		//error tab
		e.setAttribute("strict_types", excel.isStrictTypes() ? "Y" : "N");
		e.setAttribute("error_ignored", excel.isErrorIgnored() ? "Y" : "N");
		e.setAttribute("error_line_skipped", excel.isErrorLineSkipped() ? "Y" : "N");
		e.setAttribute("bad_line_files_destination_directory", excel.getWarningFilesDestinationDirectory());
		e.setAttribute("bad_line_files_extension", excel.getBadLineFilesExtension());
		e.setAttribute("error_line_files_destination_directory", excel.getErrorFilesDestinationDirectory());
		e.setAttribute("error_line_files_extension", excel.getErrorFilesExtension());
		e.setAttribute("line_number_files_destination_directory", excel.getLineNumberFilesDestinationDirectory());
		e.setAttribute("line_number_files_extension", excel.getLineNumberFilesExtension());

		//fields tab
		jsonArray = new JSONArray();
		for (int i = 0; i < excel.getField().length; i++) {
			ExcelInputField field = excel.getField()[i];
			JSONObject json2 = new JSONObject();
			json2.put("name", field.getName());
			json2.put("type", field.getTypeDesc());
			json2.put("length", field.getLength());
			json2.put("precision", field.getPrecision());
			json2.put("trim_type", field.getTrimTypeCode());
			json2.put("repeat", field.isRepeated() ? "Y" : "N");
			json2.put("format", field.getFormat());
			json2.put("currency", field.getCurrencySymbol());
			json2.put("decimal", field.getDecimalSymbol());
			json2.put("group", field.getGroupSymbol());
			jsonArray.add(json2);
		}
		e.setAttribute("fields", jsonArray.toString());

		// other tab
		e.setAttribute("filefield", excel.getFileField());
		e.setAttribute("sheetfield", excel.getSheetField());
		e.setAttribute("sheetrownumfield", excel.getSheetRowNumberField());
		e.setAttribute("rownumfield", excel.getRowNumberField());
		e.setAttribute("shortFileFieldName", excel.getShortFileNameField());
		e.setAttribute("extensionFieldName", excel.getExtensionField());
		e.setAttribute("pathFieldName", excel.getPathField());
		e.setAttribute("sizeFieldName", excel.getSizeField());
		e.setAttribute("hiddenFieldName", excel.isHiddenField());
		e.setAttribute("lastModificationTimeFieldName", excel.getLastModificationDateField());
		e.setAttribute("uriNameFieldName", excel.getUriField());
		e.setAttribute("rootUriNameFieldName", excel.getRootUriField());

		return e;
	}
}
