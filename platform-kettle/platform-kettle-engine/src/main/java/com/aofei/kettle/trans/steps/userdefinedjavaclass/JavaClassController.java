package com.aofei.kettle.trans.steps.userdefinedjavaclass;

import com.aofei.kettle.PluginFactory;
import com.aofei.kettle.base.GraphCodec;
import com.aofei.kettle.utils.NodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassDef;
import org.pentaho.di.trans.steps.userdefinedjavaclass.UserDefinedJavaClassMeta;
import org.pentaho.di.ui.trans.steps.userdefinedjavaclass.UserDefinedJavaClassCodeSnippits;
import org.pentaho.di.ui.trans.steps.userdefinedjavaclass.UserDefinedJavaClassCodeSnippits.Category;
import org.pentaho.di.ui.trans.steps.userdefinedjavaclass.UserDefinedJavaClassCodeSnippits.Snippit;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value="/javaclass")
@Api(tags = "Transformation转换 - Java类 - 接口api")
public class JavaClassController {

	@ApiOperation(value = "获取左侧树结构", httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "graphXml", value = "图形信息", paramType="query", dataType = "string"),
		@ApiImplicitParam(name = "stepName", value = "Java环节名称", paramType="query", dataType = "string")
	})
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST, value="/tree")
	protected List tree(@RequestParam String graphXml, @RequestParam String stepName) throws Exception {
		GraphCodec codec = (GraphCodec) PluginFactory.getBean(GraphCodec.TRANS_CODEC);
		TransMeta transMeta = (TransMeta) codec.decode(graphXml);
		StepMeta stepMeta = transMeta.findStep(stepName);

		Class PKG = UserDefinedJavaClassMeta.class;
		UserDefinedJavaClassMeta input = (UserDefinedJavaClassMeta) stepMeta.getStepMetaInterface();

		int count = 1;
		ArrayList result = new ArrayList();

		Map classItem = NodeUtils.createFolder("class_node_id", BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.Classes.Label" ), true);
		count = modifyTabTree(input, classItem, count);


		Map codeSnippits = NodeUtils.createFolder(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.Snippits.Label" ));
		count = buildSnippitsTree((List)codeSnippits.get("children"), count);


		Map itemInput = NodeUtils.createFolder(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.InputFields.Label" ));
		Map itemInfo = NodeUtils.createFolder(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.InfoFields.Label" ));
		Map itemOutput = NodeUtils.createFolder(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.OutputFields.Label" ));
		count = populateFieldsTree(transMeta, stepMeta, (List)itemInput.get("children"), (List)itemInfo.get("children"), (List)itemOutput.get("children"), count);

//		SearchFieldsProgress op = new SearchFieldsProgress( transMeta, stepMeta, true );
//		op.run();
//		RowMetaInterface rowMetaInterface = op.getFields();
//
//		if(rowMetaInterface.size() > 0) {
//			for (int i = 0; i < rowMetaInterface.size(); i++) {
//				ValueMetaInterface valueMeta = rowMetaInterface.getValueMeta(i);
//				Map item = NodeUtils.createLeaf(count++, valueMeta.getName(), "arrowOrange");
//				((List)itemInput.get("children")).add(item);
//			}
//		} else {
//			Map itemWaitFieldsIn1 = NodeUtils.createLeaf(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.GettingFields.Label" ));
//			((List)itemInput.get("children")).add(itemWaitFieldsIn1);
//		}
//
//		Map itemWaitFieldsIn2 = NodeUtils.createLeaf(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.GettingFields.Label" ));
//		Map itemWaitFieldsIn3 = NodeUtils.createLeaf(count++, BaseMessages.getString( PKG, "UserDefinedJavaClassDialog.GettingFields.Label" ));
//		((List)itemInfo.get("children")).add(itemWaitFieldsIn2);
//		((List)itemOutput.get("children")).add(itemWaitFieldsIn3);

		result.add(classItem);
		result.add(codeSnippits);
		result.add(itemInput);
		result.add(itemInfo);
		result.add(itemOutput);

		return result;
	}

	private int modifyTabTree(UserDefinedJavaClassMeta input, Map classItem, int count) {
		ArrayList javacodes = new ArrayList();

		List<UserDefinedJavaClassDef> definitions = input.getDefinitions();
		for (UserDefinedJavaClassDef definition : definitions) {
			Map javacode = NodeUtils.createLeaf(count++, definition.getClassName(), "activeScript");
			javacodes.add(javacode);
		}
		classItem.put("children", javacodes);

		return count;
	}

	private int buildSnippitsTree(List codeSnippits, int count) throws KettleXMLException {
//		ArrayList categories = new ArrayList();

		HashMap<Category, Map> categoryTreeItems = new HashMap<Category, Map>();
		for (Category cat : Category.values()) {
			Map category = NodeUtils.createFolder(count++, cat.getDescription(), "underGreen");
			codeSnippits.add(category);

			categoryTreeItems.put(cat, category);
		}

		Collection<Snippit> snippits = UserDefinedJavaClassCodeSnippits.getSnippitsHelper().getSnippits();
		for (Snippit snippit : snippits) {
			Map itemGroup = categoryTreeItems.get(snippit.category);

			Map itemSnippit = NodeUtils.createLeaf(count++, snippit.name, "arrowGreen");
			itemSnippit.put("data", snippit.code);

			((List)itemGroup.get("children")).add(itemSnippit);
		}

		return count;
	}

	private int populateFieldsTree(TransMeta transMeta, StepMeta stepMeta, List itemInput, List itemInfo, List itemOutput, int count) throws KettleStepException {
		RowMetaInterface inputRowMeta = transMeta.getPrevStepFields( stepMeta );
		if(inputRowMeta.size() > 0) {
			itemInput.clear();
			for ( int i = 0; i < inputRowMeta.size(); i++ ) {
	            ValueMetaInterface v = inputRowMeta.getValueMeta( i );
	            Map itemField = NodeUtils.createFolder(count++, v.getName(), "arrowOrange");
	            itemInput.add(itemField);

	            Map itemFieldGet = NodeUtils.createLeaf(count++, String.format( "get%s()", v.getTypeDesc() ));
	            Map itemFieldSet = NodeUtils.createLeaf(count++, "setValue()");

	            ((List)itemField.get("children")).add(itemFieldGet);
	            ((List)itemField.get("children")).add(itemFieldSet);
			}
		}

		RowMetaInterface infoRowMeta = transMeta.getPrevInfoFields( stepMeta );
		if(infoRowMeta.size() > 0) {
			itemInfo.clear();
			for ( int i = 0; i < infoRowMeta.size(); i++ ) {
	            ValueMetaInterface v = infoRowMeta.getValueMeta( i );
	            Map infoField = NodeUtils.createFolder(count++, v.getName(), "arrowOrange");
	            itemInfo.add(infoField);

	            Map itemFieldGet = NodeUtils.createLeaf(count++, String.format( "get%s()", v.getTypeDesc() ));
	            Map itemFieldSet = NodeUtils.createLeaf(count++, "setValue()");

	            ((List)infoField.get("children")).add(itemFieldGet);
	            ((List)infoField.get("children")).add(itemFieldSet);
			}
		}
		try {
			RowMetaInterface outputRowMeta = transMeta.getThisStepFields( stepMeta, null, inputRowMeta.clone() );
			if(outputRowMeta.size() > 0) {
				itemOutput.clear();
				for ( int i = 0; i < outputRowMeta.size(); i++ ) {
		            ValueMetaInterface v = outputRowMeta.getValueMeta( i );
		            Map itemField = NodeUtils.createFolder(count++, v.getName(), "arrowOrange");
		            itemOutput.add(itemField);

		            Map itemFieldGet = NodeUtils.createLeaf(count++, String.format( "get%s()", v.getTypeDesc() ));
		            Map itemFieldSet = NodeUtils.createLeaf(count++, "setValue()");

		            ((List)itemField.get("children")).add(itemFieldGet);
		            ((List)itemField.get("children")).add(itemFieldSet);
				}
			}
		} catch(Exception e) {
			System.out.println("Java脚本错误!");
		}

		return count;
	}
}
