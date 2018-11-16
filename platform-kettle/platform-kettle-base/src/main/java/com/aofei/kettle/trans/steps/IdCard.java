package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.ReflectUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("IdCard")
@Scope("prototype")
public class IdCard extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		ReflectUtils.set(stepMetaInterface, "fieldToUse", cell.getAttribute("fieldToUse"));
		ReflectUtils.set(stepMetaInterface, "resultField", cell.getAttribute("resultField"));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("fieldToUse", ReflectUtils.getString(stepMetaInterface, "fieldToUse"));
		e.setAttribute("resultField", ReflectUtils.getString(stepMetaInterface, "resultField"));

		return e;
	}

}
