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
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.file.BaseFileField;
import org.pentaho.di.trans.steps.fileinput.text.TextFileFilter;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("TextFileInput")
@Scope("prototype")
public class TextFileInput extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases,
					   IMetaStore metaStore) throws Exception {
		TextFileInputMeta textFileInputMeta = (TextFileInputMeta) stepMetaInterface;

		// file tab
		String fileName = cell.getAttribute("fileName");
		JSONArray jsonArray = JSONArray.fromObject(fileName);

		String filter = cell.getAttribute("filter");
		JSONArray filterjsonArray = JSONArray.fromObject(filter);

		String fields = cell.getAttribute("inputFields");
		JSONArray fieldsjsonArray = JSONArray.fromObject(fields);

		textFileInputMeta.allocate(jsonArray.size(), fieldsjsonArray.size(), filterjsonArray.size());
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			textFileInputMeta.inputFiles.fileName[i] = jsonObject.optString("name");
			textFileInputMeta.inputFiles.fileMask[i] = jsonObject.optString("filemask");
			textFileInputMeta.inputFiles.excludeFileMask[i] = jsonObject.optString("excludeFileMask");
			textFileInputMeta.inputFiles.fileRequired[i] = jsonObject.optString("fileRequired");
			textFileInputMeta.inputFiles.includeSubFolders[i] = jsonObject.optString("includeSubFolders");
		}

		textFileInputMeta.inputFiles.acceptingFilenames = "Y".equalsIgnoreCase(cell.getAttribute("acceptingFilenames"));
		textFileInputMeta.inputFiles.passingThruFields = "Y".equalsIgnoreCase(cell.getAttribute("passingThruFields"));
		textFileInputMeta.inputFiles.acceptingField = cell.getAttribute("acceptingField");
		textFileInputMeta.inputFiles.acceptingStepName = cell.getAttribute("acceptingStepName");

		// content tab
		textFileInputMeta.content.fileType = cell.getAttribute("fileType");
		textFileInputMeta.content.separator = cell.getAttribute("separator");
		textFileInputMeta.content.enclosure = cell.getAttribute("enclosure");
		textFileInputMeta.content.escapeCharacter = cell.getAttribute("escapeCharacter");
		textFileInputMeta.content.breakInEnclosureAllowed = "Y".equalsIgnoreCase(cell.getAttribute("breakInEnclosureAllowed"));

		textFileInputMeta.content.header = "Y".equalsIgnoreCase(cell.getAttribute("header"));
		textFileInputMeta.content.nrHeaderLines = Const.toInt(cell.getAttribute("nrHeaderLines"), 1);
		textFileInputMeta.content.footer = "Y".equalsIgnoreCase(cell.getAttribute("footer"));
		textFileInputMeta.content.nrFooterLines = Const.toInt(cell.getAttribute("nrFooterLines"), 1);

		textFileInputMeta.content.lineWrapped = "Y".equalsIgnoreCase(cell.getAttribute("lineWrapped"));
		textFileInputMeta.content.nrWraps = Const.toInt(cell.getAttribute("nrWraps"), 1);

		textFileInputMeta.content.layoutPaged = "Y".equalsIgnoreCase(cell.getAttribute("layoutPaged"));
		textFileInputMeta.content.nrLinesDocHeader = Const.toInt(cell.getAttribute("nrLinesDocHeader"), 1);
		textFileInputMeta.content.nrLinesPerPage = Const.toInt(cell.getAttribute("nrLinesPerPage"), 1);

		textFileInputMeta.content.fileCompression = cell.getAttribute("fileCompression");
		textFileInputMeta.content.noEmptyLines = "Y".equalsIgnoreCase(cell.getAttribute("noEmptyLines"));

		textFileInputMeta.content.includeFilename = "Y".equalsIgnoreCase(cell.getAttribute("includeFilename"));
		textFileInputMeta.content.filenameField = cell.getAttribute("filenameField");

		textFileInputMeta.content.includeRowNumber = "Y".equalsIgnoreCase(cell.getAttribute("includeRowNumber"));
		textFileInputMeta.content.fileFormat = cell.getAttribute("fileFormat");
		textFileInputMeta.content.encoding = cell.getAttribute("encoding");
		textFileInputMeta.content.length = cell.getAttribute("length");

		textFileInputMeta.content.rowNumberByFile = "Y".equalsIgnoreCase(cell.getAttribute("rowNumberByFile"));
		textFileInputMeta.content.rowNumberField = cell.getAttribute("rowNumberField");
		textFileInputMeta.content.rowLimit = Const.toInt(cell.getAttribute("rowLimit"), 0);

		textFileInputMeta.content.dateFormatLenient = "Y".equalsIgnoreCase(cell.getAttribute("dateFormatLenient"));
		textFileInputMeta.content.dateFormatLocale = EnvUtil.createLocale(cell.getAttribute("dateFormatLocale"));
		textFileInputMeta.inputFiles.isaddresult = "Y".equalsIgnoreCase(cell.getAttribute("isaddresult"));

		// error tab
		textFileInputMeta.errorHandling.errorIgnored = "Y".equalsIgnoreCase(cell.getAttribute("errorIgnored"));
		textFileInputMeta.errorHandling.skipBadFiles = "Y".equalsIgnoreCase(cell.getAttribute("skipBadFiles"));

		textFileInputMeta.errorHandling.fileErrorField = cell.getAttribute("fileErrorField");
		textFileInputMeta.errorHandling.fileErrorMessageField = cell.getAttribute("fileErrorMessageField");

		textFileInputMeta.setErrorLineSkipped("Y".equalsIgnoreCase(cell.getAttribute("errorLineSkipped")));
		textFileInputMeta.setErrorCountField(cell.getAttribute("errorCountField"));
		textFileInputMeta.setErrorFieldsField(cell.getAttribute("errorFieldsField"));
		textFileInputMeta.setErrorTextField(cell.getAttribute("errorTextField"));

		textFileInputMeta.errorHandling.warningFilesDestinationDirectory = cell.getAttribute("warningFilesDestinationDirectory");
		textFileInputMeta.errorHandling.warningFilesExtension = cell.getAttribute("warningFilesExtension");
		textFileInputMeta.errorHandling.errorFilesDestinationDirectory = cell.getAttribute("errorFilesDestinationDirectory");
		textFileInputMeta.errorHandling.errorFilesExtension = cell.getAttribute("errorFilesExtension");
		textFileInputMeta.errorHandling.lineNumberFilesDestinationDirectory = cell.getAttribute("lineNumberFilesDestinationDirectory");
		textFileInputMeta.errorHandling.lineNumberFilesExtension = cell.getAttribute("lineNumberFilesExtension");

		// filter tab
		TextFileFilter[] filterFields = new TextFileFilter[filterjsonArray.size()];
		for (int i = 0; i < filterjsonArray.size(); i++) {
			JSONObject jsonObject = filterjsonArray.getJSONObject(i);
			TextFileFilter filterField = new TextFileFilter();
			filterField.setFilterString(jsonObject.optString("filterString"));
			filterField.setFilterPosition(Const.toInt(jsonObject.optString("filterPosition"), 0));
			filterField.setFilterLastLine("Y".equalsIgnoreCase(jsonObject.optString("filterLastLine")));
			filterField.setFilterPositive("Y".equalsIgnoreCase(jsonObject.optString("filterPositive")));
			filterFields[i] = filterField;
		}
		textFileInputMeta.setFilter(filterFields);

		// fields tab
		textFileInputMeta.inputFields = new BaseFileField[fieldsjsonArray.size()];
		for (int i = 0; i < fieldsjsonArray.size(); i++) {
			JSONObject jsonObject = fieldsjsonArray.getJSONObject(i);
			BaseFileField field = new BaseFileField();

			field.setName(jsonObject.optString("name"));
			field.setType(Const.toInt(jsonObject.optString("type"), 0));
			field.setFormat(jsonObject.optString("format"));
			field.setPosition(Const.toInt(jsonObject.optString("position"), 0));
			field.setLength(Const.toInt(jsonObject.optString("length"), -1));
			field.setPrecision(Const.toInt(jsonObject.optString("precision"), -1));
			field.setCurrencySymbol(jsonObject.optString("currency"));
			field.setDecimalSymbol(jsonObject.optString("decimal"));
			field.setGroupSymbol(jsonObject.optString("group"));
			field.setTrimType(Const.toInt(jsonObject.optString("trim_type"), 0));
			field.setNullString(jsonObject.optString("nullif"));
			field.setIfNullValue(jsonObject.optString("ifnull"));
			field.setRepeated("Y".equalsIgnoreCase(jsonObject.optString("repeat")));
			textFileInputMeta.inputFields[i] = field;
		}
		// other tab
		textFileInputMeta.additionalOutputFields.shortFilenameField = (cell.getAttribute("shortFileFieldName"));
		textFileInputMeta.additionalOutputFields.pathField = (cell.getAttribute("pathFieldName"));
		textFileInputMeta.additionalOutputFields.hiddenField = (cell.getAttribute("hiddenFieldName"));
		textFileInputMeta.additionalOutputFields.lastModificationField = (cell.getAttribute("lastModificationTimeFieldName"));
		textFileInputMeta.additionalOutputFields.uriField = (cell.getAttribute("uriNameFieldName"));
		textFileInputMeta.additionalOutputFields.rootUriField = (cell.getAttribute("rootUriNameFieldName"));
		textFileInputMeta.additionalOutputFields.extensionField = ((cell.getAttribute("extensionFieldName")));
		textFileInputMeta.additionalOutputFields.sizeField = (cell.getAttribute("sizeFieldName"));


	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		TextFileInputMeta textFileInputMeta = (TextFileInputMeta) stepMetaInterface;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		// file tab
		String[] fileName = textFileInputMeta.inputFiles.fileName;
		String[] fileMask = textFileInputMeta.inputFiles.fileMask;
		String[] excludeFileMask = textFileInputMeta.inputFiles.excludeFileMask;
		String[] fileRequired = textFileInputMeta.inputFiles.fileRequired;
		String[] includeSubFolders = textFileInputMeta.inputFiles.includeSubFolders;

		JSONArray jsonArray = new JSONArray();
		for (int j = 0; j < fileName.length; j++) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", fileName[j]);
			jsonObject.put("showName", com.aofei.base.common.Const.getUserFilePath(user.getOrganizerId(),fileName[j]));
			jsonObject.put("filemask", fileMask[j]);
			jsonObject.put("excludeFileMask", excludeFileMask[j]);
			jsonObject.put("fileRequired", fileRequired[j]);
			jsonObject.put("includeSubFolders", includeSubFolders[j]);
			jsonArray.add(jsonObject);
		}
		e.setAttribute("fileName", jsonArray.toString());

		e.setAttribute("acceptingFilenames", textFileInputMeta.inputFiles.acceptingFilenames ? "Y" : "N");
		e.setAttribute("passingThruFields", textFileInputMeta.inputFiles.passingThruFields ? "Y" : "N");

		StepMeta acceptingStep = textFileInputMeta.getAcceptingStep();
		e.setAttribute("acceptingStepName", acceptingStep == null ? "" : acceptingStep.getName());
		e.setAttribute("acceptingField", textFileInputMeta.inputFiles.acceptingField);

		// content tab
		e.setAttribute("fileType", textFileInputMeta.content.fileType);
		e.setAttribute("separator", textFileInputMeta.content.separator);
		e.setAttribute("enclosure", textFileInputMeta.content.enclosure);
		e.setAttribute("escapeCharacter", textFileInputMeta.content.escapeCharacter);
		e.setAttribute("breakInEnclosureAllowed", textFileInputMeta.content.breakInEnclosureAllowed ? "Y" : "N");

		e.setAttribute("header", textFileInputMeta.content.header ? "Y" : "N");
		e.setAttribute("nrHeaderLines", textFileInputMeta.content.nrHeaderLines + "");

		e.setAttribute("footer", textFileInputMeta.content.footer ? "Y" : "N");
		e.setAttribute("nrFooterLines", textFileInputMeta.content.nrFooterLines + "");

		e.setAttribute("lineWrapped", textFileInputMeta.content.lineWrapped ? "Y" : "N");
		e.setAttribute("nrWraps", textFileInputMeta.content.nrWraps + "");

		e.setAttribute("layoutPaged", textFileInputMeta.content.layoutPaged ? "Y" : "N");
		e.setAttribute("nrLinesPerPage", textFileInputMeta.content.nrLinesPerPage + "");
		e.setAttribute("nrLinesDocHeader", textFileInputMeta.content.nrLinesDocHeader + "");

		e.setAttribute("fileCompression", textFileInputMeta.content.fileCompression);
		e.setAttribute("noEmptyLines", textFileInputMeta.content.noEmptyLines ? "Y" : "N");

		e.setAttribute("includeFilename", textFileInputMeta.content.includeFilename ? "Y" : "N");
		e.setAttribute("filenameField", textFileInputMeta.content.filenameField);

		e.setAttribute("includeRowNumber", textFileInputMeta.content.includeRowNumber ? "Y" : "N");

		e.setAttribute("fileFormat", textFileInputMeta.content.fileFormat);
		e.setAttribute("encoding", textFileInputMeta.getEncoding());
		e.setAttribute("length", textFileInputMeta.content.length);

		e.setAttribute("rowNumberByFile", textFileInputMeta.content.rowNumberByFile ? "Y" : "N");
		e.setAttribute("rowNumberField", textFileInputMeta.content.rowNumberField);
		e.setAttribute("rowLimit", textFileInputMeta.content.rowLimit + "");

		e.setAttribute("dateFormatLenient", textFileInputMeta.content.dateFormatLenient ? "Y" : "N");
		e.setAttribute("dateFormatLocale", textFileInputMeta.content.dateFormatLocale.toString());

		e.setAttribute("isaddresult", textFileInputMeta.inputFiles.isaddresult ? "Y" : "N");

		// error tab
		e.setAttribute("errorIgnored", textFileInputMeta.errorHandling.errorIgnored ? "Y" : "N");
		e.setAttribute("skipBadFiles", textFileInputMeta.errorHandling.skipBadFiles ? "Y" : "N");

		e.setAttribute("fileErrorField", textFileInputMeta.errorHandling.fileErrorField);
		e.setAttribute("fileErrorMessageField", textFileInputMeta.errorHandling.fileErrorMessageField);
		e.setAttribute("errorLineSkipped", textFileInputMeta.isErrorLineSkipped() ? "Y" : "N");
		e.setAttribute("errorCountField", textFileInputMeta.getErrorCountField());
		e.setAttribute("errorFieldsField", textFileInputMeta.getErrorFieldsField());
		e.setAttribute("errorTextField", textFileInputMeta.getErrorTextField());

		e.setAttribute("warningFilesDestinationDirectory",
				textFileInputMeta.errorHandling.warningFilesDestinationDirectory);
		e.setAttribute("warningFilesExtension", textFileInputMeta.errorHandling.warningFilesExtension);
		e.setAttribute("errorFilesDestinationDirectory",
				textFileInputMeta.errorHandling.errorFilesDestinationDirectory);
		e.setAttribute("errorFilesExtension", textFileInputMeta.errorHandling.errorFilesExtension);
		e.setAttribute("lineNumberFilesDestinationDirectory",
				textFileInputMeta.errorHandling.lineNumberFilesDestinationDirectory);
		e.setAttribute("lineNumberFilesExtension", textFileInputMeta.errorHandling.lineNumberFilesExtension);

		// filter tab
		TextFileFilter[] filters = textFileInputMeta.getFilter();
		jsonArray = new JSONArray();
		if (filters != null) {
			for (TextFileFilter filter : filters) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("filterString", filter.getFilterString());
				jsonObject.put("filterPosition", filter.getFilterPosition());
				jsonObject.put("filterLastLine", filter.isFilterLastLine());
				jsonObject.put("filterPositive", filter.isFilterPositive());
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("filter", jsonArray.toString());

		// fields tab
		jsonArray = new JSONArray();
		BaseFileField[] inputFields = textFileInputMeta.inputFields;
		if (inputFields != null) {
			for (BaseFileField inputField : inputFields) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", inputField.getName());
				jsonObject.put("position", inputField.getPosition());
				jsonObject.put("type", inputField.getTypeDesc());
				jsonObject.put("format", inputField.getFormat());
				jsonObject.put("currencySymbol", inputField.getCurrencySymbol());
				jsonObject.put("decimalSymbol", inputField.getDecimalSymbol());
				jsonObject.put("groupSymbol", inputField.getGroupSymbol());
				jsonObject.put("nullString", inputField.getNullString());
				jsonObject.put("ifNullValue", inputField.getIfNullValue());
				jsonObject.put("trimtype", inputField.getTrimTypeDesc());
				jsonObject.put("repeat", inputField.isRepeated() ? "Y" : "N");
				if (inputField.getLength() != -1)
					jsonObject.put("length", inputField.getLength());
				if (inputField.getPrecision() != -1)
					jsonObject.put("precision", inputField.getPrecision());
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("inputFields", jsonArray.toString());

		e.setAttribute("shortFileFieldName", textFileInputMeta.additionalOutputFields.shortFilenameField);
		e.setAttribute("extensionFieldName", textFileInputMeta.additionalOutputFields.extensionField);

		e.setAttribute("pathFieldName", textFileInputMeta.additionalOutputFields.pathField);
		e.setAttribute("sizeField", textFileInputMeta.additionalOutputFields.sizeField);

		e.setAttribute("hiddenFieldName", textFileInputMeta.additionalOutputFields.hiddenField);
		e.setAttribute("lastModificationTimeFieldName", textFileInputMeta.additionalOutputFields.lastModificationField);
		e.setAttribute("uriNameFieldName", textFileInputMeta.additionalOutputFields.uriField);
		e.setAttribute("rootUriNameFieldName", textFileInputMeta.additionalOutputFields.rootUriField);

		return e;
	}

}
