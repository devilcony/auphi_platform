package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.delay.DelayMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("Delay")
@Scope("prototype")
public class Delay extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		DelayMeta delayMeta = (DelayMeta) stepMetaInterface;
		delayMeta.setTimeOut(cell.getAttribute("timeout"));
		delayMeta.setScaleTimeCode(Const.toInt(cell.getAttribute("scaletime"), 1));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		DelayMeta delayMeta = (DelayMeta) stepMetaInterface;
		e.setAttribute("timeout", delayMeta.getTimeOut());
		e.setAttribute("scaletime", String.valueOf(delayMeta.getScaleTimeCode()));
		return e;
	}

}
