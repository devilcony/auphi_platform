package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.abort.AbortMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("Abort")
@Scope("prototype")
public class Abort extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		AbortMeta abortMeta = (AbortMeta) stepMetaInterface;
		abortMeta.setRowThreshold(cell.getAttribute("row_threshold"));
		abortMeta.setMessage(cell.getAttribute("message"));
		abortMeta.setAlwaysLogRows("Y".equals(cell.getAttribute("always_log_rows")));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		AbortMeta abortMeta = (AbortMeta) stepMetaInterface;

		e.setAttribute("row_threshold", abortMeta.getRowThreshold());
		e.setAttribute("message", abortMeta.getMessage());
		e.setAttribute("always_log_rows", abortMeta.isAlwaysLogRows() ? "Y" : "N");

		return e;
	}

}
