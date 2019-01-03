package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.blockingstep.BlockingStepMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("BlockingStep")
@Scope("prototype")
public class BlockingStep extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		BlockingStepMeta bsm = (BlockingStepMeta) stepMetaInterface;
		bsm.setPassAllRows("Y".equalsIgnoreCase(cell.getAttribute("pass_all_rows")));
		bsm.setDirectory(cell.getAttribute("directory"));
		bsm.setPrefix(cell.getAttribute("prefix"));
		bsm.setCacheSize(Const.toInt(cell.getAttribute("cache_size"), BlockingStepMeta.CACHE_SIZE));
		bsm.setCompress("Y".equalsIgnoreCase(cell.getAttribute("compress")));
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		BlockingStepMeta bsm = (BlockingStepMeta) stepMetaInterface;
		e.setAttribute("pass_all_rows", bsm.isPassAllRows() ? "Y" : "N");
		e.setAttribute("directory", bsm.getDirectory());
		e.setAttribute("prefix", bsm.getPrefix());
		e.setAttribute("cache_size", bsm.getCacheSize() + "");
		e.setAttribute("compress", bsm.getCompress() ? "Y" : "N");

		return e;
	}

}
