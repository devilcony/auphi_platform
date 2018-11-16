package com.aofei.kettle.trans.steps.excelinput;

import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.vfs2.FileObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KCellType;
import org.pentaho.di.core.spreadsheet.KSheet;
import org.pentaho.di.core.spreadsheet.KWorkbook;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;
import org.pentaho.di.trans.steps.excelinput.WorkbookFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RestController
@RequestMapping("/excelinput")
@Api(tags = "Transformation转换 - Excel输入 - 接口api")
public class ExcelInputController {

	@ApiOperation(value = "获取字段信息", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "EXCEL环节名称", paramType="query", dataType = "string")
	})
	@RequestMapping(method=RequestMethod.POST, value="/fields")
	protected @ResponseBody List fields(@RequestParam String graphXml, @RequestParam String stepName) throws Exception{
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);
		ExcelInputMeta input = (ExcelInputMeta) stepMeta.getStepMetaInterface();

		RowMetaInterface fields = new RowMeta();
		FileInputList fileList = input.getFileList(transMeta);
		for (FileObject file : fileList.getFiles()) {
			try {
				KWorkbook workbook = WorkbookFactory.getWorkbook(input.getSpreadSheetType(),
						KettleVFS.getFilename(file), input.getEncoding());
				processingWorkbook(fields, input, workbook);
				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ArrayList list = new ArrayList();
		if (fields.size() > 0) {
			for (int j = 0; j < fields.size(); j++) {
				ValueMetaInterface field = fields.getValueMeta(j);

				LinkedHashMap rec = new LinkedHashMap();
				rec.put("name", field.getName());
				rec.put("type", field.getTypeDesc());
				rec.put("trim_type", "none");
				rec.put("repeat", "N");

				list.add(rec);
			}
		}

		return list;
	}

	@ApiOperation(value = "获取EXCEL sheet信息", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "EXCEL环节名称", paramType="query", dataType = "string")
	})
	@RequestMapping(method=RequestMethod.POST, value="/sheets")
	protected @ResponseBody List sheets(@RequestParam String graphXml, @RequestParam String stepName) throws Exception{
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);
		ExcelInputMeta input = (ExcelInputMeta) stepMeta.getStepMetaInterface();

		List<String> sheetnames = new ArrayList<String>();
		FileInputList fileList = input.getFileList(transMeta);
		for (FileObject fileObject : fileList.getFiles()) {
			try {
				KWorkbook workbook = WorkbookFactory.getWorkbook(input.getSpreadSheetType(),
						KettleVFS.getFilename(fileObject), input.getEncoding());

				int nrSheets = workbook.getNumberOfSheets();
				for (int j = 0; j < nrSheets; j++) {
					KSheet sheet = workbook.getSheet(j);
					String sheetname = sheet.getName();

					if (Const.indexOfString(sheetname, sheetnames) < 0) {
						sheetnames.add(sheetname);
					}
				}

				workbook.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		ArrayList list = new ArrayList();
		for(String sheetname : sheetnames) {
			LinkedHashMap rec = new LinkedHashMap();
			rec.put("name", sheetname);
			rec.put("startrow", "0");
			rec.put("startcol", "0");

			list.add(rec);
		}

		return list;
	}

	private void processingWorkbook(RowMetaInterface fields, ExcelInputMeta info, KWorkbook workbook) throws KettlePluginException {
		int nrSheets = workbook.getNumberOfSheets();
		for (int j = 0; j < nrSheets; j++) {
			KSheet sheet = workbook.getSheet(j);

			// See if it's a selected sheet:
			int sheetIndex;
			if (info.readAllSheets()) {
				sheetIndex = 0;
			} else {
				sheetIndex = Const.indexOfString(sheet.getName(), info.getSheetName());
			}
			if (sheetIndex >= 0) {
				// We suppose it's the complete range we're looking for...
				//
				int rownr = 0;
				int startcol = 0;

				if (info.readAllSheets()) {
					if (info.getStartColumn().length == 1) {
						startcol = info.getStartColumn()[0];
					}
					if (info.getStartRow().length == 1) {
						rownr = info.getStartRow()[0];
					}
				} else {
					rownr = info.getStartRow()[sheetIndex];
					startcol = info.getStartColumn()[sheetIndex];
				}

				boolean stop = false;
				for (int colnr = startcol; !stop; colnr++) {
					try {
						String fieldname = null;
						int fieldtype = ValueMetaInterface.TYPE_NONE;

						KCell cell = sheet.getCell(colnr, rownr);
						if (cell == null) {
							stop = true;
						} else {
							if (cell.getType() != KCellType.EMPTY) {
								// We found a field.
								fieldname = cell.getContents();
							}

							// System.out.println("Fieldname = "+fieldname);

							KCell below = sheet.getCell(colnr, rownr + 1);

							if (below != null) {
								if (below.getType() == KCellType.BOOLEAN) {
									fieldtype = ValueMetaInterface.TYPE_BOOLEAN;
								} else if (below.getType() == KCellType.DATE) {
									fieldtype = ValueMetaInterface.TYPE_DATE;
								} else if (below.getType() == KCellType.LABEL) {
									fieldtype = ValueMetaInterface.TYPE_STRING;
								} else if (below.getType() == KCellType.NUMBER) {
									fieldtype = ValueMetaInterface.TYPE_NUMBER;
								} else {
									fieldtype = ValueMetaInterface.TYPE_STRING;
								}
							} else {
								fieldtype = ValueMetaInterface.TYPE_STRING;
							}

							if (Utils.isEmpty(fieldname)) {
								stop = true;
							} else {
								if (fieldtype != ValueMetaInterface.TYPE_NONE) {
									ValueMetaInterface field = ValueMetaFactory.createValueMeta(fieldname, fieldtype);
									fields.addValueMeta(field);
								}
							}
						}
					} catch (ArrayIndexOutOfBoundsException aioobe) {
						// System.out.println("index out of bounds at column "+colnr+" :
						// "+aioobe.toString());
						stop = true;
					}
				}
			}
		}
	}

}
