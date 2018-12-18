package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.excelwriter.ExcelWriterStepField;
import org.pentaho.di.trans.steps.excelwriter.ExcelWriterStepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("TypeExitExcelWriterStep")
@Scope("prototype")
public class TypeExitExcelWriterStep extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		ExcelWriterStepMeta ewsm = (ExcelWriterStepMeta) stepMetaInterface;

		ewsm.setFileName(cell.getAttribute("file_name"));
		ewsm.setExtension(cell.getAttribute("file_extention"));
		ewsm.setStreamingData("Y".equalsIgnoreCase(cell.getAttribute("file_stream_data")));
		ewsm.setSplitEvery(Const.toInt(cell.getAttribute("file_splitevery"), 0));
		ewsm.setStepNrInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_split")));
		ewsm.setDateInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_add_date")));
		ewsm.setTimeInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_add_time")));
		ewsm.setSpecifyFormat("Y".equalsIgnoreCase(cell.getAttribute("file_SpecifyFormat")));
		ewsm.setDateTimeFormat(cell.getAttribute("file_date_time_format"));
		ewsm.setIfFileExists(cell.getAttribute("if_file_exists"));
		ewsm.setDoNotOpenNewFileInit("Y".equalsIgnoreCase(cell.getAttribute("do_not_open_newfile_init")));
		ewsm.setAddToResultFiles("Y".equalsIgnoreCase(cell.getAttribute("add_to_result_filenames")));

		ewsm.setSheetname(cell.getAttribute("sheetname"));
		ewsm.setMakeSheetActive("Y".equalsIgnoreCase(cell.getAttribute("makeSheetActive")));
		ewsm.setIfSheetExists(cell.getAttribute("if_sheet_exists"));
		ewsm.setProtectSheet("Y".equalsIgnoreCase(cell.getAttribute("protect_sheet")));
		ewsm.setProtectedBy(cell.getAttribute("protected_by"));
		ewsm.setPassword(cell.getAttribute("password"));

		ewsm.setTemplateEnabled("Y".equalsIgnoreCase(cell.getAttribute("template_enabled")));
		ewsm.setTemplateFileName(cell.getAttribute("template_filename"));
		ewsm.setTemplateSheetEnabled("Y".equalsIgnoreCase(cell.getAttribute("template_sheet_enabled")));
		ewsm.setTemplateSheetName(cell.getAttribute("template_sheetname"));

		//tab2
		ewsm.setStartingCell(cell.getAttribute("startingCell"));
		ewsm.setRowWritingMethod(cell.getAttribute("rowWritingMethod"));
		ewsm.setHeaderEnabled("Y".equalsIgnoreCase(cell.getAttribute("header")));
		ewsm.setFooterEnabled("Y".equalsIgnoreCase(cell.getAttribute("footer")));
		ewsm.setAutoSizeColums("Y".equalsIgnoreCase(cell.getAttribute("autosizecolums")));
		ewsm.setForceFormulaRecalculation("Y".equalsIgnoreCase(cell.getAttribute("forceFormulaRecalculation")));
		ewsm.setLeaveExistingStylesUnchanged("Y".equalsIgnoreCase(cell.getAttribute("leaveExistingStylesUnchanged")));

		ewsm.setAppendLines("Y".equalsIgnoreCase(cell.getAttribute("appendLines")));
		ewsm.setAppendOffset(Const.toInt(cell.getAttribute("appendOffset"), 0));
		ewsm.setAppendEmpty(Const.toInt(cell.getAttribute("appendEmpty"), 0));
		ewsm.setAppendOmitHeader("Y".equalsIgnoreCase(cell.getAttribute("appendOmitHeader")));

		String fieldsString = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fieldsString);

		ewsm.allocate(jsonArray.size());
		for(int j=0; j<jsonArray.size(); j++) {
			JSONObject jsonObject = jsonArray.getJSONObject(j);

			ExcelWriterStepField field = new ExcelWriterStepField();
			field.setName(jsonObject.optString("name"));
			field.setType(jsonObject.optString("type"));
			field.setFormat(jsonObject.optString("format"));
			field.setTitle(jsonObject.optString("title"));
			field.setTitleStyleCell(jsonObject.optString("titleStyleCell"));
			field.setStyleCell(jsonObject.optString("styleCell"));
			field.setCommentField(jsonObject.optString("commentField"));
			field.setCommentAuthorField(jsonObject.optString("commentAuthorField"));
			field.setFormula("Y".equalsIgnoreCase(jsonObject.optString("formula")));
			field.setHyperlinkField(jsonObject.optString("hyperlinkField"));

			ewsm.getOutputFields()[j] = field;
		}

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		ExcelWriterStepMeta ewsm = (ExcelWriterStepMeta) stepMetaInterface;

		e.setAttribute("file_name", ewsm.getFileName());
		e.setAttribute("file_extention", ewsm.getExtension());
		e.setAttribute("file_stream_data", ewsm.isStreamingData() ? "Y" : "N");
		e.setAttribute("file_splitevery", ewsm.getSplitEvery() + "");
		e.setAttribute("file_split", ewsm.isStepNrInFilename() ? "Y" : "N");
		e.setAttribute("file_add_date", ewsm.isDateInFilename() ? "Y" : "N");
		e.setAttribute("file_add_time", ewsm.isTimeInFilename() ? "Y" : "N");
		e.setAttribute("file_SpecifyFormat", ewsm.isSpecifyFormat() ? "Y" : "N");
		e.setAttribute("file_date_time_format", ewsm.getDateTimeFormat());
		e.setAttribute("if_file_exists", ewsm.getIfFileExists());
		e.setAttribute("do_not_open_newfile_init", ewsm.isDoNotOpenNewFileInit() ? "Y" : "N");
		e.setAttribute("add_to_result_filenames", ewsm.isAddToResultFiles() ? "Y" : "N");

		e.setAttribute("sheetname", ewsm.getSheetname());
		e.setAttribute("makeSheetActive", ewsm.isMakeSheetActive() ? "Y" : "N");
		e.setAttribute("if_sheet_exists", ewsm.getIfSheetExists());
		e.setAttribute("protect_sheet", ewsm.isSheetProtected() ? "Y" : "N");
		e.setAttribute("protected_by", ewsm.getProtectedBy());
		e.setAttribute("password", ewsm.getPassword());

		e.setAttribute("template_enabled", ewsm.isTemplateEnabled() ? "Y" : "N");
		e.setAttribute("template_filename", ewsm.getTemplateFileName());
		e.setAttribute("template_sheet_enabled", ewsm.isTemplateSheetEnabled() ? "Y" : "N");
		e.setAttribute("template_sheetname", ewsm.getTemplateSheetName());


		//tab2
		e.setAttribute("startingCell", ewsm.getStartingCell());
		e.setAttribute("rowWritingMethod", ewsm.getRowWritingMethod());
		e.setAttribute("header", ewsm.isHeaderEnabled() ? "Y" : "N");
		e.setAttribute("footer", ewsm.isFooterEnabled() ? "Y" : "N");
		e.setAttribute("autosizecolums", ewsm.isAutoSizeColums() ? "Y" : "N");
		e.setAttribute("forceFormulaRecalculation", ewsm.isForceFormulaRecalculation() ? "Y" : "N");
		e.setAttribute("leaveExistingStylesUnchanged", ewsm.isLeaveExistingStylesUnchanged() ? "Y" : "N");

		e.setAttribute("appendLines", ewsm.isAppendLines() ? "Y" : "N");
		e.setAttribute("appendOffset", ewsm.getAppendOffset() + "");	//抵消行数
		e.setAttribute("appendEmpty", ewsm.getAppendEmpty() + "");
		e.setAttribute("appendOmitHeader", ewsm.isAppendOmitHeader() ? "Y" : "N");

		JSONArray jsonArray = new JSONArray();
		ExcelWriterStepField[] fields = ewsm.getOutputFields();
		for(int j=0; j<fields.length; j++) {
			ExcelWriterStepField field = fields[j];
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("name", field.getName());
			jsonObject.put("type", field.getTypeDesc());
			jsonObject.put("format", field.getFormat());
			jsonObject.put("title", field.getTitle());
			jsonObject.put("titleStyleCell", field.getTitleStyleCell());
			jsonObject.put("styleCell", field.getStyleCell());
			jsonObject.put("commentField", field.getCommentField());
			jsonObject.put("commentAuthorField", field.getCommentAuthorField());
			jsonObject.put("formula", field.isFormula() ? "Y" : "N");
			jsonObject.put("hyperlinkField", field.getHyperlinkField());
			jsonArray.add(jsonObject);
		}
		e.setAttribute("fields", jsonArray.toString());

		return e;
	}

}
