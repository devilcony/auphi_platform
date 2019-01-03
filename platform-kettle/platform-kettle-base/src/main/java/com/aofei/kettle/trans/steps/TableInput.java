package com.aofei.kettle.trans.steps;

import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.StringEscapeHelper;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.errorhandling.StreamInterface;
import org.pentaho.di.trans.steps.tableinput.TableInputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

@Component("TableInput")
@Scope("prototype")
public class TableInput extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		TableInputMeta tableInputMeta = (TableInputMeta) stepMetaInterface;

		String con = cell.getAttribute( "connection" );
		tableInputMeta.setDatabaseMeta(DatabaseMeta.findDatabase( databases, con ));
		tableInputMeta.setSQL(StringEscapeHelper.decode(cell.getAttribute( "sql" )));
		tableInputMeta.setRowLimit(cell.getAttribute("limit"));

		tableInputMeta.setExecuteEachInputRow("Y".equalsIgnoreCase(cell.getAttribute( "execute_each_row" )));
		tableInputMeta.setVariableReplacementActive("Y".equalsIgnoreCase(cell.getAttribute( "variables_active" )));
		tableInputMeta.setLazyConversionActive("Y".equalsIgnoreCase(cell.getAttribute( "lazy_conversion_active" )));

		String lookupFromStepname = cell.getAttribute("lookup");
		StreamInterface infoStream = tableInputMeta.getStepIOMeta().getInfoStreams().get(0);
		infoStream.setSubject(lookupFromStepname);
	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface, CurrentUserResponse user) throws Exception {
		TableInputMeta tableInputMeta = (TableInputMeta) stepMetaInterface;

		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

		e.setAttribute("connection", tableInputMeta.getDatabaseMeta() == null ? "" : tableInputMeta.getDatabaseMeta().getName());
		e.setAttribute("sql", StringEscapeHelper.encode(tableInputMeta.getSQL()));
		e.setAttribute("limit", tableInputMeta.getRowLimit());
		e.setAttribute("execute_each_row", tableInputMeta.isExecuteEachInputRow() ? "Y" : "N");
		e.setAttribute("variables_active", tableInputMeta.isVariableReplacementActive() ? "Y" : "N");
		e.setAttribute("lazy_conversion_active", tableInputMeta.isLazyConversionActive() ? "Y" : "N");
		StreamInterface infoStream = tableInputMeta.getStepIOMeta().getInfoStreams().get( 0 );
		e.setAttribute("lookup", infoStream.getStepname());

		return e;
	}

}
