package com.aofei.kettle.trans.steps.scriptvalues_mod;

import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import com.aofei.kettle.trans.steps.RowGenerator;
import com.aofei.kettle.utils.*;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.mozilla.javascript.*;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.tools.ToolErrorReporter;
import org.pentaho.di.compatibility.Row;
import org.pentaho.di.compatibility.Value;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesAddedFunctions;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesMetaMod;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesModDummy;
import org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesScript;
import org.pentaho.di.ui.trans.steps.scriptvalues_mod.ScriptValuesHelp;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping(value="/script")
@Api(tags = "Transformation转换 - JS脚本 - 接口api")
public class JavaScriptController {

	@ApiOperation(value = "获取左侧树结构", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "JavaScript环节名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/tree")
	protected List tree(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);
		ScriptValuesMetaMod input = (ScriptValuesMetaMod) stepMeta.getStepMetaInterface();

		int count = 1;
		ArrayList jsonArray = new ArrayList();

		Map transScripts = NodeUtils.createFolder("script_node_id", BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.TransformScript.Label" ), true);
		Map transCons = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.TansformConstant.Label" ));
		Map transFuncs = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.TransformFunctions.Label" ));
		Map transInputs = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.InputFields.Label" ));
		Map transOutputs = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.OutputFields.Label" ));


		// fill transforms
		for(String name : input.getJSScriptNames()) {
			Map code = NodeUtils.createLeaf(count++, name, "activeScript");
			((List)transScripts.get("children")).add(code);
		}

		// fill constants
		for(String text : Arrays.asList("SKIP_TRANSFORMATION", "ERROR_TRANSFORMATION",  "CONTINUE_TRANSFORMATION")) {
			Map constItem = NodeUtils.createLeaf(count++, text, "arrowGreen");
			((List)transCons.get("children")).add(constItem);
		}

		// fill functions
		Hashtable<String, String> hatFunctions = scVHelp.getFunctionList();
	    Vector<String> v = new Vector<String>( hatFunctions.keySet() );
	    Collections.sort( v );

	    Map stringFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.StringFunctions.Label" ), "underGreen");
	    Map numberFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.NumericFunctions.Label" ), "underGreen");
	    Map dateFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.DateFunctions.Label" ), "underGreen");
	    Map logicFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.LogicFunctions.Label" ), "underGreen");
	    Map specialFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.SpecialFunctions.Label" ), "underGreen");
	    Map fileFunc = NodeUtils.createFolder(count++, BaseMessages.getString( ScriptValuesMetaMod.class, "ScriptValuesDialogMod.FileFunctions.Label" ), "underGreen");

	    ((List)transFuncs.get("children")).add(stringFunc);
	    ((List)transFuncs.get("children")).add(numberFunc);
	    ((List)transFuncs.get("children")).add(dateFunc);
	    ((List)transFuncs.get("children")).add(logicFunc);
	    ((List)transFuncs.get("children")).add(specialFunc);
	    ((List)transFuncs.get("children")).add(fileFunc);

	    for ( String strFunction : v ) {
	    	String strFunctionType = hatFunctions.get( strFunction );
	    	int iFunctionType = Integer.valueOf( strFunctionType ).intValue();
	    	Map item = NodeUtils.createLeaf(count++, strFunction, "arrowGreen");

			switch (iFunctionType) {
			case ScriptValuesAddedFunctions.STRING_FUNCTION:
				((List)stringFunc.get("children")).add(item);
				break;
			case ScriptValuesAddedFunctions.NUMERIC_FUNCTION:
				((List)numberFunc.get("children")).add(item);
				break;
			case ScriptValuesAddedFunctions.DATE_FUNCTION:
				((List)dateFunc.get("children")).add(item);
				break;
			case ScriptValuesAddedFunctions.LOGIC_FUNCTION:
				((List)logicFunc.get("children")).add(item);
				break;
			case ScriptValuesAddedFunctions.SPECIAL_FUNCTION:
				((List)specialFunc.get("children")).add(item);
				break;
			case ScriptValuesAddedFunctions.FILE_FUNCTION:
				((List)fileFunc.get("children")).add(item);
				break;
			default:
				break;
			}
	    }
	    // end fill functions
	    try {
		    // fill input fields
		    SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, true );
			op.run();
			RowMetaInterface rowMetaInterface = op.getFields();

			for (int i = 0; i < rowMetaInterface.size(); i++) {
				ValueMetaInterface valueMeta = rowMetaInterface.getValueMeta(i);
				Map item = NodeUtils.createLeaf(count++, valueMeta.getName(), "arrowOrange");
				((List)transInputs.get("children")).add(item);
			}
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }

		jsonArray.add(transScripts);
		jsonArray.add(transCons);
		jsonArray.add(transFuncs);
		jsonArray.add(transInputs);
		jsonArray.add(transOutputs);

		return jsonArray;
	}

	private static ScriptValuesHelp scVHelp;
	static {
		try {
			scVHelp = new ScriptValuesHelp("jsFunctionHelp.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@ApiOperation(value = "获取所有的JavaScript变量", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "JavaScript环节名称", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "scriptName", value = "脚本名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/getVariables")
	protected void getVariables(@RequestParam String graphXml, @RequestParam String stepName,  @RequestParam String scriptName) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		ScriptValuesMetaMod input = (ScriptValuesMetaMod) transMeta.findStep(stepName).getStepMetaInterface();

		Context jscx = ContextFactory.getGlobal().enterContext();
		jscx.setOptimizationLevel(-1);
		Scriptable jsscope = jscx.initStandardObjects(null, false);

		String strStartScript = null, scr = null;
		for(ScriptValuesScript script : input.getJSScripts()) {
			Scriptable jsR = Context.toObject(script.getScript(), jsscope);
			jsscope.put(script.getScriptName(), jsscope, jsR);

			if(script.isStartScript())
				strStartScript = script.getScript();
			if(script.getScriptName().equals(scriptName))
				scr = script.getScript();
		}

		jsscope.put( "_TransformationName_", jsscope, stepName);
		RowMetaInterface rowMeta = transMeta.getPrevStepFields( stepName );
		if(rowMeta != null) {

			ScriptValuesModDummy dummyStep = new ScriptValuesModDummy(rowMeta, transMeta.getStepFields(stepName));
			Scriptable jsvalue = Context.toObject(dummyStep, jsscope);
			jsscope.put("_step_", jsscope, jsvalue);

			if (input.getAddClasses() != null) {
				for (int i = 0; i < input.getAddClasses().length; i++) {
					Object jsOut = Context.javaToJS(input.getAddClasses()[i].getAddObject(), jsscope);
					ScriptableObject.putProperty(jsscope, input.getAddClasses()[i].getJSName(), jsOut);
				}
			}

			Context.javaToJS(ScriptValuesAddedFunctions.class, jsscope);
			((ScriptableObject) jsscope).defineFunctionProperties(ScriptValuesAddedFunctions.jsFunctionList, ScriptValuesAddedFunctions.class, ScriptableObject.DONTENUM);

			jsscope.put("SKIP_TRANSFORMATION", jsscope, Integer.valueOf(1));
			jsscope.put("ABORT_TRANSFORMATION", jsscope, Integer.valueOf(-1));
			jsscope.put("ERROR_TRANSFORMATION", jsscope, Integer.valueOf(-2));
			jsscope.put("CONTINUE_TRANSFORMATION", jsscope, Integer.valueOf(0));

			Object[] row = new Object[rowMeta.size()];
			Scriptable jsRowMeta = Context.toObject(rowMeta, jsscope);
			jsscope.put("rowMeta", jsscope, jsRowMeta);
			for (int i = 0; i < rowMeta.size(); i++) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(i);
				Object valueData = null;

				if (valueMeta.isDate()) {
					valueData = new Date();
				}
				if (valueMeta.isString()) {
					valueData = "test value test value test value test value test value " + "test value test value test value test value test value";
				}
				if (valueMeta.isInteger()) {
					valueData = Long.valueOf(0L);
				}
				if (valueMeta.isNumber()) {
					valueData = new Double(0.0);
				}
				if (valueMeta.isBigNumber()) {
					valueData = BigDecimal.ZERO;
				}
				if (valueMeta.isBoolean()) {
					valueData = Boolean.TRUE;
				}
				if (valueMeta.isBinary()) {
					valueData = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, };
				}

				if (valueMeta.isStorageBinaryString()) {
					valueMeta.setStorageType(ValueMetaInterface.STORAGE_TYPE_NORMAL);
				}

				row[i] = valueData;

				if (input.isCompatible()) {
					Value value = valueMeta.createOriginalValue(valueData);
					Scriptable jsarg = Context.toObject(value, jsscope);
					jsscope.put(valueMeta.getName(), jsscope, jsarg);
				} else {
					Scriptable jsarg = Context.toObject(valueData, jsscope);
					jsscope.put(valueMeta.getName(), jsscope, jsarg);
				}
			}


			Scriptable jsval = Context.toObject(Value.class, jsscope);
			jsscope.put("Value", jsscope, jsval);

			if (input.isCompatible()) {
				Row v2Row = RowMeta.createOriginalRow(rowMeta, row);
				Scriptable jsV2Row = Context.toObject(v2Row, jsscope);
				jsscope.put("row", jsscope, jsV2Row);
			} else {
				Scriptable jsRow = Context.toObject(row, jsscope);
				jsscope.put("row", jsscope, jsRow);
			}

			if (strStartScript != null) {
				jscx.evaluateString(jsscope, strStartScript, "trans_Start", 1, null);
			}


			Script evalScript = jscx.compileString(scr, "script", 1, null);
			evalScript.exec(jscx, jsscope);

			CompilerEnvirons evn = new CompilerEnvirons();
			evn.setOptimizationLevel(-1);
			evn.setGeneratingSource(true);
			evn.setGenerateDebugInfo(true);
			ErrorReporter errorReporter = new ToolErrorReporter(false);
			Parser p = new Parser(evn, errorReporter);
			ScriptNode tree = p.parse(scr, "", 0);
			new NodeTransformer().transform(tree);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < tree.getParamAndVarCount(); i++) {
				String varname = tree.getParamOrVarName(i);
				if (!varname.equalsIgnoreCase("row") && !varname.equalsIgnoreCase("trans_Status")) {
					int type = ValueMetaInterface.TYPE_STRING;
					int length = -1, precision = -1;
					Object result = jsscope.get(varname, jsscope);
					if (result != null) {
						String classname = result.getClass().getName();
						if (classname.equalsIgnoreCase("java.lang.Byte")) {
							// MAX = 127
							type = ValueMetaInterface.TYPE_INTEGER;
							length = 3;
							precision = 0;
						} else if (classname.equalsIgnoreCase("java.lang.Integer")) {
							// MAX = 2147483647
							type = ValueMetaInterface.TYPE_INTEGER;
							length = 9;
							precision = 0;
						} else if (classname.equalsIgnoreCase("java.lang.Long")) {
							// MAX = 9223372036854775807
							type = ValueMetaInterface.TYPE_INTEGER;
							length = 18;
							precision = 0;
						} else if (classname.equalsIgnoreCase("java.lang.Double")) {
							type = ValueMetaInterface.TYPE_NUMBER;
							length = 16;
							precision = 2;

						} else if (classname.equalsIgnoreCase("org.mozilla.javascript.NativeDate") || classname.equalsIgnoreCase("java.util.Date")) {
							type = ValueMetaInterface.TYPE_DATE;
						} else if (classname.equalsIgnoreCase("java.lang.Boolean")) {
							type = ValueMetaInterface.TYPE_BOOLEAN;
						}
					}

					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", varname);
					jsonObject.put("rename", "");
					jsonObject.put("type", ValueMeta.getTypeDesc(type));
					jsonObject.put("length", length >= 0 ? String.valueOf(length) : "");
					jsonObject.put("precision", precision >= 0 ? String.valueOf(precision) : "");
					jsonObject.put("replace", (rowMeta.indexOfValue(varname) >= 0) ? "Y" : "N");
					jsonArray.add(jsonObject);
				}

			}

			JsonUtils.response(jsonArray);
		}
	}

	@ApiOperation(value = "生成测试数据", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "JavaScript环节名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/testData")
	protected void testData(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);

		RowMetaInterface rowMeta = transMeta.getPrevStepFields(stepName).clone();
		if (rowMeta != null) {
			RowGeneratorMeta genMeta = new RowGeneratorMeta();
			genMeta.setRowLimit("10");
			genMeta.allocate(rowMeta.size());
			for (int i = 0; i < rowMeta.size(); i++) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(i);
				if (valueMeta.isStorageBinaryString()) {
					valueMeta.setStorageType(ValueMetaInterface.STORAGE_TYPE_NORMAL);
				}
				genMeta.getFieldName()[i] = valueMeta.getName();
				genMeta.getFieldType()[i] = valueMeta.getTypeDesc();
				genMeta.getFieldLength()[i] = valueMeta.getLength();
				genMeta.getFieldPrecision()[i] = valueMeta.getPrecision();
				genMeta.getCurrency()[i] = valueMeta.getCurrencySymbol();
				genMeta.getDecimal()[i] = valueMeta.getDecimalSymbol();
				genMeta.getGroup()[i] = valueMeta.getGroupingSymbol();

				String string = null;
				switch (valueMeta.getType()) {
				case ValueMetaInterface.TYPE_DATE:
					genMeta.getFieldFormat()[i] = "yyyy/MM/dd HH:mm:ss";
					valueMeta.setConversionMask(genMeta.getFieldFormat()[i]);
					string = valueMeta.getString(new Date());
					break;
				case ValueMetaInterface.TYPE_STRING:
					string = "test value test value";
					break;
				case ValueMetaInterface.TYPE_INTEGER:
					genMeta.getFieldFormat()[i] = "#";
					valueMeta.setConversionMask(genMeta.getFieldFormat()[i]);
					string = valueMeta.getString(Long.valueOf(0L));
					break;
				case ValueMetaInterface.TYPE_NUMBER:
					genMeta.getFieldFormat()[i] = "#.#";
					valueMeta.setConversionMask(genMeta.getFieldFormat()[i]);
					string = valueMeta.getString(Double.valueOf(0.0D));
					break;
				case ValueMetaInterface.TYPE_BIGNUMBER:
					genMeta.getFieldFormat()[i] = "#.#";
					valueMeta.setConversionMask(genMeta.getFieldFormat()[i]);
					string = valueMeta.getString(BigDecimal.ZERO);
					break;
				case ValueMetaInterface.TYPE_BOOLEAN:
					string = valueMeta.getString(Boolean.TRUE);
					break;
				case ValueMetaInterface.TYPE_BINARY:
					string = valueMeta.getString(new byte[] { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, });
					break;
				default:
					break;
				}

				genMeta.getValue()[i] = string;
			}

			RowGenerator rg = (RowGenerator) PluginFactory.getBean("RowGenerator");
			Element e = rg.encode(genMeta);
			e.setAttribute("label", "## TEST DATA ##");
			e.setAttribute("ctype", "RowGenerator");
			e.setAttribute("copies", "1");

			JsonUtils.responseXml(mxUtils.getXml(e));
		}
	}

	@ApiOperation(value = "测试JavaScript脚本", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "JavaScript环节名称", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "rowGenerator", value = "JavaScript环节名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/test")
	protected void test(@RequestParam String graphXml, @RequestParam String stepName, @RequestParam String rowGenerator) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta scriptStep = transMeta.findStep(stepName);

		Document doc = mxUtils.parseXml(rowGenerator);

		RowGenerator rg = (RowGenerator) PluginFactory.getBean("RowGenerator");
		mxCell cell = new mxCell(doc.getDocumentElement());
		cell.setGeometry(new mxGeometry(0, 0, 40, 40));
		StepMeta genStep = rg.decodeStep(cell, null, null);
		RowGeneratorMeta genMeta = (RowGeneratorMeta) genStep.getStepMetaInterface();

		// Create a hop between both steps...
		//
		TransHopMeta hop = new TransHopMeta(genStep, scriptStep);

		// Generate a new test transformation...
		//
		TransMeta newMeta = new TransMeta();
		newMeta.setName(stepName + " - PREVIEW");
		newMeta.addStep(genStep);
		newMeta.addStep(scriptStep);
		newMeta.addTransHop(hop);

		int rowLimit = Const.toInt( genMeta.getRowLimit(), 10 );
		TransPreviewProgress tpp = new TransPreviewProgress(newMeta, new String[] { stepName}, new int[] { rowLimit });
		RowMetaInterface rowMeta = tpp.getPreviewRowsMeta(stepName);
		List<Object[]> rowsData = tpp.getPreviewRows(stepName);

		Font f = new Font("Arial", Font.PLAIN, 12);
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);

		if (rowMeta != null) {
			JSONObject jsonObject = new JSONObject();
			List<ValueMetaInterface> valueMetas = rowMeta.getValueMetaList();

			int width = 0;
			JSONArray columns = new JSONArray();
			JSONObject metaData = new JSONObject();
			JSONArray fields = new JSONArray();
			for (int i = 0; i < valueMetas.size(); i++) {
				ValueMetaInterface valueMeta = rowMeta.getValueMeta(i);
				fields.add(valueMeta.getName());
				String header = valueMeta.getComments() == null ? valueMeta.getName() : valueMeta.getComments();

				int hWidth = fm.stringWidth(header) + 10;
				width += hWidth;
				JSONObject column = new JSONObject();
				column.put("dataIndex", valueMeta.getName());
				column.put("header", header);
				column.put("width", hWidth);
				columns.add(column);
			}
			metaData.put("fields", fields);
			metaData.put("root", "firstRecords");

			JSONArray firstRecords = new JSONArray();
			for (int rowNr = 0; rowNr < rowsData.size(); rowNr++) {
				Object[] rowData = rowsData.get(rowNr);
				JSONObject row = new JSONObject();
				for (int colNr = 0; colNr < rowMeta.size(); colNr++) {
					String string = null;
					ValueMetaInterface valueMetaInterface;
					try {
						valueMetaInterface = rowMeta.getValueMeta(colNr);
						if (valueMetaInterface.isStorageBinaryString()) {
							Object nativeType = valueMetaInterface.convertBinaryStringToNativeType((byte[]) rowData[colNr]);
							string = valueMetaInterface.getStorageMetadata().getString(nativeType);
						} else {
							string = rowMeta.getString(rowData, colNr);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(!StringUtils.hasText(string))
						string = "&lt;null&gt;";

					ValueMetaInterface valueMeta = rowMeta.getValueMeta( colNr );
					row.put(valueMeta.getName(), string);
				}
				if(firstRecords.size() <= rowLimit) {
					firstRecords.add(row);
				}
			}

			jsonObject.put("metaData", metaData);
			jsonObject.put("columns", columns);
			jsonObject.put("firstRecords", firstRecords);
			jsonObject.put("width", width < 1000 ? width + 100 : 1000);

			JsonUtils.response(jsonObject);
		}
	}

}
