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
import org.pentaho.di.trans.steps.exceloutput.ExcelField;
import org.pentaho.di.trans.steps.exceloutput.ExcelOutputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("ExcelOutput")
@Scope("prototype")
public class ExcelOutput extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		ExcelOutputMeta excelOutputMeta = (ExcelOutputMeta) stepMetaInterface;

		// file tab
		excelOutputMeta.setFileName(cell.getAttribute("file_name"));
		excelOutputMeta.setCreateParentFolder("Y".equalsIgnoreCase(cell.getAttribute("create_parent_folder")));
		excelOutputMeta.setDoNotOpenNewFileInit("Y".equalsIgnoreCase(cell.getAttribute("do_not_open_newfile_init")));
		excelOutputMeta.setExtension(cell.getAttribute("file_extention"));
		excelOutputMeta.setStepNrInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_add_stepnr")));
		excelOutputMeta.setDateInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_add_date")));
		excelOutputMeta.setTimeInFilename("Y".equalsIgnoreCase(cell.getAttribute("file_add_time")));
		excelOutputMeta.setSpecifyFormat("Y".equalsIgnoreCase(cell.getAttribute("SpecifyFormat")));
		excelOutputMeta.setDateTimeFormat(cell.getAttribute("date_time_format"));
		excelOutputMeta.setAddToResultFiles("Y".equalsIgnoreCase(cell.getAttribute("add_to_result_filenames")));

		// content tab
		excelOutputMeta.setAppend("Y".equalsIgnoreCase(cell.getAttribute("append")));
		excelOutputMeta.setHeaderEnabled("Y".equalsIgnoreCase(cell.getAttribute("header")));
		excelOutputMeta.setFooterEnabled("Y".equalsIgnoreCase(cell.getAttribute("footer")));
		excelOutputMeta.setEncoding(cell.getAttribute("encoding"));
		excelOutputMeta.setSplitEvery(Const.toInt(cell.getAttribute("file_split"), 0));
		excelOutputMeta.setSheetname(cell.getAttribute("sheetname"));
		excelOutputMeta.setProtectSheet("Y".equalsIgnoreCase(cell.getAttribute("protect_sheet")));
		excelOutputMeta.setPassword(cell.getAttribute("password"));

		excelOutputMeta.setAutoSizeColums("Y".equalsIgnoreCase(cell.getAttribute("autosizecolums")));
		excelOutputMeta.setNullIsBlank("Y".equalsIgnoreCase(cell.getAttribute("nullisblank")));
		excelOutputMeta.setUseTempFiles("Y".equalsIgnoreCase(cell.getAttribute("usetempfiles")));
		excelOutputMeta.setTempDirectory(cell.getAttribute("tempdirectory"));
		excelOutputMeta.setTemplateEnabled("Y".equalsIgnoreCase(cell.getAttribute("template_enabled")));
		excelOutputMeta.setTemplateFileName(cell.getAttribute("template_filename"));
		excelOutputMeta.setTemplateAppend("Y".equalsIgnoreCase(cell.getAttribute("template_append")));

		// format tab
		excelOutputMeta.setHeaderFontName(Const.toInt(cell.getAttribute("header_font_name"), 0));
		excelOutputMeta.setHeaderFontSize(Const.toInt(cell.getAttribute("header_font_size"), ExcelOutputMeta.DEFAULT_FONT_SIZE) + "");
		excelOutputMeta.setHeaderFontBold("Y".equalsIgnoreCase(cell.getAttribute("header_font_bold")));
		excelOutputMeta.setHeaderFontItalic("Y".equalsIgnoreCase(cell.getAttribute("header_font_italic")));
		excelOutputMeta.setHeaderFontUnderline(Const.toInt(cell.getAttribute("header_font_underline"), 0));
		excelOutputMeta.setHeaderFontOrientation(Const.toInt(cell.getAttribute("header_font_orientation"), 0));
		excelOutputMeta.setHeaderFontColor(Const.toInt(cell.getAttribute("header_font_color"), 0));
		excelOutputMeta.setHeaderBackGroundColor(Const.toInt(cell.getAttribute("header_background_color"), 0));
		excelOutputMeta.setHeaderRowHeight(cell.getAttribute("header_row_height"));
		excelOutputMeta.setHeaderAlignment(Const.toInt(cell.getAttribute("header_alignment"), 0));
		excelOutputMeta.setHeaderImage(cell.getAttribute("header_image"));

		excelOutputMeta.setRowFontName(Const.toInt(cell.getAttribute("row_font_name"), 0));
		excelOutputMeta.setRowFontSize(Const.toInt(cell.getAttribute("row_font_size"), ExcelOutputMeta.DEFAULT_FONT_SIZE) + "");
		excelOutputMeta.setRowFontColor(Const.toInt(cell.getAttribute("row_font_color"), 0));
		excelOutputMeta.setRowBackGroundColor(Const.toInt(cell.getAttribute("row_background_color"), 0));


		// fields tab
		String fields = cell.getAttribute("fields");
		JSONArray jsonArray = JSONArray.fromObject(fields);
		excelOutputMeta.allocate(jsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			ExcelField ef = new ExcelField();
			ef.setName(jsonObject.optString("name"));
			ef.setType(jsonObject.optString("type"));
			ef.setFormat(jsonObject.optString("format"));
			excelOutputMeta.getOutputFields()[i] = ef;
		}

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		ExcelOutputMeta excelOutputMeta = (ExcelOutputMeta) stepMetaInterface;

		// file tab
		e.setAttribute("file_name", excelOutputMeta.getFileName());
		e.setAttribute("create_parent_folder", excelOutputMeta.isCreateParentFolder() ? "Y" : "N");
		e.setAttribute("do_not_open_newfile_init", excelOutputMeta.isDoNotOpenNewFileInit() ? "Y" : "N");
		e.setAttribute("file_extention", excelOutputMeta.getExtension());
		e.setAttribute("file_add_stepnr", excelOutputMeta.isStepNrInFilename() ? "Y" : "N");
		e.setAttribute("file_add_date", excelOutputMeta.isDateInFilename() ? "Y" : "N");
		e.setAttribute("file_add_time", excelOutputMeta.isTimeInFilename() ? "Y" : "N");
		e.setAttribute("SpecifyFormat", excelOutputMeta.isSpecifyFormat() ? "Y" : "N");
		e.setAttribute("date_time_format", excelOutputMeta.getDateTimeFormat());
		e.setAttribute("add_to_result_filenames", excelOutputMeta.isAddToResultFiles() ? "Y" : "N");

		// content tab
		e.setAttribute("append", excelOutputMeta.isAppend() ? "Y" : "N");
		e.setAttribute("header", excelOutputMeta.isHeaderEnabled() ? "Y" : "N");
		e.setAttribute("footer", excelOutputMeta.isFooterEnabled() ? "Y" : "N");
		e.setAttribute("encoding", excelOutputMeta.getEncoding());
		e.setAttribute("file_split", excelOutputMeta.getSplitEvery() + "");
		e.setAttribute("sheetname", excelOutputMeta.getSheetname());
		e.setAttribute("protect_sheet", excelOutputMeta.isSheetProtected() ? "Y" : "N");
		e.setAttribute("password", excelOutputMeta.getPassword());

		e.setAttribute("autosizecolums", excelOutputMeta.isAutoSizeColums() ? "Y" : "N");
		e.setAttribute("nullisblank", excelOutputMeta.isNullBlank() ? "Y" : "N");
		e.setAttribute("usetempfiles", excelOutputMeta.isUseTempFiles() ? "Y" : "N");
		e.setAttribute("tempdirectory", excelOutputMeta.getTempDirectory());
		e.setAttribute("template_enabled", excelOutputMeta.isTemplateEnabled() ? "Y" : "N");
		e.setAttribute("template_filename", excelOutputMeta.getTemplateFileName());
		e.setAttribute("template_append", excelOutputMeta.isTemplateAppend() ? "Y" : "N");

		// format tab
		e.setAttribute("header_font_name", excelOutputMeta.getHeaderFontName() + "");
		e.setAttribute("header_font_size", "" + excelOutputMeta.getHeaderFontSize());
		e.setAttribute("header_font_bold", excelOutputMeta.isHeaderFontBold() ? "Y" : "N");
		e.setAttribute("header_font_italic", excelOutputMeta.isHeaderFontItalic() ? "Y" : "N");
		e.setAttribute("header_font_underline", excelOutputMeta.getHeaderFontUnderline() + "");
		e.setAttribute("header_font_orientation", excelOutputMeta.getHeaderFontOrientation() + "");
		e.setAttribute("header_font_color", excelOutputMeta.getHeaderFontColor() + "");
		e.setAttribute("header_background_color", excelOutputMeta.getHeaderBackGroundColor() + "");
		e.setAttribute("header_row_height", Const.NVL(excelOutputMeta.getHeaderRowHeight(), ""));
		e.setAttribute("header_alignment", excelOutputMeta.getHeaderAlignment() + "");
		e.setAttribute("header_image", excelOutputMeta.getHeaderImage());

		e.setAttribute("row_font_name", excelOutputMeta.getRowFontName() + "");
		e.setAttribute("row_font_size", excelOutputMeta.getRowFontSize() + "");
		e.setAttribute("row_font_color", excelOutputMeta.getRowFontColor() + "");
		e.setAttribute("row_background_color", excelOutputMeta.getRowBackGroundColor() + "");


		// fields tab
		JSONArray jsonArray = new JSONArray();
		ExcelField[] excelFields = excelOutputMeta.getOutputFields();
		for(int j=0; j<excelFields.length; j++) {
			ExcelField ef = excelFields[j];

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", ef.getName());
			jsonObject.put("type", ef.getTypeDesc());
			jsonObject.put("format", ef.getFormat());
			jsonArray.add(jsonObject);
		}
		e.setAttribute("fields", jsonArray.toString());


		return e;
	}

}
